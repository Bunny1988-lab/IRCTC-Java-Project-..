package com.irctc.dao;

import com.irctc.model.RouteStop;

import java.util.List;
import java.util.Optional;

/**
 * Data Access Object interface for train route halts and stops.
 */
public interface RouteStopDao {

    List<RouteStop> findByTrainId(Integer trainId);

    Optional<RouteStop> findStop(Integer trainId, Integer stationId);

    RouteStop create(RouteStop stop);
}
