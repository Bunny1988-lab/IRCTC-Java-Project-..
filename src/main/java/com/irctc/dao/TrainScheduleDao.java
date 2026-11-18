package com.irctc.dao;

import com.irctc.model.TrainSchedule;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object interface for operational dated train runs.
 */
public interface TrainScheduleDao {

    Optional<TrainSchedule> findById(Long scheduleId);

    Optional<TrainSchedule> findByTrainAndDate(Integer trainId, LocalDate journeyDate);

    List<TrainSchedule> findByTrainId(Integer trainId);

    TrainSchedule create(TrainSchedule schedule);
}
