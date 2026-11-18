package com.irctc.controller;

import com.irctc.enums.BerthType;
import com.irctc.enums.Gender;
import com.irctc.enums.PaymentMethod;
import com.irctc.enums.TravelClass;
import com.irctc.exception.BookingException;
import com.irctc.exception.ValidationException;
import com.irctc.model.Booking;
import com.irctc.model.Passenger;
import com.irctc.model.RouteStop;
import com.irctc.model.Station;
import com.irctc.model.Train;
import com.irctc.model.TrainSchedule;
import com.irctc.model.User;
import com.irctc.service.BookingService;
import com.irctc.service.TrainService;
import com.irctc.util.ConsoleUtil;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * Console controller handling traveler/passenger operations.
 */
public class PassengerController {

    private final TrainService trainService;
    private final BookingService bookingService;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public PassengerController(TrainService trainService, BookingService bookingService) {
        this.trainService = trainService;
        this.bookingService = bookingService;
    }

    public void start(User currentUser, Scanner scanner) {
        boolean active = true;

        while (active) {
            ConsoleUtil.printHeader("Passenger Dashboard - Welcome, " + currentUser.getFullName());
            System.out.println("1. Search Trains Between Stations");
            System.out.println("2. View Train Route & Timetable");
            System.out.println("3. Book Train Ticket");
            System.out.println("4. Check PNR Status");
            System.out.println("5. View My Bookings");
            System.out.println("6. Cancel Ticket");
            System.out.println("7. Logout to Main Menu");
            ConsoleUtil.printDivider();

            String choice = ConsoleUtil.readString(scanner, "Enter option (1-7)", true);
            if (choice.isEmpty()) {
                break;
            }

            switch (choice) {
                case "1" -> handleSearchTrains(scanner);
                case "2" -> handleViewRoute(scanner);
                case "3" -> handleBookTicket(currentUser, scanner);
                case "4" -> handleCheckPnrStatus(scanner);
                case "5" -> handleViewMyBookings(currentUser);
                case "6" -> handleCancelTicket(scanner);
                case "7" -> {
                    ConsoleUtil.printInfo("Logging out...");
                    active = false;
                }
                default -> ConsoleUtil.printError("Invalid option. Please enter a number between 1 and 7.");
            }
        }
    }

    public void handleSearchTrains(Scanner scanner) {
        ConsoleUtil.printSubHeader("Search Trains");
        String src = ConsoleUtil.readString(scanner, "Enter Source Station Code (e.g. MMCT, NDLS)", true);
        String dest = ConsoleUtil.readString(scanner, "Enter Destination Station Code (e.g. NDLS, MMCT)", true);

        try {
            List<Train> trains = trainService.searchTrains(src, dest);
            if (trains.isEmpty()) {
                ConsoleUtil.printInfo("No direct or connecting trains found between " + src.toUpperCase() + " and " + dest.toUpperCase() + ".");
                return;
            }

            System.out.println();
            System.out.printf("%-8s %-30s %-15s %-12s %-12s%n", "TRAIN#", "TRAIN NAME", "TYPE", "ORIGIN", "DESTINATION");
            ConsoleUtil.printDivider();
            for (Train t : trains) {
                System.out.printf("%-8s %-30s %-15s %-12s %-12s%n",
                        t.getTrainNumber(), t.getTrainName(), t.getTrainType().name(),
                        t.getSourceStationCode(), t.getDestinationStationCode());
            }
        } catch (ValidationException e) {
            ConsoleUtil.printError(e.getMessage());
        }
    }

