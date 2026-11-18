package com.irctc.model;

import com.irctc.enums.TravelClass;

/**
 * Represents a physical railway coach attached to a train.
 */
public class Coach {

    private Integer coachId;
    private Integer trainId;
    private String coachLabel;
    private TravelClass travelClass;
    private int totalSeats;

    public Coach() {
    }

    public Coach(Integer coachId, Integer trainId, String coachLabel, TravelClass travelClass, int totalSeats) {
        this.coachId = coachId;
        this.trainId = trainId;
        this.coachLabel = coachLabel;
        this.travelClass = travelClass;
        this.totalSeats = totalSeats;
    }

    public Coach(Integer trainId, String coachLabel, TravelClass travelClass, int totalSeats) {
        this(null, trainId, coachLabel, travelClass, totalSeats);
    }

    public Integer getCoachId() {
        return coachId;
    }

    public void setCoachId(Integer coachId) {
        this.coachId = coachId;
    }

    public Integer getTrainId() {
        return trainId;
    }

    public void setTrainId(Integer trainId) {
        this.trainId = trainId;
    }

    public String getCoachLabel() {
        return coachLabel;
    }

    public void setCoachLabel(String coachLabel) {
        this.coachLabel = coachLabel;
    }

    public TravelClass getTravelClass() {
        return travelClass;
    }

    public void setTravelClass(TravelClass travelClass) {
        this.travelClass = travelClass;
    }

    public int getTotalSeats() {
        return totalSeats;
    }

    public void setTotalSeats(int totalSeats) {
        this.totalSeats = totalSeats;
    }

    @Override
    public String toString() {
        return "Coach{" +
                "coachId=" + coachId +
                ", trainId=" + trainId +
                ", coachLabel='" + coachLabel + '\'' +
                ", travelClass=" + travelClass +
                ", totalSeats=" + totalSeats +
                '}';
    }
}
