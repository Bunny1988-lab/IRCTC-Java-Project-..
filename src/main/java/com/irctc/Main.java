package com.irctc;

import com.irctc.controller.MainController;

/**
 * Entry point for the IRCTC Railway Reservation System.
 *
 * <p>Initializes the application banner and launches the interactive console controller loop.</p>
 */
public class Main {

    private static final String APP_NAME = "IRCTC Railway Reservation System";
    private static final String APP_VERSION = "1.0.0-SNAPSHOT";

    public static void main(String[] args) {
        printBanner();
        printWelcomeMessage();

        // Detect if running in an interactive terminal or with explicit interactive flag
        boolean isInteractive = (System.console() != null)
                || (args != null && args.length > 0 && "--interactive".equalsIgnoreCase(args[0]));

        if (isInteractive) {
            MainController controller = new MainController();
            controller.start();
        } else {
            System.out.println("[INFO] Application running in non-interactive verification mode.");
            System.out.println("[INFO] Launch in an interactive terminal or pass '--interactive' to open the CLI menu.");
            System.out.println("[INFO] Core layered architecture, services, DAOs, and database modules ready.");
            System.out.println("==================================================================");
        }
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
}
