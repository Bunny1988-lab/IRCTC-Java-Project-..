package com.irctc.model;

import com.irctc.enums.BerthType;
import com.irctc.enums.BookingStatus;
import com.irctc.enums.Gender;
import com.irctc.enums.PassengerStatus;
import com.irctc.enums.Role;
import com.irctc.enums.TrainType;
import com.irctc.enums.TravelClass;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ModelAndEnumTest {

    @Test
    @DisplayName("TravelClass code mappings should correctly resolve MySQL 8 enum codes")
    void testTravelClassCodes() {
        assertEquals(TravelClass.FIRST_AC, TravelClass.fromCode("1A"));
        assertEquals(TravelClass.SECOND_AC, TravelClass.fromCode("2a"));
        assertEquals(TravelClass.THIRD_AC, TravelClass.fromCode("3A"));
        assertEquals(TravelClass.SLEEPER, TravelClass.fromCode("SL"));
        assertEquals(TravelClass.SECOND_SEATING, TravelClass.fromCode("2S"));
        assertEquals(TravelClass.AC_CHAIR_CAR, TravelClass.fromCode("CC"));
        assertEquals("3A", TravelClass.THIRD_AC.getCode());
    }

    @Test
    @DisplayName("Enums should parse valid and default case-insensitive values")
    void testEnumParsing() {
        assertEquals(Role.ADMIN, Role.fromString("admin"));
        assertEquals(Role.PASSENGER, Role.fromString(null));
        assertEquals(TrainType.RAJDHANI, TrainType.fromString("rajdhani"));
        assertEquals(BookingStatus.CONFIRMED, BookingStatus.fromString("CONFIRMED"));
        assertEquals(PassengerStatus.RAC, PassengerStatus.fromString("rac"));
        assertEquals(Gender.FEMALE, Gender.fromString("female"));
        assertEquals(BerthType.SIDE_LOWER, BerthType.fromString("side_lower"));
    }

    @Test
    @DisplayName("Passenger model should format seat allocation correctly")
    void testPassengerSeatFormatting() {
        Passenger p1 = new Passenger("Rahul Sharma", 29, Gender.MALE, BerthType.LOWER);
        p1.setSeatId(10);
        p1.setCoachLabel("B1");
        p1.setSeatNumber(21);
        p1.setAllocatedBerthType(BerthType.LOWER);
        p1.setPassengerStatus(PassengerStatus.CONFIRMED);

        assertEquals("B1-21 (LOWER)", p1.getFormattedSeatAllocation());

        Passenger p2 = new Passenger("Amit Verma", 31, Gender.MALE, BerthType.UPPER);
        p2.setPassengerStatus(PassengerStatus.WAITLISTED);
        assertTrue(p2.getFormattedSeatAllocation().contains("WL"));
    }

    @Test
    @DisplayName("Booking model should manage passenger lists and totals")
    void testBookingModel() {
        Booking booking = new Booking();
        booking.setPnrNumber("8245192837");
        booking.setTravelClass(TravelClass.THIRD_AC);
        booking.setTotalFare(new BigDecimal("3850.00"));

        Passenger p = new Passenger("Priya Patel", 27, Gender.FEMALE, BerthType.MIDDLE);
        booking.addPassenger(p);

        assertEquals(1, booking.getPassengers().size());
        assertEquals("8245192837", booking.getPnrNumber());
        assertNotNull(booking.toString());
    }
}
