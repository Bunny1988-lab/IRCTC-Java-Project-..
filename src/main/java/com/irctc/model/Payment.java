package com.irctc.model;

import com.irctc.enums.PaymentMethod;
import com.irctc.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Represents a simulated payment gateway transaction.
 */
public class Payment {

    private Long paymentId;
    private Long bookingId;
    private String transactionReference;
    private PaymentMethod paymentMethod;
    private BigDecimal amount;
    private PaymentStatus paymentStatus;
    private LocalDateTime paymentTime;

    public Payment() {
        this.paymentStatus = PaymentStatus.PENDING;
        this.amount = BigDecimal.ZERO;
    }

    public Payment(Long paymentId, Long bookingId, String transactionReference,
                   PaymentMethod paymentMethod, BigDecimal amount,
                   PaymentStatus paymentStatus, LocalDateTime paymentTime) {
        this.paymentId = paymentId;
        this.bookingId = bookingId;
        this.transactionReference = transactionReference;
        this.paymentMethod = paymentMethod != null ? paymentMethod : PaymentMethod.UPI;
        this.amount = amount != null ? amount : BigDecimal.ZERO;
        this.paymentStatus = paymentStatus != null ? paymentStatus : PaymentStatus.PENDING;
        this.paymentTime = paymentTime != null ? paymentTime : LocalDateTime.now();
    }

    public Payment(Long bookingId, String transactionReference, PaymentMethod paymentMethod, BigDecimal amount) {
        this(null, bookingId, transactionReference, paymentMethod, amount, PaymentStatus.PENDING, LocalDateTime.now());
    }

    public Long getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Long paymentId) {
        this.paymentId = paymentId;
    }

    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

    public String getTransactionReference() {
        return transactionReference;
    }

    public void setTransactionReference(String transactionReference) {
        this.transactionReference = transactionReference;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public LocalDateTime getPaymentTime() {
        return paymentTime;
    }

    public void setPaymentTime(LocalDateTime paymentTime) {
        this.paymentTime = paymentTime;
    }

    @Override
    public String toString() {
        return "Payment{" +
                "paymentId=" + paymentId +
                ", bookingId=" + bookingId +
                ", transactionReference='" + transactionReference + '\'' +
                ", paymentMethod=" + paymentMethod +
                ", amount=" + amount +
                ", paymentStatus=" + paymentStatus +
                '}';
    }
}
