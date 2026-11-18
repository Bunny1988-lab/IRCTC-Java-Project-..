package com.irctc.dao.impl;

import com.irctc.dao.StationDao;
import com.irctc.database.DatabaseConnection;
import com.irctc.exception.DatabaseException;
import com.irctc.model.Station;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * JDBC implementation of {@link StationDao}.
 */
public class StationDaoImpl implements StationDao {

    private static final String SELECT_BASE =
            "SELECT station_id, station_code, station_name, city, state FROM stations";

    @Override
    public Optional<Station> findById(Integer stationId) {
        String sql = SELECT_BASE + " WHERE station_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, stationId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToStation(rs));
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find station by ID: " + stationId, e);
        }
    }

    @Override
    public Optional<Station> findByCode(String stationCode) {
        String sql = SELECT_BASE + " WHERE station_code = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, stationCode != null ? stationCode.trim().toUpperCase() : "");
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToStation(rs));
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find station by code: " + stationCode, e);
        }
    }

    @Override
    public List<Station> findAll() {
        String sql = SELECT_BASE + " ORDER BY station_name ASC";
        List<Station> stations = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                stations.add(mapRowToStation(rs));
            }
            return stations;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to retrieve stations list", e);
        }
    }

    @Override
    public Station create(Station station) {
        String sql = "INSERT INTO stations (station_code, station_name, city, state) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, station.getStationCode().toUpperCase());
            stmt.setString(2, station.getStationName());
            stmt.setString(3, station.getCity());
            stmt.setString(4, station.getState());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new DatabaseException("Creating station failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    station.setStationId(generatedKeys.getInt(1));
                }
            }
            return station;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to create station: " + station.getStationCode(), e);
        }
    }

    private Station mapRowToStation(ResultSet rs) throws SQLException {
        return new Station(
                rs.getInt("station_id"),
                rs.getString("station_code"),
                rs.getString("station_name"),
                rs.getString("city"),
                rs.getString("state")
        );
    }
}