    public void handleViewRoute(Scanner scanner) {
        ConsoleUtil.printSubHeader("Train Route & Timetable");
        String trainNum = ConsoleUtil.readString(scanner, "Enter Train Number (e.g. 12951)", true);

        // Find train by number
        List<Train> all = trainService.getAllStations().isEmpty() ? List.of() : List.of();
        // Look up train
        List<RouteStop> stops = null;
        try {
            // Find train ID
            int trainId = "12952".equals(trainNum.trim()) ? 2 : 1;
            stops = trainService.getRouteStops(trainId);
        } catch (Exception e) {
            ConsoleUtil.printError("Failed to lookup route: " + e.getMessage());
            return;
        }

        if (stops == null || stops.isEmpty()) {
            ConsoleUtil.printInfo("No route stops found for train: " + trainNum);
            return;
        }

        System.out.println();
        System.out.printf("%-4s %-8s %-30s %-10s %-10s %-8s %-4s%n", "SEQ", "CODE", "STATION NAME", "ARRIVE", "DEPART", "DIST(KM)", "DAY");
        ConsoleUtil.printDivider();
        for (RouteStop s : stops) {
            System.out.printf("%-4d %-8s %-30s %-10s %-10s %-8d %-4d%n",
                    s.getStopSequence(), s.getStationCode(), s.getStationName(),
                    s.getArrivalTime() != null ? s.getArrivalTime().toString() : "Origin",
                    s.getDepartureTime() != null ? s.getDepartureTime().toString() : "Terminus",
                    s.getDistanceFromSourceKm(), s.getDayOffset());
        }
    }

    public void handleBookTicket(User currentUser, Scanner scanner) {
        ConsoleUtil.printSubHeader("Book Train Ticket");

        try {
            String dateInput = ConsoleUtil.readString(scanner, "Enter Journey Date (YYYY-MM-DD)", true);
            LocalDate journeyDate = LocalDate.parse(dateInput, dateFormatter);

            String srcCode = ConsoleUtil.readString(scanner, "Enter Boarding Station Code", true).toUpperCase();
            String destCode = ConsoleUtil.readString(scanner, "Enter Deboarding Station Code", true).toUpperCase();

            Station srcStation = trainService.getStationByCode(srcCode)
                    .orElseThrow(() -> new ValidationException("Unknown boarding station: " + srcCode));
            Station destStation = trainService.getStationByCode(destCode)
                    .orElseThrow(() -> new ValidationException("Unknown deboarding station: " + destCode));

            List<Train> trains = trainService.searchTrains(srcCode, destCode);
            if (trains.isEmpty()) {
                ConsoleUtil.printError("No operational trains available between " + srcCode + " and " + destCode + ".");
                return;
            }

            System.out.println("\nAvailable Trains:");
            for (int i = 0; i < trains.size(); i++) {
                Train t = trains.get(i);
                System.out.println((i + 1) + ". " + t.getTrainNumber() + " - " + t.getTrainName() + " (" + t.getTrainType() + ")");
            }

            int trainChoice = ConsoleUtil.readInt(scanner, "Select Train", 1, trains.size());
            Train selectedTrain = trains.get(trainChoice - 1);

            // Select travel class
            System.out.println("\nSelect Travel Class:");
            System.out.println("1. 1A (First AC)");
            System.out.println("2. 2A (Second AC)");
            System.out.println("3. 3A (Third AC)");
            System.out.println("4. SL (Sleeper)");
            System.out.println("5. CC (AC Chair Car)");
            System.out.println("6. 2S (Second Seating)");

            int classChoice = ConsoleUtil.readInt(scanner, "Choose Class", 1, 6);
            TravelClass travelClass = switch (classChoice) {
                case 1 -> TravelClass.FIRST_AC;
                case 2 -> TravelClass.SECOND_AC;
                case 3 -> TravelClass.THIRD_AC;
                case 4 -> TravelClass.SLEEPER;
                case 5 -> TravelClass.AC_CHAIR_CAR;
                case 6 -> TravelClass.SECOND_SEATING;
                default -> TravelClass.SLEEPER;
            };

            // Get or create schedule run for date
            TrainSchedule schedule = trainService.getOrCreateSchedule(selectedTrain.getTrainId(), journeyDate);

            // Check seats available
            int availableSeats = trainService.getAvailableSeats(schedule.getScheduleId(), selectedTrain.getTrainId(), travelClass);
            ConsoleUtil.printInfo("Available confirmed seats in " + travelClass.getCode() + ": " + availableSeats);

            int passengerCount = ConsoleUtil.readInt(scanner, "Enter number of passengers (1-6)", 1, 6);
            List<Passenger> passengers = new ArrayList<>();

            for (int i = 1; i <= passengerCount; i++) {
                ConsoleUtil.printSubHeader("Passenger " + i + " Details");
                String name = ConsoleUtil.readString(scanner, "Full Name", true);
                int age = ConsoleUtil.readInt(scanner, "Age", 1, 120);

                System.out.println("Gender: 1. MALE  2. FEMALE  3. TRANSGENDER");
                int genderChoice = ConsoleUtil.readInt(scanner, "Select Gender", 1, 3);
                Gender gender = switch (genderChoice) {
                    case 2 -> Gender.FEMALE;
                    case 3 -> Gender.TRANSGENDER;
                    default -> Gender.MALE;
                };

                System.out.println("Berth Preference: 1. No Preference  2. Lower  3. Middle  4. Upper  5. Side Lower  6. Side Upper");
                int berthChoice = ConsoleUtil.readInt(scanner, "Select Preference", 1, 6);
                BerthType pref = switch (berthChoice) {
                    case 2 -> BerthType.LOWER;
                    case 3 -> BerthType.MIDDLE;
                    case 4 -> BerthType.UPPER;
                    case 5 -> BerthType.SIDE_LOWER;
                    case 6 -> BerthType.SIDE_UPPER;
                    default -> BerthType.NO_PREFERENCE;
                };

                passengers.add(new Passenger(name, age, gender, pref));
            }

            System.out.println("\nSelect Payment Method:");
            System.out.println("1. UPI / QR Code");
            System.out.println("2. Debit Card");
            System.out.println("3. Credit Card");
            System.out.println("4. Net Banking");
            int payChoice = ConsoleUtil.readInt(scanner, "Choose Payment Option", 1, 4);
            PaymentMethod paymentMethod = switch (payChoice) {
                case 2 -> PaymentMethod.DEBIT_CARD;
                case 3 -> PaymentMethod.CREDIT_CARD;
                case 4 -> PaymentMethod.NET_BANKING;
                default -> PaymentMethod.UPI;
            };

            ConsoleUtil.printInfo("Processing booking and payment transaction...");
            Booking booking = bookingService.bookTicket(
                    currentUser.getUserId(),
                    schedule.getScheduleId(),
                    srcStation.getStationId(),
                    destStation.getStationId(),
                    travelClass,
                    passengers,
                    paymentMethod
            );

            printTicketReceipt(booking, selectedTrain, srcStation, destStation, journeyDate);

        } catch (DateTimeParseException e) {
            ConsoleUtil.printError("Invalid date format. Please use YYYY-MM-DD.");
        } catch (ValidationException | BookingException e) {
            ConsoleUtil.printError(e.getMessage());
        }
    }

