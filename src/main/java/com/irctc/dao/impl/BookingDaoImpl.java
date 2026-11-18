package com.irctc.dao.impl;

import com.irctc.dao.BookingDao;
import com.irctc.database.DatabaseConnection;
import com.irctc.enums.BerthType;
import com.irctc.enums.BookingStatus;
import com.irctc.enums.Gender;
import com.irctc.enums.PassengerStatus;
import com.irctc.enums.TravelClass;
import com.irctc.exception.DatabaseException;
import com.irctc.model.Booking;
import com.irctc.model.Passenger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * JDBC implementation of {@link BookingDao}.
 */
public class BookingDaoImpl implements BookingDao {

    private static final String SELECT_BOOKING_BASE =
            "SELECT b.booking_id, b.pnr_number, b.user_id, b.schedule_id, " +
            "       b.from_station_id, b.to_station_id, b.travel_class, " +
            "       b.booking_date, b.total_fare, b.booking_status, " +
            "       t.train_number, t.train_name, ts.journey_date, " +
            "       s_from.station_code AS from_code, s_to.station_code AS to_code " +
            "FROM bookings b " +
            "JOIN train_schedules ts ON b.schedule_id = ts.schedule_id " +
            "JOIN trains t ON ts.train_id = t.train_id " +
            "JOIN stations s_from ON b.from_station_id = s_from.station_id " +
            "JOIN stations s_to ON b.to_station_id = s_to.station_id";

