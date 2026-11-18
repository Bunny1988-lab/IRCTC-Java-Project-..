package com.irctc.dao;

import com.irctc.model.Station;

import java.util.List;
import java.util.Optional;

/**
 * Data Access Object interface for {@link Station} operations.
 */
public interface StationDao {

    Optional<Station> findById(Integer stationId);

    Optional<Station> findByCode(String stationCode);

    List<Station> findAll();

    Station create(Station station);
}
