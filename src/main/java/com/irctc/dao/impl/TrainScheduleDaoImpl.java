package com.irctc.dao.impl;

import com.irctc.dao.TrainScheduleDao;
import com.irctc.database.DatabaseConnection;
import com.irctc.enums.ScheduleStatus;
import com.irctc.exception.DatabaseException;
import com.irctc.model.TrainSchedule;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * JDBC implementation of {@link TrainScheduleDao}.
 */
public class TrainScheduleDaoImpl implements TrainScheduleDao {

    private static final String SELECT_BASE =
            "SELECT ts.schedule_id, ts.train_id, ts.journey_date, ts.status, " +
            "       t.train_number, t.train_name " +
            "FROM train_schedules ts " +
            "JOIN trains t ON ts.train_id = t.train_id";

    @Override
    public Optional<TrainSchedule> findById(Long scheduleId) {
        String sql = SELECT_BASE + " WHERE ts.schedule_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, scheduleId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToSchedule(rs));
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find schedule by ID: " + scheduleId, e);
        }
    }

    @Override
    public Optional<TrainSchedule> findByTrainAndDate(Integer trainId, LocalDate journeyDate) {
        String sql = SELECT_BASE + " WHERE ts.train_id = ? AND ts.journey_date = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, trainId);
            stmt.setDate(2, Date.valueOf(journeyDate));

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToSchedule(rs));
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find schedule for train " + trainId + " on " + journeyDate, e);
        }
    }

    @Override
    public List<TrainSchedule> findByTrainId(Integer trainId) {
        String sql = SELECT_BASE + " WHERE ts.train_id = ? ORDER BY ts.journey_date ASC";
        List<TrainSchedule> list = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, trainId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToSchedule(rs));
                }
            }
            return list;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find schedules for train: " + trainId, e);
        }
    }

    @Override
    public TrainSchedule create(TrainSchedule schedule) {
        String sql = "INSERT INTO train_schedules (train_id, journey_date, status) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, schedule.getTrainId());
            stmt.setDate(2, Date.valueOf(schedule.getJourneyDate()));
            stmt.setString(3, schedule.getStatus() != null ? schedule.getStatus().name() : ScheduleStatus.SCHEDULED.name());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new DatabaseException("Creating schedule failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    schedule.setScheduleId(generatedKeys.getLong(1));
                }
            }
            return schedule;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to create schedule for train: " + schedule.getTrainId(), e);
        }
    }

    private TrainSchedule mapRowToSchedule(ResultSet rs) throws SQLException {
        TrainSchedule schedule = new TrainSchedule(
                rs.getLong("schedule_id"),
                rs.getInt("train_id"),
                rs.getDate("journey_date").toLocalDate(),
                ScheduleStatus.fromString(rs.getString("status"))
        );
        schedule.setTrainNumber(rs.getString("train_number"));
        schedule.setTrainName(rs.getString("train_name"));
        return schedule;
    }
}
