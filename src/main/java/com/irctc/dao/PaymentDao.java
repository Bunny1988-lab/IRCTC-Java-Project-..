package com.irctc.dao;

import com.irctc.model.Payment;

import java.sql.Connection;
import java.util.Optional;

/**
 * Data Access Object interface for payment transaction ledger records.
 */
public interface PaymentDao {

    Payment create(Payment payment, Connection conn);

    Optional<Payment> findByBookingId(Long bookingId);

    Optional<Payment> findByReference(String transactionReference);
}
