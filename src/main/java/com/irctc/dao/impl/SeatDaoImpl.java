package com.irctc.dao.impl;

import com.irctc.dao.SeatDao;
import com.irctc.database.DatabaseConnection;
import com.irctc.enums.BerthType;
import com.irctc.enums.TravelClass;
import com.irctc.exception.DatabaseException;
import com.irctc.model.Seat;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * JDBC implementation of {@link SeatDao}.
 */
public class SeatDaoImpl implements SeatDao {

    @Override
    public Optional<Seat> findById(Integer seatId) {
        String sql =
                "SELECT s.seat_id, s.coach_id, s.seat_number, s.berth_type, c.coach_label, c.travel_class " +
                "FROM seats s " +
                "JOIN coaches c ON s.coach_id = c.coach_id " +
                "WHERE s.seat_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, seatId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToSeat(rs));
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find seat by ID: " + seatId, e);
        }
    }

    @Override
    public List<Seat> findByCoachId(Integer coachId) {
        String sql =
                "SELECT s.seat_id, s.coach_id, s.seat_number, s.berth_type, c.coach_label, c.travel_class " +
                "FROM seats s " +
                "JOIN coaches c ON s.coach_id = c.coach_id " +
                "WHERE s.coach_id = ? " +
                "ORDER BY s.seat_number ASC";

        List<Seat> seats = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, coachId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    seats.add(mapRowToSeat(rs));
                }
            }
            return seats;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find seats for coach: " + coachId, e);
        }
    }

    @Override
    public List<Seat> findAvailableSeats(Long scheduleId, Integer trainId, TravelClass travelClass, int limit, Connection externalConn) {
        boolean isInternalConn = (externalConn == null);
        String sql =
                "SELECT s.seat_id, s.coach_id, s.seat_number, s.berth_type, c.coach_label, c.travel_class " +
                "FROM seats s " +
                "JOIN coaches c ON s.coach_id = c.coach_id " +
                "WHERE c.train_id = ? " +
                "  AND c.travel_class = ? " +
                "  AND s.seat_id NOT IN ( " +
                "      SELECT bp.seat_id " +
                "      FROM booking_passengers bp " +
                "      JOIN bookings b ON bp.booking_id = b.booking_id " +
                "      WHERE b.schedule_id = ? " +
                "        AND bp.passenger_status = 'CONFIRMED' " +
                "        AND bp.seat_id IS NOT NULL " +
                "  ) " +
                "ORDER BY s.seat_id ASC " +
                "LIMIT ?" +
                (isInternalConn ? "" : " FOR UPDATE");

        List<Seat> availableSeats = new ArrayList<>();
        Connection conn = null;

        try {
            conn = isInternalConn ? DatabaseConnection.getConnection() : externalConn;
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, trainId);
                stmt.setString(2, travelClass.getCode());
                stmt.setLong(3, scheduleId);
                stmt.setInt(4, limit);

                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        availableSeats.add(mapRowToSeat(rs));
                    }
                }
            }
            return availableSeats;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to query available seats for schedule: " + scheduleId, e);
        } finally {
            if (isInternalConn && conn != null) {
                DatabaseConnection.closeQuietly(conn);
            }
        }
    }

    @Override
    public int countAvailableSeats(Long scheduleId, Integer trainId, TravelClass travelClass) {
        String sql =
                "SELECT COUNT(*) " +
                "FROM seats s " +
                "JOIN coaches c ON s.coach_id = c.coach_id " +
                "WHERE c.train_id = ? " +
                "  AND c.travel_class = ? " +
                "  AND s.seat_id NOT IN ( " +
                "      SELECT bp.seat_id " +
                "      FROM booking_passengers bp " +
                "      JOIN bookings b ON bp.booking_id = b.booking_id " +
                "      WHERE b.schedule_id = ? " +
                "        AND bp.passenger_status = 'CONFIRMED' " +
                "        AND bp.seat_id IS NOT NULL " +
                "  )";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, trainId);
            stmt.setString(2, travelClass.getCode());
            stmt.setLong(3, scheduleId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
            return 0;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to count available seats for schedule: " + scheduleId, e);
        }
    }

    private Seat mapRowToSeat(ResultSet rs) throws SQLException {
        Seat seat = new Seat(
                rs.getInt("seat_id"),
                rs.getInt("coach_id"),
                rs.getInt("seat_number"),
                BerthType.fromString(rs.getString("berth_type"))
        );
        seat.setCoachLabel(rs.getString("coach_label"));
        seat.setTravelClass(TravelClass.fromCode(rs.getString("travel_class")));
        return seat;
    }
}
