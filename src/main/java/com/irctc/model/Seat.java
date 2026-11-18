package com.irctc.model;

import com.irctc.enums.BerthType;
import com.irctc.enums.TravelClass;

/**
 * Represents an individual physical seat/berth in a coach.
 */
public class Seat {

    private Integer seatId;
    private Integer coachId;
    private int seatNumber;
    private BerthType berthType;

    // Optional display helpers for JOINed queries
    private String coachLabel;
    private TravelClass travelClass;

    public Seat() {
    }

    public Seat(Integer seatId, Integer coachId, int seatNumber, BerthType berthType) {
        this.seatId = seatId;
        this.coachId = coachId;
        this.seatNumber = seatNumber;
        this.berthType = berthType;
    }

    public Seat(Integer coachId, int seatNumber, BerthType berthType) {
        this(null, coachId, seatNumber, berthType);
    }

    public Integer getSeatId() {
        return seatId;
    }

    public void setSeatId(Integer seatId) {
        this.seatId = seatId;
    }

    public Integer getCoachId() {
        return coachId;
    }

    public void setCoachId(Integer coachId) {
        this.coachId = coachId;
    }

    public int getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(int seatNumber) {
        this.seatNumber = seatNumber;
    }

    public BerthType getBerthType() {
        return berthType;
    }

    public void setBerthType(BerthType berthType) {
        this.berthType = berthType;
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

    @Override
    public String toString() {
        return "Seat{" +
                "seatId=" + seatId +
                ", coachId=" + coachId +
                ", seatNumber=" + seatNumber +
                ", berthType=" + berthType +
                (coachLabel != null ? ", coachLabel='" + coachLabel + '\'' : "") +
                '}';
    }
}
