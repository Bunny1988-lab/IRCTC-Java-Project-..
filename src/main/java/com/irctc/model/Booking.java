package com.irctc.model;

import com.irctc.enums.BookingStatus;
import com.irctc.enums.TravelClass;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Master reservation entity tracking a ticket booking under a unique PNR.
 */
public class Booking {

    private Long bookingId;
    private String pnrNumber;
    private Long userId;
    private Long scheduleId;
    private Integer fromStationId;
    private Integer toStationId;
    private TravelClass travelClass;
    private LocalDateTime bookingDate;
    private BigDecimal totalFare;
    private BookingStatus bookingStatus;

    // Associated passenger records (1 to 6)
    private List<Passenger> passengers;

    // Optional display helpers for JOINed queries
    private String trainNumber;
    private String trainName;
    private String fromStationCode;
    private String toStationCode;
    private LocalDate journeyDate;

    public Booking() {
        this.bookingStatus = BookingStatus.CONFIRMED;
        this.totalFare = BigDecimal.ZERO;
        this.passengers = new ArrayList<>();
    }

    public Booking(Long bookingId, String pnrNumber, Long userId, Long scheduleId,
                   Integer fromStationId, Integer toStationId, TravelClass travelClass,
                   LocalDateTime bookingDate, BigDecimal totalFare, BookingStatus bookingStatus) {
        this.bookingId = bookingId;
        this.pnrNumber = pnrNumber;
        this.userId = userId;
        this.scheduleId = scheduleId;
        this.fromStationId = fromStationId;
        this.toStationId = toStationId;
        this.travelClass = travelClass;
        this.bookingDate = bookingDate != null ? bookingDate : LocalDateTime.now();
        this.totalFare = totalFare != null ? totalFare : BigDecimal.ZERO;
        this.bookingStatus = bookingStatus != null ? bookingStatus : BookingStatus.CONFIRMED;
        this.passengers = new ArrayList<>();
    }

    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

    public String getPnrNumber() {
        return pnrNumber;
    }

    public void setPnrNumber(String pnrNumber) {
        this.pnrNumber = pnrNumber;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(Long scheduleId) {
        this.scheduleId = scheduleId;
    }

    public Integer getFromStationId() {
        return fromStationId;
    }

    public void setFromStationId(Integer fromStationId) {
        this.fromStationId = fromStationId;
    }

    public Integer getToStationId() {
        return toStationId;
    }

    public void setToStationId(Integer toStationId) {
        this.toStationId = toStationId;
    }

    public TravelClass getTravelClass() {
        return travelClass;
    }

    public void setTravelClass(TravelClass travelClass) {
        this.travelClass = travelClass;
    }

    public LocalDateTime getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(LocalDateTime bookingDate) {
        this.bookingDate = bookingDate;
    }

    public BigDecimal getTotalFare() {
        return totalFare;
    }

    public void setTotalFare(BigDecimal totalFare) {
        this.totalFare = totalFare;
    }

    public BookingStatus getBookingStatus() {
        return bookingStatus;
    }

    public void setBookingStatus(BookingStatus bookingStatus) {
        this.bookingStatus = bookingStatus;
    }

    public List<Passenger> getPassengers() {
        return passengers;
    }

    public void setPassengers(List<Passenger> passengers) {
        this.passengers = passengers != null ? passengers : new ArrayList<>();
    }

    public void addPassenger(Passenger passenger) {
        if (this.passengers == null) {
            this.passengers = new ArrayList<>();
        }
        this.passengers.add(passenger);
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

    public String getFromStationCode() {
        return fromStationCode;
    }

    public void setFromStationCode(String fromStationCode) {
        this.fromStationCode = fromStationCode;
    }

    public String getToStationCode() {
        return toStationCode;
    }

    public void setToStationCode(String toStationCode) {
        this.toStationCode = toStationCode;
    }

    public LocalDate getJourneyDate() {
        return journeyDate;
    }

    public void setJourneyDate(LocalDate journeyDate) {
        this.journeyDate = journeyDate;
    }

    @Override
    public String toString() {
        return "Booking{" +
                "bookingId=" + bookingId +
                ", pnrNumber='" + pnrNumber + '\'' +
                ", travelClass=" + travelClass +
                ", bookingStatus=" + bookingStatus +
                ", totalFare=" + totalFare +
                ", passengerCount=" + (passengers != null ? passengers.size() : 0) +
                '}';
    }
}
