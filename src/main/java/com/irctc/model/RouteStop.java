package com.irctc.model;

import java.time.LocalTime;

/**
 * Represents an ordered stopping halt along a train route.
 */
public class RouteStop {

    private Integer routeStopId;
    private Integer trainId;
    private Integer stationId;
    private int stopSequence;
    private LocalTime arrivalTime;
    private LocalTime departureTime;
    private int distanceFromSourceKm;
    private int dayOffset;

    // Optional display helpers for JOINed queries
    private String stationCode;
    private String stationName;

    public RouteStop() {
    }

    public RouteStop(Integer routeStopId, Integer trainId, Integer stationId, int stopSequence,
                     LocalTime arrivalTime, LocalTime departureTime, int distanceFromSourceKm, int dayOffset) {
        this.routeStopId = routeStopId;
        this.trainId = trainId;
        this.stationId = stationId;
        this.stopSequence = stopSequence;
        this.arrivalTime = arrivalTime;
        this.departureTime = departureTime;
        this.distanceFromSourceKm = distanceFromSourceKm;
        this.dayOffset = dayOffset;
    }

    public RouteStop(Integer trainId, Integer stationId, int stopSequence,
                     LocalTime arrivalTime, LocalTime departureTime, int distanceFromSourceKm, int dayOffset) {
        this(null, trainId, stationId, stopSequence, arrivalTime, departureTime, distanceFromSourceKm, dayOffset);
    }

    public Integer getRouteStopId() {
        return routeStopId;
    }

    public void setRouteStopId(Integer routeStopId) {
        this.routeStopId = routeStopId;
    }

    public Integer getTrainId() {
        return trainId;
    }

    public void setTrainId(Integer trainId) {
        this.trainId = trainId;
    }

    public Integer getStationId() {
        return stationId;
    }

    public void setStationId(Integer stationId) {
        this.stationId = stationId;
    }

    public int getStopSequence() {
        return stopSequence;
    }

    public void setStopSequence(int stopSequence) {
        this.stopSequence = stopSequence;
    }

    public LocalTime getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(LocalTime arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public LocalTime getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(LocalTime departureTime) {
        this.departureTime = departureTime;
    }

    public int getDistanceFromSourceKm() {
        return distanceFromSourceKm;
    }

    public void setDistanceFromSourceKm(int distanceFromSourceKm) {
        this.distanceFromSourceKm = distanceFromSourceKm;
    }

    public int getDayOffset() {
        return dayOffset;
    }

    public void setDayOffset(int dayOffset) {
        this.dayOffset = dayOffset;
    }

    public String getStationCode() {
        return stationCode;
    }

    public void setStationCode(String stationCode) {
        this.stationCode = stationCode;
    }

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    @Override
    public String toString() {
        return "RouteStop{" +
                "routeStopId=" + routeStopId +
                ", trainId=" + trainId +
                ", stationId=" + stationId +
                ", stopSequence=" + stopSequence +
                ", arrivalTime=" + arrivalTime +
                ", departureTime=" + departureTime +
                ", distanceFromSourceKm=" + distanceFromSourceKm +
                ", dayOffset=" + dayOffset +
                '}';
    }
}
