package com.irctc;

/**
 * Entry point for the IRCTC Railway Reservation System.
 *
 * This class initializes the console application and verifies system startup.
 */
public class Main {

    private static final String APP_NAME = "IRCTC Railway Reservation System";
    private static final String APP_VERSION = "1.0.0-SNAPSHOT";

    public static void main(String[] args) {
        printBanner();
        printWelcomeMessage();
        printStartupConfirmation();
        printExitMessage();
    }

    private static void printBanner() {
        System.out.println("==================================================================");
        System.out.println("            " + APP_NAME);
        System.out.println("                      Version " + APP_VERSION);
        System.out.println("==================================================================");
    }

    private static void printWelcomeMessage() {
        System.out.println("Welcome to the IRCTC Railway Reservation System!");
        System.out.println("Next-generation console interface for railway ticketing operations.");
        System.out.println("------------------------------------------------------------------");
    }

    private static void printStartupConfirmation() {
        System.out.println("[INFO] Application initialized successfully.");
        System.out.println("[INFO] Core scaffolding and layered architecture ready.");
        System.out.println("------------------------------------------------------------------");
    }

    private static void printExitMessage() {
        System.out.println("[INFO] Initial scaffolding run completed. Exiting cleanly.");
        System.out.println("==================================================================");
    }
}
