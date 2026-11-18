package com.irctc.dao;

import com.irctc.enums.TravelClass;
import com.irctc.model.Seat;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object interface for coach seat inventory and allocation queries.
 */
public interface SeatDao {

    Optional<Seat> findById(Integer seatId);

    List<Seat> findByCoachId(Integer coachId);

    /**
     * Finds and locks available candidate seats for a schedule run and class.
     * Uses pessimistic row locking when a connection is provided within a transaction.
     */
    List<Seat> findAvailableSeats(Long scheduleId, Integer trainId, TravelClass travelClass, int limit, Connection conn);

    int countAvailableSeats(Long scheduleId, Integer trainId, TravelClass travelClass);
}
