package com.irctc.dao.impl;

import com.irctc.dao.TrainDao;
import com.irctc.database.DatabaseConnection;
import com.irctc.enums.TrainType;
import com.irctc.exception.DatabaseException;
import com.irctc.model.Train;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * JDBC implementation of {@link TrainDao}.
 */
public class TrainDaoImpl implements TrainDao {

    private static final String SELECT_BASE =
            "SELECT t.train_id, t.train_number, t.train_name, t.train_type, " +
            "       t.source_station_id, t.destination_station_id, t.is_active, " +
            "       s1.station_code AS source_code, s2.station_code AS dest_code " +
            "FROM trains t " +
            "LEFT JOIN stations s1 ON t.source_station_id = s1.station_id " +
            "LEFT JOIN stations s2 ON t.destination_station_id = s2.station_id";

    @Override
    public Optional<Train> findById(Integer trainId) {
        String sql = SELECT_BASE + " WHERE t.train_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, trainId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToTrain(rs));
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find train by ID: " + trainId, e);
        }
    }

    @Override
    public Optional<Train> findByNumber(String trainNumber) {
        String sql = SELECT_BASE + " WHERE t.train_number = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, trainNumber != null ? trainNumber.trim() : "");
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToTrain(rs));
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find train by number: " + trainNumber, e);
        }
    }

    @Override
    public List<Train> findAll() {
        String sql = SELECT_BASE + " ORDER BY t.train_number ASC";
        List<Train> trains = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                trains.add(mapRowToTrain(rs));
            }
            return trains;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to retrieve trains list", e);
        }
    }

    @Override
    public List<Train> searchTrainsBetweenStations(int sourceStationId, int destinationStationId) {
        String sql =
                "SELECT DISTINCT t.train_id, t.train_number, t.train_name, t.train_type, " +
                "       t.source_station_id, t.destination_station_id, t.is_active, " +
                "       s1.station_code AS source_code, s2.station_code AS dest_code " +
                "FROM trains t " +
                "JOIN route_stops r1 ON t.train_id = r1.train_id " +
                "JOIN route_stops r2 ON t.train_id = r2.train_id " +
                "LEFT JOIN stations s1 ON t.source_station_id = s1.station_id " +
                "LEFT JOIN stations s2 ON t.destination_station_id = s2.station_id " +
                "WHERE r1.station_id = ? " +
                "  AND r2.station_id = ? " +
                "  AND r1.stop_sequence < r2.stop_sequence " +
                "  AND t.is_active = TRUE " +
                "ORDER BY t.train_number ASC";

        List<Train> trains = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, sourceStationId);
            stmt.setInt(2, destinationStationId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    trains.add(mapRowToTrain(rs));
                }
            }
            return trains;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to search trains between stations: " + sourceStationId + " -> " + destinationStationId, e);
        }
    }

    @Override
    public Train create(Train train) {
        String sql = "INSERT INTO trains (train_number, train_name, train_type, source_station_id, destination_station_id, is_active) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, train.getTrainNumber());
            stmt.setString(2, train.getTrainName());
            stmt.setString(3, train.getTrainType() != null ? train.getTrainType().name() : TrainType.EXPRESS.name());
            stmt.setInt(4, train.getSourceStationId());
            stmt.setInt(5, train.getDestinationStationId());
            stmt.setBoolean(6, train.isActive());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new DatabaseException("Creating train failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    train.setTrainId(generatedKeys.getInt(1));
                }
            }
            return train;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to create train: " + train.getTrainNumber(), e);
        }
    }

    private Train mapRowToTrain(ResultSet rs) throws SQLException {
        Train train = new Train(
                rs.getInt("train_id"),
                rs.getString("train_number"),
                rs.getString("train_name"),
                TrainType.fromString(rs.getString("train_type")),
                rs.getInt("source_station_id"),
                rs.getInt("destination_station_id"),
                rs.getBoolean("is_active")
        );
        train.setSourceStationCode(rs.getString("source_code"));
        train.setDestinationStationCode(rs.getString("dest_code"));
        return train;
    }
}
