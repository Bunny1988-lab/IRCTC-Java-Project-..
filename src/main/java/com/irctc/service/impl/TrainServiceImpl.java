package com.irctc.service.impl;

import com.irctc.dao.RouteStopDao;
import com.irctc.dao.SeatDao;
import com.irctc.dao.StationDao;
import com.irctc.dao.TrainDao;
import com.irctc.dao.TrainScheduleDao;
import com.irctc.dao.impl.RouteStopDaoImpl;
import com.irctc.dao.impl.SeatDaoImpl;
import com.irctc.dao.impl.StationDaoImpl;
import com.irctc.dao.impl.TrainDaoImpl;
import com.irctc.dao.impl.TrainScheduleDaoImpl;
import com.irctc.enums.ScheduleStatus;
import com.irctc.enums.TravelClass;
import com.irctc.exception.ValidationException;
import com.irctc.model.RouteStop;
import com.irctc.model.Station;
import com.irctc.model.Train;
import com.irctc.model.TrainSchedule;
import com.irctc.service.TrainService;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of {@link TrainService}.
 */
public class TrainServiceImpl implements TrainService {

    private final StationDao stationDao;
    private final TrainDao trainDao;
    private final RouteStopDao routeStopDao;
    private final TrainScheduleDao scheduleDao;
    private final SeatDao seatDao;

    public TrainServiceImpl() {
        this(new StationDaoImpl(), new TrainDaoImpl(), new RouteStopDaoImpl(), new TrainScheduleDaoImpl(), new SeatDaoImpl());
    }

    public TrainServiceImpl(StationDao stationDao, TrainDao trainDao, RouteStopDao routeStopDao,
                            TrainScheduleDao scheduleDao, SeatDao seatDao) {
        this.stationDao = stationDao;
        this.trainDao = trainDao;
        this.routeStopDao = routeStopDao;
        this.scheduleDao = scheduleDao;
        this.seatDao = seatDao;
    }

    @Override
    public List<Station> getAllStations() {
        return stationDao.findAll();
    }

    @Override
    public Optional<Station> getStationByCode(String code) {
        if (code == null || code.isBlank()) {
            return Optional.empty();
        }
        return stationDao.findByCode(code.trim());
    }

    @Override
    public List<Train> searchTrains(String sourceCode, String destinationCode) {
        if (sourceCode == null || destinationCode == null) {
            throw new ValidationException("Source and destination stations are required.");
        }

        Station src = stationDao.findByCode(sourceCode.trim())
                .orElseThrow(() -> new ValidationException("Source station not found: " + sourceCode));
        Station dest = stationDao.findByCode(destinationCode.trim())
                .orElseThrow(() -> new ValidationException("Destination station not found: " + destinationCode));

        if (src.getStationId().equals(dest.getStationId())) {
            throw new ValidationException("Source and destination stations cannot be the same.");
        }

        return trainDao.searchTrainsBetweenStations(src.getStationId(), dest.getStationId());
    }

    @Override
    public List<RouteStop> getRouteStops(Integer trainId) {
        if (trainId == null) {
            return Collections.emptyList();
        }
        return routeStopDao.findByTrainId(trainId);
    }

    @Override
    public TrainSchedule getOrCreateSchedule(Integer trainId, LocalDate journeyDate) {
        if (trainId == null || journeyDate == null) {
            throw new ValidationException("Train ID and journey date are required to access schedule.");
        }

        return scheduleDao.findByTrainAndDate(trainId, journeyDate)
                .orElseGet(() -> {
                    TrainSchedule newSchedule = new TrainSchedule(trainId, journeyDate, ScheduleStatus.SCHEDULED);
                    return scheduleDao.create(newSchedule);
                });
    }

    @Override
    public int getAvailableSeats(Long scheduleId, Integer trainId, TravelClass travelClass) {
        if (scheduleId == null || trainId == null || travelClass == null) {
            return 0;
        }
        return seatDao.countAvailableSeats(scheduleId, trainId, travelClass);
    }
}
