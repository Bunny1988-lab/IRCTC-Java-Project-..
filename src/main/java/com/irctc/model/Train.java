package com.irctc.model;

import com.irctc.enums.TrainType;

/**
 * Represents a train service running between origin and destination stations.
 */
public class Train {

    private Integer trainId;
    private String trainNumber;
    private String trainName;
    private TrainType trainType;
    private Integer sourceStationId;
    private Integer destinationStationId;
    private boolean active;

    // Optional display helpers for JOINed queries
    private String sourceStationCode;
    private String destinationStationCode;

    public Train() {
        this.active = true;
    }

    public Train(Integer trainId, String trainNumber, String trainName, TrainType trainType,
                 Integer sourceStationId, Integer destinationStationId, boolean active) {
        this.trainId = trainId;
        this.trainNumber = trainNumber;
        this.trainName = trainName;
        this.trainType = trainType != null ? trainType : TrainType.EXPRESS;
        this.sourceStationId = sourceStationId;
        this.destinationStationId = destinationStationId;
        this.active = active;
    }

    public Train(String trainNumber, String trainName, TrainType trainType,
                 Integer sourceStationId, Integer destinationStationId) {
        this(null, trainNumber, trainName, trainType, sourceStationId, destinationStationId, true);
    }

    public Integer getTrainId() {
        return trainId;
    }

    public void setTrainId(Integer trainId) {
        this.trainId = trainId;
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

    public TrainType getTrainType() {
        return trainType;
    }

    public void setTrainType(TrainType trainType) {
        this.trainType = trainType;
    }

    public Integer getSourceStationId() {
        return sourceStationId;
    }

    public void setSourceStationId(Integer sourceStationId) {
        this.sourceStationId = sourceStationId;
    }

    public Integer getDestinationStationId() {
        return destinationStationId;
    }

    public void setDestinationStationId(Integer destinationStationId) {
        this.destinationStationId = destinationStationId;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getSourceStationCode() {
        return sourceStationCode;
    }

    public void setSourceStationCode(String sourceStationCode) {
        this.sourceStationCode = sourceStationCode;
    }

    public String getDestinationStationCode() {
        return destinationStationCode;
    }

    public void setDestinationStationCode(String destinationStationCode) {
        this.destinationStationCode = destinationStationCode;
    }

    @Override
    public String toString() {
        return "Train{" +
                "trainId=" + trainId +
                ", trainNumber='" + trainNumber + '\'' +
                ", trainName='" + trainName + '\'' +
                ", trainType=" + trainType +
                ", sourceStationId=" + sourceStationId +
                ", destinationStationId=" + destinationStationId +
                ", active=" + active +
                '}';
    }
}