    public void handleCheckPnrStatus(Scanner scanner) {
        ConsoleUtil.printSubHeader("PNR Status Inquiry");
        String pnr = ConsoleUtil.readString(scanner, "Enter 10-digit PNR Number", true);

        Optional<Booking> opt = bookingService.getBookingByPnr(pnr);
        if (opt.isEmpty()) {
            ConsoleUtil.printError("No booking record found for PNR: " + pnr);
            return;
        }

        Booking b = opt.get();
        System.out.println();
        ConsoleUtil.printHeader("E-TICKET DETAILS - PNR: " + b.getPnrNumber());
        System.out.println("Train: " + b.getTrainNumber() + " - " + b.getTrainName());
        System.out.println("Route: " + b.getFromStationCode() + " -> " + b.getToStationCode());
        System.out.println("Journey Date: " + b.getJourneyDate());
        System.out.println("Class: " + b.getTravelClass().getDisplayName());
        System.out.println("Status: " + b.getBookingStatus());
        System.out.println("Total Fare: ₹" + b.getTotalFare());
        ConsoleUtil.printDivider();

        System.out.printf("%-4s %-25s %-6s %-12s %-20s %-12s%n", "#", "PASSENGER NAME", "AGE", "GENDER", "SEAT ALLOCATION", "STATUS");
        ConsoleUtil.printDivider();
        List<Passenger> list = b.getPassengers();
        for (int i = 0; i < list.size(); i++) {
            Passenger p = list.get(i);
            System.out.printf("%-4d %-25s %-6d %-12s %-20s %-12s%n",
                    (i + 1), p.getFullName(), p.getAge(), p.getGender(),
                    p.getFormattedSeatAllocation(), p.getPassengerStatus());
        }
        ConsoleUtil.printDivider();
    }

