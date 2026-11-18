package com.irctc.dao;

import com.irctc.model.Train;

import java.util.List;
import java.util.Optional;

/**
 * Data Access Object interface for {@link Train} entity persistence operations.
 */
public interface TrainDao {

    Optional<Train> findById(Integer trainId);

    Optional<Train> findByNumber(String trainNumber);

    List<Train> findAll();

    List<Train> searchTrainsBetweenStations(int sourceStationId, int destinationStationId);

    Train create(Train train);
}
