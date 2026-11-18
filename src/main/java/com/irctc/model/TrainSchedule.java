package com.irctc.model;

import com.irctc.enums.ScheduleStatus;

import java.time.LocalDate;

/**
 * Represents a calendar-dated run of a train service.
 */
public class TrainSchedule {

    private Long scheduleId;
    private Integer trainId;
    private LocalDate journeyDate;
    private ScheduleStatus status;

    // Optional display helpers for JOINed queries
    private String trainNumber;
    private String trainName;

    public TrainSchedule() {
        this.status = ScheduleStatus.SCHEDULED;
    }

    public TrainSchedule(Long scheduleId, Integer trainId, LocalDate journeyDate, ScheduleStatus status) {
        this.scheduleId = scheduleId;
        this.trainId = trainId;
        this.journeyDate = journeyDate;
        this.status = status != null ? status : ScheduleStatus.SCHEDULED;
    }

    public TrainSchedule(Integer trainId, LocalDate journeyDate, ScheduleStatus status) {
        this(null, trainId, journeyDate, status);
    }

    public Long getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(Long scheduleId) {
        this.scheduleId = scheduleId;
    }

    public Integer getTrainId() {
        return trainId;
    }

    public void setTrainId(Integer trainId) {
        this.trainId = trainId;
    }

    public LocalDate getJourneyDate() {
        return journeyDate;
    }

    public void setJourneyDate(LocalDate journeyDate) {
        this.journeyDate = journeyDate;
    }

    public ScheduleStatus getStatus() {
        return status;
    }

    public void setStatus(ScheduleStatus status) {
        this.status = status;
    }

    public String getTrainNumber() {
        return trainNumber;
    }

    public void setTrainNumber(String trainNumber) {
        this.trainNumber = trainNumber;
    }

    public String getTrainName() {
        return trainName;
    }

    public void setTrainName(String trainName) {
        this.trainName = trainName;
    }

    @Override
    public String toString() {
        return "TrainSchedule{" +
                "scheduleId=" + scheduleId +
                ", trainId=" + trainId +
                ", journeyDate=" + journeyDate +
                ", status=" + status +
                (trainNumber != null ? ", trainNumber='" + trainNumber + '\'' : "") +
                '}';
    }
}