    @Override
    public Booking create(Booking booking, Connection externalConn) {
        boolean isInternalConn = (externalConn == null);
        String sql =
                "INSERT INTO bookings (pnr_number, user_id, schedule_id, from_station_id, to_station_id, travel_class, total_fare, booking_status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        Connection conn = null;
        try {
            conn = isInternalConn ? DatabaseConnection.getConnection() : externalConn;
            try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, booking.getPnrNumber());
                stmt.setLong(2, booking.getUserId());
                stmt.setLong(3, booking.getScheduleId());
                stmt.setInt(4, booking.getFromStationId());
                stmt.setInt(5, booking.getToStationId());
                stmt.setString(6, booking.getTravelClass().getCode());
                stmt.setBigDecimal(7, booking.getTotalFare());
                stmt.setString(8, booking.getBookingStatus() != null ? booking.getBookingStatus().name() : BookingStatus.CONFIRMED.name());

                int affected = stmt.executeUpdate();
                if (affected == 0) {
                    throw new DatabaseException("Creating booking failed, no rows affected.");
                }

                try (ResultSet keys = stmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        booking.setBookingId(keys.getLong(1));
                    }
                }
            }
            return booking;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to insert booking record: " + booking.getPnrNumber(), e);
        } finally {
            if (isInternalConn && conn != null) {
                DatabaseConnection.closeQuietly(conn);
            }
        }
    }

    @Override
    public Optional<Booking> findById(Long bookingId) {
        String sql = SELECT_BOOKING_BASE + " WHERE b.booking_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, bookingId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Booking booking = mapRowToBooking(rs);
                    booking.setPassengers(findPassengersByBookingId(booking.getBookingId()));
                    return Optional.of(booking);
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find booking by ID: " + bookingId, e);
        }
    }

    @Override
    public Optional<Booking> findByPnr(String pnrNumber) {
        String sql = SELECT_BOOKING_BASE + " WHERE b.pnr_number = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, pnrNumber != null ? pnrNumber.trim() : "");
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Booking booking = mapRowToBooking(rs);
                    booking.setPassengers(findPassengersByBookingId(booking.getBookingId()));
                    return Optional.of(booking);
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find booking by PNR: " + pnrNumber, e);
        }
    }

    @Override
    public List<Booking> findByUserId(Long userId) {
        String sql = SELECT_BOOKING_BASE + " WHERE b.user_id = ? ORDER BY b.booking_date DESC";
        List<Booking> bookings = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Booking booking = mapRowToBooking(rs);
                    booking.setPassengers(findPassengersByBookingId(booking.getBookingId()));
                    bookings.add(booking);
                }
            }
            return bookings;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find bookings for user: " + userId, e);
        }
    }

    @Override
    public boolean updateStatus(Long bookingId, BookingStatus status, Connection externalConn) {
        boolean isInternalConn = (externalConn == null);
        String sql = "UPDATE bookings SET booking_status = ? WHERE booking_id = ?";

        Connection conn = null;
        try {
            conn = isInternalConn ? DatabaseConnection.getConnection() : externalConn;
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, status.name());
                stmt.setLong(2, bookingId);
                return stmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update booking status for ID: " + bookingId, e);
        } finally {
            if (isInternalConn && conn != null) {
                DatabaseConnection.closeQuietly(conn);
            }
        }
    }

    @Override
    public void addPassengers(Long bookingId, List<Passenger> passengers, Connection externalConn) {
        boolean isInternalConn = (externalConn == null);
        String sql =
                "INSERT INTO booking_passengers (booking_id, full_name, age, gender, berth_preference, seat_id, passenger_status, fare) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        Connection conn = null;
        try {
            conn = isInternalConn ? DatabaseConnection.getConnection() : externalConn;
            try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                for (Passenger p : passengers) {
                    stmt.setLong(1, bookingId);
                    stmt.setString(2, p.getFullName());
                    stmt.setInt(3, p.getAge());
                    stmt.setString(4, p.getGender() != null ? p.getGender().name() : Gender.MALE.name());
                    stmt.setString(5, p.getBerthPreference() != null ? p.getBerthPreference().name() : BerthType.NO_PREFERENCE.name());

                    if (p.getSeatId() != null) {
                        stmt.setInt(6, p.getSeatId());
                    } else {
                        stmt.setNull(6, Types.INTEGER);
                    }

                    stmt.setString(7, p.getPassengerStatus() != null ? p.getPassengerStatus().name() : PassengerStatus.CONFIRMED.name());
                    stmt.setBigDecimal(8, p.getFare());

                    stmt.executeUpdate();

                    try (ResultSet keys = stmt.getGeneratedKeys()) {
                        if (keys.next()) {
                            p.setPassengerId(keys.getLong(1));
                        }
                    }
                    p.setBookingId(bookingId);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to add passengers for booking: " + bookingId, e);
        } finally {
            if (isInternalConn && conn != null) {
                DatabaseConnection.closeQuietly(conn);
            }
        }
    }

    @Override
    public List<Passenger> findPassengersByBookingId(Long bookingId) {
        String sql =
                "SELECT bp.passenger_id, bp.booking_id, bp.full_name, bp.age, bp.gender, " +
                "       bp.berth_preference, bp.seat_id, bp.passenger_status, bp.fare, " +
                "       c.coach_label, s.seat_number, s.berth_type " +
                "FROM booking_passengers bp " +
                "LEFT JOIN seats s ON bp.seat_id = s.seat_id " +
                "LEFT JOIN coaches c ON s.coach_id = c.coach_id " +
                "WHERE bp.booking_id = ? " +
                "ORDER BY bp.passenger_id ASC";

        List<Passenger> passengers = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, bookingId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Passenger p = new Passenger(
                            rs.getLong("passenger_id"),
                            rs.getLong("booking_id"),
                            rs.getString("full_name"),
                            rs.getInt("age"),
                            Gender.fromString(rs.getString("gender")),
                            BerthType.fromString(rs.getString("berth_preference")),
                            rs.getObject("seat_id") != null ? rs.getInt("seat_id") : null,
                            PassengerStatus.fromString(rs.getString("passenger_status")),
                            rs.getBigDecimal("fare")
                    );
                    p.setCoachLabel(rs.getString("coach_label"));
                    if (rs.getObject("seat_number") != null) {
                        p.setSeatNumber(rs.getInt("seat_number"));
                    }
                    String berth = rs.getString("berth_type");
                    if (berth != null) {
                        p.setAllocatedBerthType(BerthType.fromString(berth));
                    }
                    passengers.add(p);
                }
            }
            return passengers;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to retrieve passengers for booking: " + bookingId, e);
        }
    }

    private Booking mapRowToBooking(ResultSet rs) throws SQLException {
        Booking booking = new Booking(
                rs.getLong("booking_id"),
                rs.getString("pnr_number"),
                rs.getLong("user_id"),
                rs.getLong("schedule_id"),
                rs.getInt("from_station_id"),
                rs.getInt("to_station_id"),
                TravelClass.fromCode(rs.getString("travel_class")),
                rs.getTimestamp("booking_date").toLocalDateTime(),
                rs.getBigDecimal("total_fare"),
                BookingStatus.fromString(rs.getString("booking_status"))
        );
        booking.setTrainNumber(rs.getString("train_number"));
        booking.setTrainName(rs.getString("train_name"));
        booking.setFromStationCode(rs.getString("from_code"));
        booking.setToStationCode(rs.getString("to_code"));
        booking.setJourneyDate(rs.getDate("journey_date").toLocalDate());
        return booking;
    }
}
