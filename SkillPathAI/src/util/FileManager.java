package util;

import model.*;

import java.io.*;
import java.time.LocalDate;
import java.util.*;

/**
 * Handles all file-based persistence for the application using plain
 * text files under the data/ directory. Every method fails gracefully
 * (never throws out of this class) so a corrupt or missing file cannot
 * crash the app -- callers get back an empty collection instead.
 *
 * The storage format is intentionally simple (pipe-delimited lines) so
 * that migrating to a real database (MySQL/MongoDB) later only requires
 * swapping the implementation of these methods, not the calling code.
 */
public class FileManager {

    private static final String DATA_DIR = "data";
    private static final String STUDENTS_FILE = DATA_DIR + "/students.txt";
    private static final String SKILLS_FILE = DATA_DIR + "/skills.txt";
    private static final String QUIZ_RESULTS_FILE = DATA_DIR + "/quiz_results.txt";
    private static final String PROGRESS_FILE = DATA_DIR + "/progress.txt";
    private static final String ROADMAPS_FILE = DATA_DIR + "/roadmaps.txt";
    private static final String JOB_MARKET_FILE = DATA_DIR + "/job_market.txt";

    public static void ensureDataFiles() {
        try {
            File dir = new File(DATA_DIR);
            if (!dir.exists()) dir.mkdirs();
            for (String f : new String[]{STUDENTS_FILE, SKILLS_FILE, QUIZ_RESULTS_FILE,
                    PROGRESS_FILE, ROADMAPS_FILE, JOB_MARKET_FILE}) {
                File file = new File(f);
                if (!file.exists()) file.createNewFile();
            }
        } catch (IOException e) {
            ConsoleUI.printError("Unable to initialize data directory: " + e.getMessage());
        }
    }

    // ---------------------------------------------------------------
    // Students
    // ---------------------------------------------------------------

    public static void saveStudent(Student s) {
        appendLine(STUDENTS_FILE, s.toFileLine());
    }

    public static List<Student> loadStudents() {
        List<Student> list = new ArrayList<>();
        for (String line : readLines(STUDENTS_FILE)) {
            try {
                list.add(Student.fromFileLine(line));
            } catch (Exception e) {
                ConsoleUI.printWarning("Skipping corrupt student record.");
            }
        }
        return list;
    }

    public static int nextStudentId() {
        int max = 0;
        for (Student s : loadStudents()) max = Math.max(max, s.getStudentId());
        return max + 1;
    }

    // ---------------------------------------------------------------
    // Student Skills
    // ---------------------------------------------------------------

    public static void saveStudentSkill(StudentSkill skill) {
        appendLine(SKILLS_FILE, skill.toFileLine());
    }

    /** Rewrites the whole skills file (used when updating an existing record). */
    public static void saveAllStudentSkills(List<StudentSkill> skills) {
        List<String> lines = new ArrayList<>();
        for (StudentSkill s : skills) lines.add(s.toFileLine());
        writeLines(SKILLS_FILE, lines);
    }

    public static List<StudentSkill> loadStudentSkills(int studentId) {
        List<StudentSkill> all = loadAllStudentSkills();
        List<StudentSkill> result = new ArrayList<>();
        for (StudentSkill s : all) {
            if (s.getStudentId() == studentId) result.add(s);
        }
        return result;
    }

    public static List<StudentSkill> loadAllStudentSkills() {
        List<StudentSkill> list = new ArrayList<>();
        for (String line : readLines(SKILLS_FILE)) {
            try {
                list.add(StudentSkill.fromFileLine(line));
            } catch (Exception e) {
                ConsoleUI.printWarning("Skipping corrupt skill record.");
            }
        }
        return list;
    }

    // ---------------------------------------------------------------
    // Quiz Results
    // ---------------------------------------------------------------

    public static void saveQuizResult(int studentId, QuizResult result) {
        appendLine(QUIZ_RESULTS_FILE, result.toFileLine(studentId));
    }

    // ---------------------------------------------------------------
    // Progress
    // ---------------------------------------------------------------

    public static void saveProgress(Progress p) {
        List<Progress> all = loadAllProgress();
        boolean replaced = false;
        for (int i = 0; i < all.size(); i++) {
            Progress existing = all.get(i);
            if (existing.getStudentId() == p.getStudentId() && existing.getSkill().equalsIgnoreCase(p.getSkill())) {
                all.set(i, p);
                replaced = true;
                break;
            }
        }
        if (!replaced) all.add(p);
        List<String> lines = new ArrayList<>();
        for (Progress pr : all) lines.add(pr.toFileLine());
        writeLines(PROGRESS_FILE, lines);
    }

    public static List<Progress> loadAllProgress() {
        List<Progress> list = new ArrayList<>();
        for (String line : readLines(PROGRESS_FILE)) {
            try {
                list.add(Progress.fromFileLine(line));
            } catch (Exception e) {
                ConsoleUI.printWarning("Skipping corrupt progress record.");
            }
        }
        return list;
    }

    public static List<Progress> loadProgress(int studentId) {
        List<Progress> result = new ArrayList<>();
        for (Progress p : loadAllProgress()) {
            if (p.getStudentId() == studentId) result.add(p);
        }
        return result;
    }

