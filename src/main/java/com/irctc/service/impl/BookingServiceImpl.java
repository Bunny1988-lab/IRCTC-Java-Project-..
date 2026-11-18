package com.irctc.service.impl;

import com.irctc.dao.BookingDao;
import com.irctc.dao.PaymentDao;
import com.irctc.dao.RouteStopDao;
import com.irctc.dao.SeatDao;
import com.irctc.dao.TrainScheduleDao;
import com.irctc.dao.impl.BookingDaoImpl;
import com.irctc.dao.impl.PaymentDaoImpl;
import com.irctc.dao.impl.RouteStopDaoImpl;
import com.irctc.dao.impl.SeatDaoImpl;
import com.irctc.dao.impl.TrainScheduleDaoImpl;
import com.irctc.database.DatabaseConnection;
import com.irctc.enums.BookingStatus;
import com.irctc.enums.PassengerStatus;
import com.irctc.enums.PaymentMethod;
import com.irctc.enums.PaymentStatus;
import com.irctc.enums.TravelClass;
import com.irctc.exception.BookingException;
import com.irctc.exception.DatabaseException;
import com.irctc.exception.SeatUnavailableException;
import com.irctc.exception.ValidationException;
import com.irctc.model.Booking;
import com.irctc.model.Passenger;
import com.irctc.model.Payment;
import com.irctc.model.RouteStop;
import com.irctc.model.Seat;
import com.irctc.model.TrainSchedule;
import com.irctc.service.BookingService;
import com.irctc.util.FareCalculator;
import com.irctc.util.PnrGenerator;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of {@link BookingService} providing ACID transactional reservation workflows.
 */
public class BookingServiceImpl implements BookingService {

    private static final int MAX_PASSENGERS_PER_PNR = 6;

    private final BookingDao bookingDao;
    private final SeatDao seatDao;
    private final PaymentDao paymentDao;
    private final RouteStopDao routeStopDao;
    private final TrainScheduleDao scheduleDao;

    public BookingServiceImpl() {
        this(new BookingDaoImpl(), new SeatDaoImpl(), new PaymentDaoImpl(), new RouteStopDaoImpl(), new TrainScheduleDaoImpl());
    }

    public BookingServiceImpl(BookingDao bookingDao, SeatDao seatDao, PaymentDao paymentDao,
                              RouteStopDao routeStopDao, TrainScheduleDao scheduleDao) {
        this.bookingDao = bookingDao;
        this.seatDao = seatDao;
        this.paymentDao = paymentDao;
        this.routeStopDao = routeStopDao;
        this.scheduleDao = scheduleDao;
    }

