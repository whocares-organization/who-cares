package presentation;

import java.util.Scanner;

/**
 * Utility for reading and validating console input in the CLI application.
 */
public class InputHandler {

    /**
     * Creates a new {@code InputHandler}.
     */
    public InputHandler() { }

    private final Scanner scanner = new Scanner(System.in);

    /**
     * Reads a non-empty string from the user, reprompting on empty input.
     *
     * @param message the prompt to show before reading input
     * @return the trimmed, non-empty string entered by the user
     */
    public String readString(String message) {
        while (true) {
            System.out.print(message);
            String input = scanner.nextLine().trim();

            if (!input.isEmpty()) {
                return input;
            }

            System.out.println("Input cannot be empty. Please try again.");
        }
    }

    /**
     * Reads a valid integer from the user, reprompting on invalid input.
     *
     * @param message the prompt to show
     * @return the parsed integer value
     */
    public int readInt(String message) {
        while (true) {
            System.out.print(message);

            String input = scanner.nextLine().trim();

            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid number. Please enter a valid integer.");
            }
        }
    }

    /**
     * Reads an integer within the specified range, inclusive.
     * Reprompts until a value within [min, max] is entered.
     *
     * @param message the prompt to show
     * @param min the minimum accepted value
     * @param max the maximum accepted value
     * @return the integer within the requested range
     */
    public int readIntRange(String message, int min, int max) {
        while (true) {
            int value = readInt(message);

            if (value >= min && value <= max) {
                return value;
            }

            System.out.println("Please enter a number between " + min + " and " + max + ".");
        }
    }

    /**
     * Reads a yes/no input from the user.
     * Accepts 'Y' or 'N' (case-insensitive).
     *
     * @param message the prompt message (Y/N is appended automatically)
     * @return {@code true} for 'Y', {@code false} for 'N'
     */
    public boolean readYesNo(String message) {
        while (true) {
            System.out.print(message + " (Y/N): ");
            String input = scanner.nextLine().trim().toLowerCase();

            if (input.equals("y")) {
                return true;
            }
            if (input.equals("n")) {
                return false;
            }

            System.out.println("Invalid input. Please type 'Y' or 'N'.");
        }
    }

    /**
     * Reads a valid double from the user, reprompting on invalid input.
     *
     * @param message the prompt to show
     * @return the parsed double value
     */
    public double readDouble(String message) {
        while (true) {
            System.out.print(message);

            String input = scanner.nextLine().trim();

            try {
                return Double.parseDouble(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid number. Please enter a valid decimal number.");
            }
            
        }
    }

    /**
     * Reads an optional string input, which may be empty.
     * Returns {@code null} if the trimmed input is empty.
     *
     * @param message the prompt to show
     * @return the trimmed input string or {@code null} if empty
     */
    public String readOptionalString(String message) {
        System.out.print(message);
        String input = scanner.nextLine();
        if (input == null) {
            return null;
        }
        input = input.trim();
        return input.isEmpty() ? null : input;
    }
}