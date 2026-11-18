package com.irctc.controller;

import com.irctc.database.DatabaseConnection;
import com.irctc.model.Booking;
import com.irctc.model.Passenger;
import com.irctc.model.RouteStop;
import com.irctc.model.Station;
import com.irctc.model.User;
import com.irctc.service.BookingService;
import com.irctc.service.TrainService;
import com.irctc.util.ConsoleUtil;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * Console controller handling administrator workflows.
 */
public class AdminController {

    private final TrainService trainService;
    private final BookingService bookingService;

    public AdminController(TrainService trainService, BookingService bookingService) {
        this.trainService = trainService;
        this.bookingService = bookingService;
    }

    public void start(User currentUser, Scanner scanner) {
        boolean active = true;

        while (active) {
            ConsoleUtil.printHeader("Admin Portal - " + currentUser.getFullName());
            System.out.println("1. View Stations Directory");
            System.out.println("2. View Train Route & Stopping Halts");
            System.out.println("3. Global PNR Record Inspection");
            System.out.println("4. System Database Health & Connectivity");
            System.out.println("5. Logout to Main Menu");
            ConsoleUtil.printDivider();

            String choice = ConsoleUtil.readString(scanner, "Enter admin option (1-5)", true);
            if (choice.isEmpty()) {
                break;
            }

            switch (choice) {
                case "1" -> handleViewStations();
                case "2" -> handleViewTrainRoute(scanner);
                case "3" -> handleInspectPnr(scanner);
                case "4" -> handleDatabaseHealth();
                case "5" -> {
                    ConsoleUtil.printInfo("Logging out of administrative portal...");
                    active = false;
                }
                default -> ConsoleUtil.printError("Invalid option. Please enter 1-5.");
            }
        }
    }

    private void handleViewStations() {
        ConsoleUtil.printSubHeader("Stations Directory");
        List<Station> stations = trainService.getAllStations();

        if (stations.isEmpty()) {
            ConsoleUtil.printInfo("No stations found. Ensure 02_seed_data.sql is executed.");
            return;
        }

        System.out.println();
        System.out.printf("%-6s %-10s %-32s %-20s %-20s%n", "ID", "CODE", "STATION NAME", "CITY", "STATE");
        ConsoleUtil.printDivider();
        for (Station s : stations) {
            System.out.printf("%-6d %-10s %-32s %-20s %-20s%n",
                    s.getStationId(), s.getStationCode(), s.getStationName(), s.getCity(), s.getState());
        }
    }

    private void handleViewTrainRoute(Scanner scanner) {
        ConsoleUtil.printSubHeader("Inspect Route Halts");
        int trainId = ConsoleUtil.readInt(scanner, "Enter Train ID", 1, 1000);

        List<RouteStop> stops = trainService.getRouteStops(trainId);
        if (stops.isEmpty()) {
            ConsoleUtil.printInfo("No route stops found for train ID: " + trainId);
            return;
        }

        System.out.println();
        System.out.printf("%-4s %-8s %-30s %-10s %-10s %-10s %-4s%n", "SEQ", "CODE", "STATION NAME", "ARRIVE", "DEPART", "TRACK(KM)", "DAY");
        ConsoleUtil.printDivider();
        for (RouteStop rs : stops) {
            System.out.printf("%-4d %-8s %-30s %-10s %-10s %-10d %-4d%n",
                    rs.getStopSequence(), rs.getStationCode(), rs.getStationName(),
                    rs.getArrivalTime() != null ? rs.getArrivalTime().toString() : "Origin",
                    rs.getDepartureTime() != null ? rs.getDepartureTime().toString() : "Terminus",
                    rs.getDistanceFromSourceKm(), rs.getDayOffset());
        }
    }

    private void handleInspectPnr(Scanner scanner) {
        ConsoleUtil.printSubHeader("Inspect PNR Record");
        String pnr = ConsoleUtil.readString(scanner, "Enter PNR", true);

        Optional<Booking> opt = bookingService.getBookingByPnr(pnr);
        if (opt.isEmpty()) {
            ConsoleUtil.printError("PNR not found: " + pnr);
            return;
        }

        Booking b = opt.get();
        System.out.println("\nBooking ID: " + b.getBookingId() + " | PNR: " + b.getPnrNumber());
        System.out.println("User ID: " + b.getUserId() + " | Status: " + b.getBookingStatus());
        System.out.println("Train: " + b.getTrainNumber() + " - " + b.getTrainName());
        System.out.println("Route: " + b.getFromStationCode() + " -> " + b.getToStationCode());
        System.out.println("Date: " + b.getJourneyDate() + " | Class: " + b.getTravelClass());
        System.out.println("Total Fare: ₹" + b.getTotalFare());

        ConsoleUtil.printDivider();
        System.out.printf("%-4s %-25s %-6s %-12s %-20s%n", "#", "NAME", "AGE", "GENDER", "SEAT");
        ConsoleUtil.printDivider();
        for (int i = 0; i < b.getPassengers().size(); i++) {
            Passenger p = b.getPassengers().get(i);
            System.out.printf("%-4d %-25s %-6d %-12s %-20s%n", (i + 1), p.getFullName(), p.getAge(), p.getGender(), p.getFormattedSeatAllocation());
        }
    }

    private void handleDatabaseHealth() {
        ConsoleUtil.printSubHeader("Database Diagnostics");
        boolean configured = DatabaseConnection.isConfigured();
        System.out.println("Configuration Status: " + (configured ? "LOADED (db.properties)" : "NOT LOADED"));
        System.out.println("JDBC Target URL     : " + (DatabaseConnection.getDatabaseUrl() != null ? DatabaseConnection.getDatabaseUrl() : "None"));

        if (configured) {
            boolean active = DatabaseConnection.testConnection(3);
            System.out.println("Live Ping Test (3s) : " + (active ? "HEALTHY (Connection active)" : "FAILED (Cannot connect to MySQL)"));
        } else {
            System.out.println("Diagnostic Note     : " + DatabaseConnection.getConfigurationError());
        }
    }
}
