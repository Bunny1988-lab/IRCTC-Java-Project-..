package com.irctc.service;

import com.irctc.enums.PaymentMethod;
import com.irctc.enums.TravelClass;
import com.irctc.model.Booking;
import com.irctc.model.Passenger;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for transactional ticket reservations, PNR inquiries, and cancellations.
 */
public interface BookingService {

    /**
     * Executes an end-to-end ACID ticket reservation transaction:
     * locks available seats, generates PNR, saves booking and passengers,
     * logs payment, and commits.
     */
    Booking bookTicket(Long userId, Long scheduleId, int fromStationId, int toStationId,
                       TravelClass travelClass, List<Passenger> passengers, PaymentMethod paymentMethod);

    Optional<Booking> getBookingByPnr(String pnrNumber);

    List<Booking> getBookingsForUser(Long userId);

    boolean cancelBooking(String pnrNumber, String reason);
}