    @Override
    public Booking bookTicket(Long userId, Long scheduleId, int fromStationId, int toStationId,
                              TravelClass travelClass, List<Passenger> passengers, PaymentMethod paymentMethod) {

        validateBookingRequest(userId, scheduleId, fromStationId, toStationId, travelClass, passengers);

        TrainSchedule schedule = scheduleDao.findById(scheduleId)
                .orElseThrow(() -> new ValidationException("Train schedule run not found for ID: " + scheduleId));

        int distanceKm = calculateDistance(schedule.getTrainId(), fromStationId, toStationId);
        BigDecimal farePerPassenger = FareCalculator.calculateFare(travelClass, distanceKm);
        BigDecimal totalFare = farePerPassenger.multiply(BigDecimal.valueOf(passengers.size()));

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Begin ACID Transaction

            // 1. Lock and fetch candidate seats using pessimistic row locking (FOR UPDATE)
            List<Seat> allocatedSeats = seatDao.findAvailableSeats(
                    scheduleId, schedule.getTrainId(), travelClass, passengers.size(), conn
            );

            if (allocatedSeats.size() < passengers.size()) {
                throw new SeatUnavailableException(String.format(
                        "Not enough confirmed seats available in class %s. Requested: %d, Available: %d.",
                        travelClass.getDisplayName(), passengers.size(), allocatedSeats.size()
                ));
            }

            // 2. Generate unique 10-digit PNR
            String pnr = PnrGenerator.generatePnr();

            // 3. Insert Master Booking Record
            Booking booking = new Booking();
            booking.setPnrNumber(pnr);
            booking.setUserId(userId);
            booking.setScheduleId(scheduleId);
            booking.setFromStationId(fromStationId);
            booking.setToStationId(toStationId);
            booking.setTravelClass(travelClass);
            booking.setTotalFare(totalFare);
            booking.setBookingStatus(BookingStatus.CONFIRMED);
            booking.setBookingDate(LocalDateTime.now());

            bookingDao.create(booking, conn);

            // 4. Assign Seats to Passengers and persist
            for (int i = 0; i < passengers.size(); i++) {
                Passenger p = passengers.get(i);
                Seat s = allocatedSeats.get(i);

                p.setSeatId(s.getSeatId());
                p.setCoachLabel(s.getCoachLabel());
                p.setSeatNumber(s.getSeatNumber());
                p.setAllocatedBerthType(s.getBerthType());
                p.setPassengerStatus(PassengerStatus.CONFIRMED);
                p.setFare(farePerPassenger);
            }

            bookingDao.addPassengers(booking.getBookingId(), passengers, conn);
            booking.setPassengers(passengers);

            // 5. Simulate Payment Gateway processing & ledger insertion
            String txnRef = "TXN_" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
            Payment payment = new Payment();
            payment.setBookingId(booking.getBookingId());
            payment.setTransactionReference(txnRef);
            payment.setPaymentMethod(paymentMethod != null ? paymentMethod : PaymentMethod.UPI);
            payment.setAmount(totalFare);
            payment.setPaymentStatus(PaymentStatus.SUCCESS);
            payment.setPaymentTime(LocalDateTime.now());

            paymentDao.create(payment, conn);

            // 6. Commit Transaction atomically
            conn.commit();
            return booking;

        } catch (SQLException e) {
            rollbackQuietly(conn);
            throw new BookingException("Database transaction failed during booking: " + e.getMessage(), e);
        } catch (Exception e) {
            rollbackQuietly(conn);
            if (e instanceof BookingException be) {
                throw be;
            }
            throw new BookingException("Ticket booking failed: " + e.getMessage(), e);
        } finally {
            resetAutoCommitAndClose(conn);
        }
    }

    @Override
    public Optional<Booking> getBookingByPnr(String pnrNumber) {
        if (pnrNumber == null || pnrNumber.isBlank()) {
            return Optional.empty();
        }
        return bookingDao.findByPnr(pnrNumber.trim());
    }

    @Override
    public List<Booking> getBookingsForUser(Long userId) {
        if (userId == null) {
            return List.of();
        }
        return bookingDao.findByUserId(userId);
    }

    @Override
    public boolean cancelBooking(String pnrNumber, String reason) {
        if (pnrNumber == null || pnrNumber.isBlank()) {
            return false;
        }

        Booking booking = bookingDao.findByPnr(pnrNumber.trim())
                .orElseThrow(() -> new ValidationException("Booking with PNR " + pnrNumber + " not found."));

        if (booking.getBookingStatus() == BookingStatus.CANCELLED) {
            throw new ValidationException("Booking with PNR " + pnrNumber + " is already cancelled.");
        }

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            boolean updated = bookingDao.updateStatus(booking.getBookingId(), BookingStatus.CANCELLED, conn);

            conn.commit();
            return updated;
        } catch (SQLException e) {
            rollbackQuietly(conn);
            throw new BookingException("Failed to cancel booking: " + e.getMessage(), e);
        } finally {
            resetAutoCommitAndClose(conn);
        }
    }

    private void validateBookingRequest(Long userId, Long scheduleId, int fromStationId, int toStationId,
                                        TravelClass travelClass, List<Passenger> passengers) {
        if (userId == null) {
            throw new ValidationException("User identification is required for booking.");
        }
        if (scheduleId == null) {
            throw new ValidationException("Train schedule ID is required.");
        }
        if (fromStationId == toStationId) {
            throw new ValidationException("Boarding and deboarding stations cannot be identical.");
        }
        if (travelClass == null) {
            throw new ValidationException("Travel service class must be specified.");
        }
        if (passengers == null || passengers.isEmpty()) {
            throw new ValidationException("At least one passenger is required to create a booking.");
        }
        if (passengers.size() > MAX_PASSENGERS_PER_PNR) {
            throw new ValidationException("Maximum " + MAX_PASSENGERS_PER_PNR + " passengers allowed per booking.");
        }

        for (Passenger p : passengers) {
            if (p.getFullName() == null || p.getFullName().trim().isEmpty()) {
                throw new ValidationException("Passenger name cannot be blank.");
            }
            if (p.getAge() <= 0 || p.getAge() > 120) {
                throw new ValidationException("Invalid passenger age for " + p.getFullName() + ". Must be between 1 and 120.");
            }
        }
    }

    private int calculateDistance(int trainId, int fromStationId, int toStationId) {
        Optional<RouteStop> stopFrom = routeStopDao.findStop(trainId, fromStationId);
        Optional<RouteStop> stopTo = routeStopDao.findStop(trainId, toStationId);

        if (stopFrom.isPresent() && stopTo.isPresent()) {
            int d = stopTo.get().getDistanceFromSourceKm() - stopFrom.get().getDistanceFromSourceKm();
            return Math.abs(d);
        }

        return 500; // Sensible default distance fallback if route stops are unseeded
    }

    private void rollbackQuietly(Connection conn) {
        if (conn != null) {
            try {
                conn.rollback();
            } catch (SQLException ignored) {
            }
        }
    }

    private void resetAutoCommitAndClose(Connection conn) {
        if (conn != null) {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException ignored) {
            }
            DatabaseConnection.closeQuietly(conn);
        }
    }
}
