package com.irctc.util;

import java.util.Scanner;

/**
 * Utility helper for formatted console I/O, user prompts, and menu headers.
 */
public final class ConsoleUtil {

    private ConsoleUtil() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated.");
    }

    public static void printHeader(String title) {
        System.out.println();
        System.out.println("==================================================================");
        System.out.println("  " + title.toUpperCase());
        System.out.println("==================================================================");
    }

    public static void printSubHeader(String title) {
        System.out.println();
        System.out.println("--- " + title + " ---");
    }

    public static void printDivider() {
        System.out.println("------------------------------------------------------------------");
    }

    public static void printSuccess(String message) {
        System.out.println("[SUCCESS] " + message);
    }

    public static void printInfo(String message) {
        System.out.println("[INFO] " + message);
    }

    public static void printError(String message) {
        System.out.println("[ERROR] " + message);
    }

    public static String readString(Scanner scanner, String prompt, boolean required) {
        while (true) {
            System.out.print(prompt + ": ");
            if (!scanner.hasNextLine()) {
                return "";
            }
            String input = scanner.nextLine().trim();
            if (required && input.isEmpty()) {
                printError("This field is required. Please enter a value.");
                continue;
            }
            return input;
        }
    }

    public static int readInt(Scanner scanner, String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt + " (" + min + "-" + max + "): ");
            if (!scanner.hasNextLine()) {
                return min;
            }
            String input = scanner.nextLine().trim();
            try {
                int value = Integer.parseInt(input);
                if (value >= min && value <= max) {
                    return value;
                }
                printError("Value must be between " + min + " and " + max + ".");
            } catch (NumberFormatException e) {
                printError("Please enter a valid integer number.");
            }
        }
    }
}
