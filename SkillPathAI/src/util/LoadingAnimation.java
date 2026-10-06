package util;

/**
 * Simple console loading animations and step-based progress simulation
 * using Thread.sleep. Kept short so the demo never feels sluggish.
 */
public class LoadingAnimation {

    /** Prints a labeled step, then a small animated progress bar to a target percentage. */
    public static void step(String label, int targetPercent) {
        System.out.println("> " + label);
        int steps = 4;
        for (int i = 1; i <= steps; i++) {
            int pct = targetPercent * i / steps;
            int filled = pct / 5; // 20-char bar
            String bar = "#".repeat(filled) + "-".repeat(20 - filled);
            System.out.print("\r[" + bar + "] " + pct + "%");
            sleep(120);
        }
        System.out.println();
    }

    public static void spinner(String label, long millis) {
        char[] frames = {'|', '/', '-', '\\'};
        long end = System.currentTimeMillis() + millis;
        int i = 0;
        while (System.currentTimeMillis() < end) {
            System.out.print("\r" + label + " " + frames[i % frames.length]);
            i++;
            sleep(100);
        }
        System.out.println("\r" + label + " done.   ");
    }

    public static void dots(String label, int count) {
        System.out.print("> " + label);
        for (int i = 0; i < count; i++) {
            sleep(150);
            System.out.print(".");
        }
        System.out.println();
    }

    private static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
