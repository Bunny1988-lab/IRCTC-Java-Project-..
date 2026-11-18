package com.irctc.controller;

import com.irctc.enums.Role;
import com.irctc.exception.AuthenticationException;
import com.irctc.exception.ValidationException;
import com.irctc.model.User;
import com.irctc.service.BookingService;
import com.irctc.service.TrainService;
import com.irctc.service.UserService;
import com.irctc.service.impl.BookingServiceImpl;
import com.irctc.service.impl.TrainServiceImpl;
import com.irctc.service.impl.UserServiceImpl;
import com.irctc.util.ConsoleUtil;

import java.util.Scanner;

/**
 * Top-level application controller managing authentication routing and public menu actions.
 */
public class MainController {

    private final UserService userService;
    private final TrainService trainService;
    private final BookingService bookingService;

    private final PassengerController passengerController;
    private final AdminController adminController;

    public MainController() {
        this.userService = new UserServiceImpl();
        this.trainService = new TrainServiceImpl();
        this.bookingService = new BookingServiceImpl();

        this.passengerController = new PassengerController(trainService, bookingService);
        this.adminController = new AdminController(trainService, bookingService);
    }

    public MainController(UserService userService, TrainService trainService, BookingService bookingService) {
        this.userService = userService;
        this.trainService = trainService;
        this.bookingService = bookingService;

        this.passengerController = new PassengerController(trainService, bookingService);
        this.adminController = new AdminController(trainService, bookingService);
    }

    public void start() {
        start(new Scanner(System.in));
    }

    public void start(Scanner scanner) {
        boolean running = true;

        while (running) {
            ConsoleUtil.printHeader("IRCTC Railway Reservation System");
            System.out.println("1. Login (Passenger / Admin)");
            System.out.println("2. Register New Passenger Account");
            System.out.println("3. Quick PNR Status Check");
            System.out.println("4. Search Trains & Timetables");
            System.out.println("5. View Stations Directory");
            System.out.println("6. Exit");
            ConsoleUtil.printDivider();

            System.out.print("Please select an option (1-6): ");
            if (!scanner.hasNextLine()) {
                ConsoleUtil.printInfo("Non-interactive session detected. Exiting cleanly.");
                break;
            }

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> handleLogin(scanner);
                case "2" -> handleRegister(scanner);
                case "3" -> passengerController.handleCheckPnrStatus(scanner);
                case "4" -> passengerController.handleSearchTrains(scanner);
                case "5" -> adminController.start(new User(1L, "guest", "", "Guest User", "guest@example.com", "0000000000", Role.PASSENGER, null, null), scanner);
                case "6" -> {
                    ConsoleUtil.printInfo("Thank you for using the IRCTC Railway Reservation System. Goodbye!");
                    running = false;
                }
                default -> ConsoleUtil.printError("Invalid choice. Please select 1 through 6.");
            }
        }
    }

    private void handleLogin(Scanner scanner) {
        ConsoleUtil.printSubHeader("User Login");
        String username = ConsoleUtil.readString(scanner, "Username", true);
        String password = ConsoleUtil.readString(scanner, "Password", true);

        try {
            User user = userService.login(username, password);
            ConsoleUtil.printSuccess("Login successful! Welcome, " + user.getFullName() + " (" + user.getRole() + ")");

            if (user.getRole() == Role.ADMIN) {
                adminController.start(user, scanner);
            } else {
                passengerController.start(user, scanner);
            }
        } catch (AuthenticationException e) {
            ConsoleUtil.printError(e.getMessage());
        } catch (Exception e) {
            ConsoleUtil.printError("Login failed: " + e.getMessage());
        }
    }

    private void handleRegister(Scanner scanner) {
        ConsoleUtil.printSubHeader("Passenger Registration");
        String username = ConsoleUtil.readString(scanner, "Username (min 3 chars)", true);
        String password = ConsoleUtil.readString(scanner, "Password (min 6 chars)", true);
        String fullName = ConsoleUtil.readString(scanner, "Full Legal Name", true);
        String email = ConsoleUtil.readString(scanner, "Email Address", true);
        String phone = ConsoleUtil.readString(scanner, "Mobile Phone (10 digits)", true);

        try {
            User user = userService.register(username, password, fullName, email, phone, Role.PASSENGER);
            ConsoleUtil.printSuccess("Registration complete! Your user account ID is: " + user.getUserId());
            ConsoleUtil.printInfo("You may now log in using option 1.");
        } catch (ValidationException e) {
            ConsoleUtil.printError(e.getMessage());
        } catch (Exception e) {
            ConsoleUtil.printError("Registration failed: " + e.getMessage());
        }
    }
}
