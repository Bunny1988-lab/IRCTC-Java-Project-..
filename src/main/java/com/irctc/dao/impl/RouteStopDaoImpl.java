package com.irctc.dao.impl;

import com.irctc.dao.RouteStopDao;
import com.irctc.database.DatabaseConnection;
import com.irctc.exception.DatabaseException;
import com.irctc.model.RouteStop;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * JDBC implementation of {@link RouteStopDao}.
 */
public class RouteStopDaoImpl implements RouteStopDao {

    private static final String SELECT_BASE =
            "SELECT rs.route_stop_id, rs.train_id, rs.station_id, rs.stop_sequence, " +
            "       rs.arrival_time, rs.departure_time, rs.distance_from_source_km, rs.day_offset, " +
            "       s.station_code, s.station_name " +
            "FROM route_stops rs " +
            "JOIN stations s ON rs.station_id = s.station_id";

    @Override
    public List<RouteStop> findByTrainId(Integer trainId) {
        String sql = SELECT_BASE + " WHERE rs.train_id = ? ORDER BY rs.stop_sequence ASC";
        List<RouteStop> stops = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, trainId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    stops.add(mapRowToRouteStop(rs));
                }
            }
            return stops;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find route stops for train: " + trainId, e);
        }
    }

    @Override
    public Optional<RouteStop> findStop(Integer trainId, Integer stationId) {
        String sql = SELECT_BASE + " WHERE rs.train_id = ? AND rs.station_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, trainId);
            stmt.setInt(2, stationId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToRouteStop(rs));
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find stop for train " + trainId + " at station " + stationId, e);
        }
    }

    @Override
    public RouteStop create(RouteStop stop) {
        String sql = "INSERT INTO route_stops (train_id, station_id, stop_sequence, arrival_time, departure_time, distance_from_source_km, day_offset) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, stop.getTrainId());
            stmt.setInt(2, stop.getStationId());
            stmt.setInt(3, stop.getStopSequence());
            stmt.setTime(4, stop.getArrivalTime() != null ? Time.valueOf(stop.getArrivalTime()) : null);
            stmt.setTime(5, stop.getDepartureTime() != null ? Time.valueOf(stop.getDepartureTime()) : null);
            stmt.setInt(6, stop.getDistanceFromSourceKm());
            stmt.setInt(7, stop.getDayOffset());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new DatabaseException("Creating route stop failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    stop.setRouteStopId(generatedKeys.getInt(1));
                }
            }
            return stop;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to create route stop for train: " + stop.getTrainId(), e);
        }
    }

    private RouteStop mapRowToRouteStop(ResultSet rs) throws SQLException {
        Time arr = rs.getTime("arrival_time");
        Time dep = rs.getTime("departure_time");

        RouteStop stop = new RouteStop(
                rs.getInt("route_stop_id"),
                rs.getInt("train_id"),
                rs.getInt("station_id"),
                rs.getInt("stop_sequence"),
                arr != null ? arr.toLocalTime() : null,
                dep != null ? dep.toLocalTime() : null,
                rs.getInt("distance_from_source_km"),
                rs.getInt("day_offset")
        );
        stop.setStationCode(rs.getString("station_code"));
        stop.setStationName(rs.getString("station_name"));
        return stop;
    }
}
