package com.irctc.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Audit record representing a full or partial ticket cancellation and refund.
 */
public class Cancellation {

    private Long cancellationId;
    private Long bookingId;
    private Long passengerId;
    private LocalDateTime cancellationTime;
    private BigDecimal refundAmount;
    private BigDecimal cancellationCharge;
    private String cancellationReason;

    public Cancellation() {
        this.refundAmount = BigDecimal.ZERO;
        this.cancellationCharge = BigDecimal.ZERO;
    }

    public Cancellation(Long cancellationId, Long bookingId, Long passengerId,
                        LocalDateTime cancellationTime, BigDecimal refundAmount,
                        BigDecimal cancellationCharge, String cancellationReason) {
        this.cancellationId = cancellationId;
        this.bookingId = bookingId;
        this.passengerId = passengerId;
        this.cancellationTime = cancellationTime != null ? cancellationTime : LocalDateTime.now();
        this.refundAmount = refundAmount != null ? refundAmount : BigDecimal.ZERO;
        this.cancellationCharge = cancellationCharge != null ? cancellationCharge : BigDecimal.ZERO;
        this.cancellationReason = cancellationReason;
    }

    public Cancellation(Long bookingId, Long passengerId, BigDecimal refundAmount,
                        BigDecimal cancellationCharge, String cancellationReason) {
        this(null, bookingId, passengerId, LocalDateTime.now(), refundAmount, cancellationCharge, cancellationReason);
    }

    public Long getCancellationId() {
        return cancellationId;
    }

    public void setCancellationId(Long cancellationId) {
        this.cancellationId = cancellationId;
    }

    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

    public Long getPassengerId() {
        return passengerId;
    }

    public void setPassengerId(Long passengerId) {
        this.passengerId = passengerId;
    }

    public LocalDateTime getCancellationTime() {
        return cancellationTime;
    }

    public void setCancellationTime(LocalDateTime cancellationTime) {
        this.cancellationTime = cancellationTime;
    }

    public BigDecimal getRefundAmount() {
        return refundAmount;
    }

    public void setRefundAmount(BigDecimal refundAmount) {
        this.refundAmount = refundAmount;
    }

    public BigDecimal getCancellationCharge() {
        return cancellationCharge;
    }

    public void setCancellationCharge(BigDecimal cancellationCharge) {
        this.cancellationCharge = cancellationCharge;
    }

    public String getCancellationReason() {
        return cancellationReason;
    }

    public void setCancellationReason(String cancellationReason) {
        this.cancellationReason = cancellationReason;
    }

    @Override
    public String toString() {
        return "Cancellation{" +
                "cancellationId=" + cancellationId +
                ", bookingId=" + bookingId +
                ", passengerId=" + passengerId +
                ", refundAmount=" + refundAmount +
                ", cancellationCharge=" + cancellationCharge +
                '}';
    }
}
