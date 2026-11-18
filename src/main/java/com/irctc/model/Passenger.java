package com.irctc.model;

import com.irctc.enums.BerthType;
import com.irctc.enums.Gender;
import com.irctc.enums.PassengerStatus;

import java.math.BigDecimal;

/**
 * Represents an individual passenger traveling under a booking.
 */
public class Passenger {

    private Long passengerId;
    private Long bookingId;
    private String fullName;
    private int age;
    private Gender gender;
    private BerthType berthPreference;
    private Integer seatId;
    private PassengerStatus passengerStatus;
    private BigDecimal fare;

    // Optional display helpers for JOINed queries
    private String coachLabel;
    private Integer seatNumber;
    private BerthType allocatedBerthType;

    public Passenger() {
        this.berthPreference = BerthType.NO_PREFERENCE;
        this.passengerStatus = PassengerStatus.CONFIRMED;
        this.fare = BigDecimal.ZERO;
    }

    public Passenger(Long passengerId, Long bookingId, String fullName, int age,
                     Gender gender, BerthType berthPreference, Integer seatId,
                     PassengerStatus passengerStatus, BigDecimal fare) {
        this.passengerId = passengerId;
        this.bookingId = bookingId;
        this.fullName = fullName;
        this.age = age;
        this.gender = gender;
        this.berthPreference = berthPreference != null ? berthPreference : BerthType.NO_PREFERENCE;
        this.seatId = seatId;
        this.passengerStatus = passengerStatus != null ? passengerStatus : PassengerStatus.CONFIRMED;
        this.fare = fare != null ? fare : BigDecimal.ZERO;
    }

    public Passenger(String fullName, int age, Gender gender, BerthType berthPreference) {
        this(null, null, fullName, age, gender, berthPreference, null, PassengerStatus.CONFIRMED, BigDecimal.ZERO);
    }

    public Long getPassengerId() {
        return passengerId;
    }

    public void setPassengerId(Long passengerId) {
        this.passengerId = passengerId;
    }

    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public BerthType getBerthPreference() {
        return berthPreference;
    }

    public void setBerthPreference(BerthType berthPreference) {
        this.berthPreference = berthPreference;
    }

    public Integer getSeatId() {
        return seatId;
    }

    public void setSeatId(Integer seatId) {
        this.seatId = seatId;
    }

    public PassengerStatus getPassengerStatus() {
        return passengerStatus;
    }

    public void setPassengerStatus(PassengerStatus passengerStatus) {
        this.passengerStatus = passengerStatus;
    }

    public BigDecimal getFare() {
        return fare;
    }

    public void setFare(BigDecimal fare) {
        this.fare = fare;
    }

    public String getCoachLabel() {
        return coachLabel;
    }

    public void setCoachLabel(String coachLabel) {
        this.coachLabel = coachLabel;
    }

    public Integer getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(Integer seatNumber) {
        this.seatNumber = seatNumber;
    }

    public BerthType getAllocatedBerthType() {
        return allocatedBerthType;
    }

    public void setAllocatedBerthType(BerthType allocatedBerthType) {
        this.allocatedBerthType = allocatedBerthType;
    }

    /**
     * Formats allocated seat designation (e.g. "B1-12 (LOWER)" or "WAITLISTED").
     */
    public String getFormattedSeatAllocation() {
        if (passengerStatus == PassengerStatus.WAITLISTED || seatId == null) {
            return "WL (Waitlisted)";
        }
        if (coachLabel != null && seatNumber != null) {
            String berthStr = allocatedBerthType != null ? " (" + allocatedBerthType.name() + ")" : "";
            return coachLabel + "-" + seatNumber + berthStr;
        }
        return "Seat #" + seatId;
    }

    @Override
    public String toString() {
        return "Passenger{" +
                "passengerId=" + passengerId +
                ", fullName='" + fullName + '\'' +
                ", age=" + age +
                ", gender=" + gender +
                ", status=" + passengerStatus +
                ", seat=" + getFormattedSeatAllocation() +
                ", fare=" + fare +
                '}';
    }
}
