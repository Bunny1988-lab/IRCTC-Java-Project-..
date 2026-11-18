package com.irctc.service;

import com.irctc.enums.TravelClass;
import com.irctc.model.RouteStop;
import com.irctc.model.Station;
import com.irctc.model.Train;
import com.irctc.model.TrainSchedule;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Service interface for train routing, station exploration, and schedule lookups.
 */
public interface TrainService {

    List<Station> getAllStations();

    Optional<Station> getStationByCode(String code);

    List<Train> searchTrains(String sourceStationCode, String destinationStationCode);

    List<RouteStop> getRouteStops(Integer trainId);

    TrainSchedule getOrCreateSchedule(Integer trainId, LocalDate journeyDate);

    int getAvailableSeats(Long scheduleId, Integer trainId, TravelClass travelClass);
}
