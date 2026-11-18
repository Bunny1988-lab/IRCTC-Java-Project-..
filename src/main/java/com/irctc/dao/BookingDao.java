package com.irctc.dao;

import com.irctc.enums.BookingStatus;
import com.irctc.model.Booking;
import com.irctc.model.Passenger;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object interface for booking reservations and passenger records.
 */
public interface BookingDao {

    Booking create(Booking booking, Connection conn);

    Optional<Booking> findById(Long bookingId);

    Optional<Booking> findByPnr(String pnrNumber);

    List<Booking> findByUserId(Long userId);

    boolean updateStatus(Long bookingId, BookingStatus status, Connection conn);

    void addPassengers(Long bookingId, List<Passenger> passengers, Connection conn);

    List<Passenger> findPassengersByBookingId(Long bookingId);
}