    // ---------------------------------------------------------------
    // Roadmaps
    // ---------------------------------------------------------------

    public static void saveRoadmap(Roadmap r) {
        // One roadmap per student is kept current; replace any existing entry.
        List<String> lines = new ArrayList<>();
        for (String line : readLines(ROADMAPS_FILE)) {
            String[] parts = line.split("\\|", -1);
            if (parts.length > 1 && Integer.parseInt(parts[1]) == r.getStudentId()) continue; // drop old
            lines.add(line);
        }
        lines.add(serializeRoadmap(r));
        writeLines(ROADMAPS_FILE, lines);
    }

    public static Roadmap loadRoadmap(int studentId) {
        for (String line : readLines(ROADMAPS_FILE)) {
            try {
                Roadmap r = deserializeRoadmap(line);
                if (r.getStudentId() == studentId) return r;
            } catch (Exception e) {
                ConsoleUI.printWarning("Skipping corrupt roadmap record.");
            }
        }
        return null;
    }

    private static String serializeRoadmap(Roadmap r) {
        StringBuilder sb = new StringBuilder();
        sb.append(r.getRoadmapId()).append("|").append(r.getStudentId()).append("|")
                .append(r.getCareer()).append("|").append(r.getTotalWeeks()).append("|")
                .append(r.getGeneratedDate()).append("|");
        List<String> weekChunks = new ArrayList<>();
        for (WeeklyPlan w : r.getWeeklyPlans()) {
            String topics = String.join(",", w.getTopics());
            String tasks = String.join(",", w.getTasks());
            weekChunks.add(w.getWeekNumber() + ":" + w.getSkill() + ":" + w.getHours() + ":"
                    + topics + ":" + tasks + ":" + (w.getProject() == null ? "" : w.getProject())
                    + ":" + w.isCompleted());
        }
        sb.append(String.join("##", weekChunks));
        return sb.toString();
    }

    private static Roadmap deserializeRoadmap(String line) {
        String[] p = line.split("\\|", -1);
        int roadmapId = Integer.parseInt(p[0]);
        int studentId = Integer.parseInt(p[1]);
        String career = p[2];
        LocalDate genDate = p.length > 4 ? LocalDate.parse(p[4]) : LocalDate.now();
        List<WeeklyPlan> plans = new ArrayList<>();
        if (p.length > 5 && !p[5].isEmpty()) {
            String[] weekChunks = p[5].split("##");
            for (String chunk : weekChunks) {
                String[] wp = chunk.split(":", -1);
                int weekNum = Integer.parseInt(wp[0]);
                String skill = wp[1];
                double hours = Double.parseDouble(wp[2]);
                List<String> topics = wp[3].isEmpty() ? new ArrayList<>() : new ArrayList<>(Arrays.asList(wp[3].split(",")));
                List<String> tasks = wp[4].isEmpty() ? new ArrayList<>() : new ArrayList<>(Arrays.asList(wp[4].split(",")));
                String project = wp.length > 5 ? wp[5] : "";
                boolean completed = wp.length > 6 && Boolean.parseBoolean(wp[6]);
                WeeklyPlan wpo = new WeeklyPlan(weekNum, skill, hours, topics, tasks, project);
                wpo.setCompleted(completed);
                plans.add(wpo);
            }
        }
        Roadmap r = new Roadmap(roadmapId, studentId, career, plans);
        r.setGeneratedDate(genDate);
        return r;
    }

    // ---------------------------------------------------------------
    // Generic helpers
    // ---------------------------------------------------------------

    public static void appendLine(String path, String line) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(path, true))) {
            bw.write(line);
            bw.newLine();
        } catch (IOException e) {
            ConsoleUI.printError("File write failed for " + path + ": " + e.getMessage());
        }
    }

    public static void writeLines(String path, List<String> lines) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(path, false))) {
            for (String l : lines) {
                bw.write(l);
                bw.newLine();
            }
        } catch (IOException e) {
            ConsoleUI.printError("File write failed for " + path + ": " + e.getMessage());
        }
    }

    public static List<String> readLines(String path) {
        List<String> lines = new ArrayList<>();
        File f = new File(path);
        if (!f.exists()) return lines;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty()) lines.add(line);
            }
        } catch (IOException e) {
            ConsoleUI.printError("File read failed for " + path + ": " + e.getMessage());
        }
        return lines;
    }

    public static void writeTextFile(String path, String content) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(path, false))) {
            bw.write(content);
        } catch (IOException e) {
            ConsoleUI.printError("Unable to write file " + path + ": " + e.getMessage());
        }
    }

    /** Loads simple KEY=VALUE pairs from a .env file, if present. Never throws. */
    public static Map<String, String> loadEnv(String path) {
        Map<String, String> env = new HashMap<>();
        File f = new File(path);
        if (!f.exists()) return env;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#") || !line.contains("=")) continue;
                int idx = line.indexOf('=');
                String key = line.substring(0, idx).trim();
                String value = line.substring(idx + 1).trim();
                env.put(key, value);
            }
        } catch (IOException e) {
            ConsoleUI.printWarning("Could not read .env file (continuing without it).");
        }
        return env;
    }
}
