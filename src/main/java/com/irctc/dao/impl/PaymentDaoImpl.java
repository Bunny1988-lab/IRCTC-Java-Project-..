package com.irctc.dao.impl;

import com.irctc.dao.PaymentDao;
import com.irctc.database.DatabaseConnection;
import com.irctc.enums.PaymentMethod;
import com.irctc.enums.PaymentStatus;
import com.irctc.exception.DatabaseException;
import com.irctc.model.Payment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

/**
 * JDBC implementation of {@link PaymentDao}.
 */
public class PaymentDaoImpl implements PaymentDao {

    private static final String SELECT_BASE =
            "SELECT payment_id, booking_id, transaction_reference, payment_method, amount, payment_status, payment_time FROM payments";

    @Override
    public Payment create(Payment payment, Connection externalConn) {
        boolean isInternalConn = (externalConn == null);
        String sql =
                "INSERT INTO payments (booking_id, transaction_reference, payment_method, amount, payment_status) " +
                "VALUES (?, ?, ?, ?, ?)";

        Connection conn = null;
        try {
            conn = isInternalConn ? DatabaseConnection.getConnection() : externalConn;
            try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setLong(1, payment.getBookingId());
                stmt.setString(2, payment.getTransactionReference());
                stmt.setString(3, payment.getPaymentMethod() != null ? payment.getPaymentMethod().name() : PaymentMethod.UPI.name());
                stmt.setBigDecimal(4, payment.getAmount());
                stmt.setString(5, payment.getPaymentStatus() != null ? payment.getPaymentStatus().name() : PaymentStatus.PENDING.name());

                int affected = stmt.executeUpdate();
                if (affected == 0) {
                    throw new DatabaseException("Creating payment failed, no rows affected.");
                }

                try (ResultSet keys = stmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        payment.setPaymentId(keys.getLong(1));
                    }
                }
            }
            return payment;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to record payment transaction: " + payment.getTransactionReference(), e);
        } finally {
            if (isInternalConn && conn != null) {
                DatabaseConnection.closeQuietly(conn);
            }
        }
    }

    @Override
    public Optional<Payment> findByBookingId(Long bookingId) {
        String sql = SELECT_BASE + " WHERE booking_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, bookingId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToPayment(rs));
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find payment for booking: " + bookingId, e);
        }
    }

    @Override
    public Optional<Payment> findByReference(String transactionReference) {
        String sql = SELECT_BASE + " WHERE transaction_reference = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, transactionReference);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToPayment(rs));
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find payment by reference: " + transactionReference, e);
        }
    }

    private Payment mapRowToPayment(ResultSet rs) throws SQLException {
        return new Payment(
                rs.getLong("payment_id"),
                rs.getLong("booking_id"),
                rs.getString("transaction_reference"),
                PaymentMethod.fromString(rs.getString("payment_method")),
                rs.getBigDecimal("amount"),
                PaymentStatus.fromString(rs.getString("payment_status")),
                rs.getTimestamp("payment_time").toLocalDateTime()
        );
    }
}
