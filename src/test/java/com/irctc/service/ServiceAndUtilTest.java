package com.irctc.service;

import com.irctc.enums.Gender;
import com.irctc.enums.PaymentMethod;
import com.irctc.enums.Role;
import com.irctc.enums.TravelClass;
import com.irctc.exception.ValidationException;
import com.irctc.model.Passenger;
import com.irctc.service.impl.BookingServiceImpl;
import com.irctc.service.impl.UserServiceImpl;
import com.irctc.util.FareCalculator;
import com.irctc.util.PasswordUtil;
import com.irctc.util.PnrGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ServiceAndUtilTest {

    @Test
    @DisplayName("PasswordUtil should generate salted hashes and verify credentials correctly")
    void testPasswordHashingAndVerification() {
        String raw = "SecurePass@2026";
        String hashed = PasswordUtil.hashPassword(raw);

        assertNotNull(hashed);
        assertTrue(hashed.contains("$"), "Hash should contain salt separator '$'");

        assertTrue(PasswordUtil.verifyPassword(raw, hashed), "Password verification should succeed for matching password");
        assertFalse(PasswordUtil.verifyPassword("WrongPassword", hashed), "Verification should fail for incorrect password");
    }

    @Test
    @DisplayName("PnrGenerator should generate valid 10-digit numeric strings")
    void testPnrGeneration() {
        String pnr = PnrGenerator.generatePnr();

        assertNotNull(pnr);
        assertEquals(10, pnr.length(), "PNR must be exactly 10 digits");
        assertTrue(pnr.matches("\\d{10}"), "PNR must contain only digits");
        char firstChar = pnr.charAt(0);
        assertTrue(firstChar >= '2' && firstChar <= '8', "First digit must be a valid zone code (2-8)");
    }

    @Test
    @DisplayName("FareCalculator should compute accurate fares by distance and class")
    void testFareCalculation() {
        // 1000 km in 3A: 350 + (1000 * 1.15) = 1500.00
        BigDecimal fare3A = FareCalculator.calculateFare(TravelClass.THIRD_AC, 1000);
        assertEquals(new BigDecimal("1500.00"), fare3A);

        // 1000 km in Sleeper: 120 + (1000 * 0.45) = 570.00
        BigDecimal fareSL = FareCalculator.calculateFare(TravelClass.SLEEPER, 1000);
        assertEquals(new BigDecimal("570.00"), fareSL);

        // 1000 km in 1A: 1000 + (1000 * 2.60) = 3600.00
        BigDecimal fare1A = FareCalculator.calculateFare(TravelClass.FIRST_AC, 1000);
        assertEquals(new BigDecimal("3600.00"), fare1A);
    }

    @Test
    @DisplayName("UserService should reject invalid registration inputs")
    void testUserRegistrationValidation() {
        UserService userService = new UserServiceImpl(null);

        // Password too short (< 6 chars)
        assertThrows(ValidationException.class, () ->
                userService.register("john_doe", "123", "John Doe", "john@example.com", "9876543210", Role.PASSENGER)
        );

        // Invalid email
        assertThrows(ValidationException.class, () ->
                userService.register("john_doe", "Password123", "John Doe", "invalid-email", "9876543210", Role.PASSENGER)
        );

        // Invalid phone number (not 10 digits)
        assertThrows(ValidationException.class, () ->
                userService.register("john_doe", "Password123", "John Doe", "john@example.com", "12345", Role.PASSENGER)
        );
    }

    @Test
    @DisplayName("BookingService should reject invalid reservation parameters")
    void testBookingValidation() {
        BookingService bookingService = new BookingServiceImpl(null, null, null, null, null);

        // Identical boarding and deboarding stations
        assertThrows(ValidationException.class, () ->
                bookingService.bookTicket(1L, 1L, 2, 2, TravelClass.THIRD_AC, List.of(new Passenger("A", 25, Gender.MALE, null)), PaymentMethod.UPI)
        );

        // Exceeding 6 passengers
        List<Passenger> tooMany = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            tooMany.add(new Passenger("Passenger " + i, 20 + i, Gender.MALE, null));
        }
        assertThrows(ValidationException.class, () ->
                bookingService.bookTicket(1L, 1L, 1, 2, TravelClass.THIRD_AC, tooMany, PaymentMethod.UPI)
        );
    }
}
