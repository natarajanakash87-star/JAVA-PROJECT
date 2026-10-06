package util;

import java.util.List;

/**
 * Provides consistent, styled console output: banners, borders, tables,
 * progress bars, and colored status messages. ANSI escape codes are used
 * for color where the terminal supports them; the codes degrade gracefully
 * (as plain text) on terminals that don't render them.
 */
public class ConsoleUI {

    public static final String RESET = "\u001B[0m";
    public static final String BOLD = "\u001B[1m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE = "\u001B[34m";
    public static final String MAGENTA = "\u001B[35m";
    public static final String CYAN = "\u001B[36m";
    public static final String WHITE = "\u001B[37m";

    private static final int WIDTH = 60;

    public static void printLine() {
        System.out.println(CYAN + "=".repeat(WIDTH) + RESET);
    }

    public static void printThinLine() {
        System.out.println("-".repeat(WIDTH));
    }

    public static void printHeader(String title) {
        printLine();
        int pad = Math.max(0, (WIDTH - title.length()) / 2);
        System.out.println(BOLD + " ".repeat(pad) + title + RESET);
        printLine();
    }

    public static void printSubHeader(String title, String subtitle) {
        printLine();
        int pad1 = Math.max(0, (WIDTH - title.length()) / 2);
        int pad2 = Math.max(0, (WIDTH - subtitle.length()) / 2);
        System.out.println(BOLD + " ".repeat(pad1) + title + RESET);
        System.out.println(" ".repeat(pad2) + subtitle);
        printLine();
    }

    public static void printSuccess(String msg) {
        System.out.println(GREEN + "\u2713 " + msg + RESET);
    }

    public static void printError(String msg) {
        System.out.println(RED + "> ERROR: " + msg + RESET);
    }

    public static void printWarning(String msg) {
        System.out.println(YELLOW + "\u26A0 " + msg + RESET);
    }

    public static void printInfo(String msg) {
        System.out.println(BLUE + "> " + msg + RESET);
    }

    public static void printDemoTag() {
        System.out.println(MAGENTA + BOLD + "[DEMO DATA]" + RESET);
        System.out.println("The current job-market results are simulated.");
        System.out.println("They are not live market data.");
        System.out.println();
    }

    /** Renders a block progress bar like: ████████░░░░░░░ 40% */
    public static String progressBar(double percentage, int length) {
        percentage = Math.max(0, Math.min(100, percentage));
        int filled = (int) Math.round((percentage / 100.0) * length);
        int empty = length - filled;
        String color = percentage >= 100 ? GREEN : percentage >= 50 ? YELLOW : RED;
        return color + "\u2588".repeat(filled) + RESET + "\u2591".repeat(empty)
                + String.format(" %3.0f%%", percentage) + (percentage >= 100 ? " " + GREEN + "\u2713" + RESET : "");
    }

    public static void printTableRow(List<String> cols, List<Integer> widths) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < cols.size(); i++) {
            sb.append(padRight(cols.get(i), widths.get(i)));
            sb.append("  ");
        }
        System.out.println(sb.toString());
    }

    public static String padRight(String s, int width) {
        if (s == null) s = "";
        if (s.length() >= width) return s.substring(0, width);
        return s + " ".repeat(width - s.length());
    }

    /**
     * NOTE: intentionally does not read from System.in directly. Mixing raw
     * stream reads with a shared Scanner (used everywhere else for input)
     * can desynchronize buffered input. Use {@link #pause(java.util.Scanner)}
     * instead, which reuses the same Scanner as the rest of the app.
     */
    public static void pause(java.util.Scanner scanner) {
        System.out.println();
        System.out.print("Press ENTER to continue...");
        try {
            scanner.nextLine();
        } catch (Exception ignored) {
        }
    }

    public static void clearScreenSoft() {
        System.out.println("\n".repeat(1));
    }
}
