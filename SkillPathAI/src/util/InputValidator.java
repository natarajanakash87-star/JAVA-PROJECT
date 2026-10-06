package util;

import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * Centralized input validation and safe reading helpers. All console
 * input for the application should flow through here so invalid input
 * never crashes the program.
 */
public class InputValidator {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private final Scanner scanner;

    public InputValidator(Scanner scanner) {
        this.scanner = scanner;
    }

    /** Reads a non-empty line, re-prompting on empty input. */
    public String readNonEmptyLine(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine();
            if (line != null && !line.trim().isEmpty()) {
                return line.trim();
            }
            ConsoleUI.printError("Input cannot be empty. Please try again.");
        }
    }

    /** Reads any line, allowing empty (used for optional fields / free text). */
    public String readLine(String prompt) {
        System.out.print(prompt);
        String line = scanner.nextLine();
        return line == null ? "" : line.trim();
    }

    public String readValidEmail(String prompt) {
        while (true) {
            String email = readNonEmptyLine(prompt);
            if (EMAIL_PATTERN.matcher(email).matches()) {
                return email;
            }
            ConsoleUI.printError("Invalid email format. Example: name@example.com");
        }
    }

    public int readInt(String prompt) {
        while (true) {
            String line = readNonEmptyLine(prompt);
            try {
                return Integer.parseInt(line.trim());
            } catch (NumberFormatException e) {
                ConsoleUI.printError("Please enter a valid whole number.");
            }
        }
    }

    public int readIntInRange(String prompt, int min, int max) {
        while (true) {
            int val = readInt(prompt);
            if (val >= min && val <= max) return val;
            ConsoleUI.printError("Please enter a number between " + min + " and " + max + ".");
        }
    }

    public double readDouble(String prompt) {
        while (true) {
            String line = readNonEmptyLine(prompt);
            try {
                return Double.parseDouble(line.trim());
            } catch (NumberFormatException e) {
                ConsoleUI.printError("Please enter a valid number (e.g. 2 or 2.5).");
            }
        }
    }

    public double readPositiveDouble(String prompt) {
        while (true) {
            double val = readDouble(prompt);
            if (val > 0) return val;
            ConsoleUI.printError("Value must be greater than zero.");
        }
    }

    public boolean readYesNo(String prompt) {
        while (true) {
            String line = readNonEmptyLine(prompt + " (y/n): ").toLowerCase();
            if (line.equals("y") || line.equals("yes")) return true;
            if (line.equals("n") || line.equals("no")) return false;
            ConsoleUI.printError("Please answer y or n.");
        }
    }
}