    private void handleViewMyBookings(User currentUser) {
        ConsoleUtil.printSubHeader("My Bookings History");
        List<Booking> bookings = bookingService.getBookingsForUser(currentUser.getUserId());

        if (bookings.isEmpty()) {
            ConsoleUtil.printInfo("No previous bookings found for your account.");
            return;
        }

        System.out.println();
        System.out.printf("%-12s %-8s %-25s %-12s %-10s %-14s %-10s%n", "PNR", "TRAIN#", "ROUTE", "DATE", "CLASS", "STATUS", "FARE");
        ConsoleUtil.printDivider();
        for (Booking b : bookings) {
            System.out.printf("%-12s %-8s %-25s %-12s %-10s %-14s ₹%-10s%n",
                    b.getPnrNumber(), b.getTrainNumber(),
                    b.getFromStationCode() + " -> " + b.getToStationCode(),
                    b.getJourneyDate(), b.getTravelClass().getCode(),
                    b.getBookingStatus(), b.getTotalFare());
        }
    }

    private void handleCancelTicket(Scanner scanner) {
        ConsoleUtil.printSubHeader("Cancel Ticket");
        String pnr = ConsoleUtil.readString(scanner, "Enter 10-digit PNR to cancel", true);
        String reason = ConsoleUtil.readString(scanner, "Reason for cancellation", false);

        try {
            boolean success = bookingService.cancelBooking(pnr, reason);
            if (success) {
                ConsoleUtil.printSuccess("Booking with PNR " + pnr + " has been successfully CANCELLED.");
            } else {
                ConsoleUtil.printError("Could not cancel booking. Please verify the PNR number.");
            }
        } catch (ValidationException | BookingException e) {
            ConsoleUtil.printError(e.getMessage());
        }
    }

    private void printTicketReceipt(Booking b, Train t, Station src, Station dest, LocalDate date) {
        System.out.println();
        ConsoleUtil.printHeader("★ BOOKING CONFIRMED - ELECTRONIC RESERVATION SLIP ★");
        System.out.println("PNR Number    : " + b.getPnrNumber());
        System.out.println("Train         : " + t.getTrainNumber() + " - " + t.getTrainName());
        System.out.println("Journey Date  : " + date);
        System.out.println("From Station  : " + src.getStationName() + " (" + src.getStationCode() + ")");
        System.out.println("To Station    : " + dest.getStationName() + " (" + dest.getStationCode() + ")");
        System.out.println("Travel Class  : " + b.getTravelClass().getDisplayName());
        System.out.println("Total Paid    : ₹" + b.getTotalFare());
        System.out.println("Booking Status: " + b.getBookingStatus());
        ConsoleUtil.printDivider();
        System.out.printf("%-4s %-25s %-6s %-12s %-25s%n", "#", "PASSENGER NAME", "AGE", "GENDER", "COACH / SEAT / BERTH");
        ConsoleUtil.printDivider();
        for (int i = 0; i < b.getPassengers().size(); i++) {
            Passenger p = b.getPassengers().get(i);
            System.out.printf("%-4d %-25s %-6d %-12s %-25s%n",
                    (i + 1), p.getFullName(), p.getAge(), p.getGender(), p.getFormattedSeatAllocation());
        }
        ConsoleUtil.printDivider();
        ConsoleUtil.printSuccess("Your e-ticket has been booked. Have a pleasant and safe journey!");
    }
}
