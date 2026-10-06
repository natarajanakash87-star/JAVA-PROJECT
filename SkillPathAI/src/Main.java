import model.*;
import service.*;
import util.*;
import data.DemoData;

import java.util.*;

/**
 * SkillPath AI - Adaptive Career Roadmap Generator
 *
 * Entry point and top-level console flow. Wires together the model,
 * service, and util layers described in the project's intelligence
 * pipeline: Profile -> Verified Skills -> Job Market Data -> Gap
 * Analysis -> Roadmap -> Progress -> Regeneration -> AI Assistant.
 */
public class Main {

    private static final Scanner scanner = new Scanner(System.in);
    private static final InputValidator input = new InputValidator(scanner);

    // Services (stateless / reusable across the session)
    private static final QuizEngine quizEngine = new QuizEngine();
    private static final DependencyEngine dependencyEngine = new DependencyEngine();
    private static final GapAnalysisEngine gapAnalysisEngine = new GapAnalysisEngine(dependencyEngine);
    private static final RoadmapGenerator roadmapGenerator = new RoadmapGenerator(dependencyEngine);
    private static final ProgressTracker progressTracker = new ProgressTracker();
    private static final CareerAssistant careerAssistant = new CareerAssistant();
    private static final InterviewEngine interviewEngine = new InterviewEngine();
    private static final ProjectRecommendationService projectService = new ProjectRecommendationService();
    private static final ReportGenerator reportGenerator = new ReportGenerator();
    private static final JobMarketDataProvider jobMarketProvider = new JobMarketDataProvider();

    // Session state for the logged-in student
    private static Student currentStudent;
    private static List<StudentSkill> currentSkills = new ArrayList<>();
    private static Roadmap currentRoadmap;
    private static List<SkillGap> currentGaps = new ArrayList<>();

    private static final String[] CAREER_OPTIONS = {
            "Software Developer", "Full Stack Developer", "Data Scientist", "Data Analyst",
            "AI/ML Engineer", "Cybersecurity Analyst", "Cloud Engineer", "DevOps Engineer", "Custom Career"
    };

    private static final String[] SKILL_CATALOG = {
            "Python", "C", "C++", "Java", "HTML", "CSS", "JavaScript", "SQL", "Git",
            "React", "Spring Boot", "Docker", "AWS"
    };

    private static final String[] SKILL_LEVELS = {"Beginner", "Intermediate", "Advanced"};

    public static void main(String[] args) {
        FileManager.ensureDataFiles();
        printBoot();
        welcomeMenu();
        System.out.println("\nThank you for using SkillPath AI. Goodbye!");
        scanner.close();
    }

    // =====================================================================
    // Boot / Welcome
    // =====================================================================

    private static void printBoot() {
        ConsoleUI.printLine();
        System.out.println(ConsoleUI.BOLD + "                 SKILLPATH AI v1.0" + ConsoleUI.RESET);
        System.out.println("          Adaptive Career Intelligence System");
        ConsoleUI.printLine();
        System.out.println();
        LoadingAnimation.step("Initializing SkillPath AI...", 100);
        LoadingAnimation.step("Loading career data...", 100);
        ConsoleUI.printSuccess("System Ready!");
        System.out.println();
    }

    private static void welcomeMenu() {
        while (true) {
            System.out.println("Welcome to SkillPath AI\n");
            System.out.println("1. Create Student Profile");
            System.out.println("2. Login");
            System.out.println("3. Demo Mode");
            System.out.println("4. Exit");
            int choice = input.readIntInRange("\nEnter choice: ", 1, 4);

            try {
                switch (choice) {
                    case 1:
                        createProfile();
                        if (currentStudent != null) mainDashboard();
                        break;
                    case 2:
                        login();
                        if (currentStudent != null) mainDashboard();
                        break;
                    case 3:
                        demoMode();
                        mainDashboard();
                        break;
                    case 4:
                        return;
                }
            } catch (Exception e) {
                ConsoleUI.printError("Unexpected error: " + e.getMessage());
                ConsoleUI.printInfo("Returning to main menu.");
            }
            System.out.println();
        }
    }

    // =====================================================================
    // Profile creation / login
    // =====================================================================

    private static void createProfile() {
        ConsoleUI.printHeader("CREATE STUDENT PROFILE");
        String name = input.readNonEmptyLine("Enter Name: ");
        String email = input.readValidEmail("Enter Email: ");
        String degree = input.readNonEmptyLine("Enter Degree: ");
        String branch = input.readNonEmptyLine("Enter Branch: ");
        String college = input.readNonEmptyLine("Enter College: ");
        int year = input.readIntInRange("Enter Current Year: ", 1, 6);

        System.out.println("\nCareer options:");
        for (int i = 0; i < CAREER_OPTIONS.length; i++) {
            System.out.println((i + 1) + ". " + CAREER_OPTIONS[i]);
        }
        int careerChoice = input.readIntInRange("\nEnter Target Career choice: ", 1, CAREER_OPTIONS.length);
        String targetCareer = CAREER_OPTIONS[careerChoice - 1];
        if (targetCareer.equals("Custom Career")) {
            targetCareer = input.readNonEmptyLine("Enter your custom target career: ");
        }

        double hours = input.readPositiveDouble("Enter learning hours per day: ");

        int id = FileManager.nextStudentId();
        Student student = new Student(id, name, email, degree, branch, college, year, targetCareer, hours);
        FileManager.saveStudent(student);

        currentStudent = student;
        currentSkills = new ArrayList<>();
        currentRoadmap = null;
        currentGaps = new ArrayList<>();

        System.out.println();
        ConsoleUI.printSuccess("Profile created successfully! Your Student ID is " + id + ".");
        ConsoleUI.pause(scanner);
    }

    private static void login() {
        ConsoleUI.printHeader("LOGIN");
        List<Student> students = FileManager.loadStudents();
        if (students.isEmpty()) {
            ConsoleUI.printWarning("No student profiles found. Please create one first.");
            ConsoleUI.pause(scanner);
            return;
        }
        String email = input.readNonEmptyLine("Enter your registered Email: ");
        Student found = null;
        for (Student s : students) {
            if (s.getEmail().equalsIgnoreCase(email)) {
                found = s;
                break;
            }
        }
        if (found == null) {
            ConsoleUI.printError("No profile found with that email.");
            ConsoleUI.pause(scanner);
            return;
        }
        currentStudent = found;
        currentSkills = FileManager.loadStudentSkills(found.getStudentId());
        currentRoadmap = FileManager.loadRoadmap(found.getStudentId());
        currentGaps = new ArrayList<>();
        ConsoleUI.printSuccess("Welcome back, " + found.getName() + "!");
        ConsoleUI.pause(scanner);
    }

    // =====================================================================
    // Demo Mode
    // =====================================================================

    private static void demoMode() {
        ConsoleUI.printHeader("DEMO MODE");
        ConsoleUI.printInfo("Loading demo student profile and skills...");
        System.out.println();

        int id = FileManager.nextStudentId();
        currentStudent = DemoData.demoStudent(id);
        FileManager.saveStudent(currentStudent);
        currentSkills = DemoData.demoSkills(id);
        for (StudentSkill s : currentSkills) FileManager.saveStudentSkill(s);
        currentRoadmap = null;
        currentGaps = new ArrayList<>();

        System.out.println("Student:        " + currentStudent.getName());
        System.out.println("Degree:         " + currentStudent.getDegree());
        System.out.println("Target Career:  " + currentStudent.getTargetCareer());
        System.out.println("Learning Time:  " + currentStudent.getHoursPerDay() + " hours/day");
        System.out.println("\nSkills:");
        for (StudentSkill s : currentSkills) {
            System.out.println("  " + s.getSkillName() + " - " + s.getSelfReportedLevel());
        }
        ConsoleUI.pause(scanner);

        ConsoleUI.printHeader("AUTO-DEMO: SKILL VERIFICATION");
        ConsoleUI.printInfo("Automatically verifying each demo skill...\n");
        for (StudentSkill s : currentSkills) {
            if (quizEngine.hasQuestionsFor(s.getSkillName())) {
                QuizResult result = quizEngine.runQuiz(s.getSkillName(), s, input);
                FileManager.saveQuizResult(currentStudent.getStudentId(), result);
                System.out.println();
            }
        }
        FileManager.saveAllStudentSkills(replaceSkillsForStudent(currentStudent.getStudentId(), currentSkills));
        ConsoleUI.pause(scanner);

        ConsoleUI.printHeader("AUTO-DEMO: MARKET ANALYSIS");
        jobMarketAnalysis();

        ConsoleUI.printHeader("AUTO-DEMO: GAP ANALYSIS");
        gapAnalysis();

        ConsoleUI.printHeader("AUTO-DEMO: ROADMAP GENERATION");
        generateRoadmap();

        ConsoleUI.printHeader("AUTO-DEMO: SAMPLE PROGRESS UPDATE");
        if (currentRoadmap != null && !currentRoadmap.getWeeklyPlans().isEmpty()) {
            WeeklyPlan firstWeek = currentRoadmap.getWeeklyPlans().get(0);
            Progress demoProgress = new Progress(currentStudent.getStudentId(), firstWeek.getSkill(), 60, firstWeek.getHours() * 0.5);
            progressTracker.recordProgress(demoProgress);
            System.out.println("Simulated progress: " + firstWeek.getSkill() + " -> 60% complete.");
        }
        ConsoleUI.pause(scanner);

        ConsoleUI.printHeader("AUTO-DEMO: ROADMAP REGENERATION");
        regenerateRoadmap();

        ConsoleUI.printHeader("AUTO-DEMO: AI CAREER ASSISTANT");
        String sampleQuestion = "What should I learn next?";
        System.out.println("You: " + sampleQuestion + "\n");
        System.out.println("AI:\n" + careerAssistant.askQuestion(sampleQuestion, currentStudent, currentRoadmap, currentGaps));
        System.out.println();
        ConsoleUI.printSuccess("Demo complete! You now have full access to the dashboard.");
        ConsoleUI.pause(scanner);
    }

    // =====================================================================
    // Main Dashboard
    // =====================================================================

    private static void mainDashboard() {
        boolean loggedIn = true;
        while (loggedIn) {
            refreshGapsIfPossible();
            ConsoleUI.printHeader("MAIN DASHBOARD");
            System.out.println("Welcome, " + currentStudent.getName() + "!\n");
            System.out.println("Target Career       : " + currentStudent.getTargetCareer());
            System.out.println("Verified Skills     : " + countVerified());
            System.out.println("Skill Gaps          : " + countGaps());
            System.out.println("Roadmap Progress    : " + String.format("%.0f%%", currentRoadmap == null ? 0 : currentRoadmap.overallProgress()));
            System.out.println("Learning Time       : " + currentStudent.getHoursPerDay() + " hours/day");
            ConsoleUI.printThinLine();
            System.out.println("1. View Profile");
            System.out.println("2. Add Skills");
            System.out.println("3. Verify Skills");
            System.out.println("4. Job Market Analysis");
            System.out.println("5. Gap Analysis");
            System.out.println("6. Generate Roadmap");
            System.out.println("7. View Roadmap");
            System.out.println("8. Update Progress");
            System.out.println("9. Regenerate Roadmap");
            System.out.println("10. Interview Preparation");
            System.out.println("11. AI Career Assistant");
            System.out.println("12. Recommended Projects");
            System.out.println("13. View Reports");
            System.out.println("14. Logout");

            int choice = input.readIntInRange("\nEnter choice: ", 1, 14);
            System.out.println();
            try {
                switch (choice) {
                    case 1: viewProfile(); break;
                    case 2: addSkills(); break;
                    case 3: verifySkills(); break;
                    case 4: jobMarketAnalysis(); ConsoleUI.pause(scanner); break;
                    case 5: gapAnalysis(); ConsoleUI.pause(scanner); break;
                    case 6: generateRoadmap(); ConsoleUI.pause(scanner); break;
                    case 7: viewRoadmap(); ConsoleUI.pause(scanner); break;
                    case 8: updateProgress(); break;
                    case 9: regenerateRoadmap(); ConsoleUI.pause(scanner); break;
                    case 10: interviewPreparation(); break;
                    case 11: aiAssistantLoop(); break;
                    case 12: recommendedProjects(); ConsoleUI.pause(scanner); break;
                    case 13: viewReports(); break;
                    case 14:
                        loggedIn = false;
                        currentStudent = null;
                        break;
                }
            } catch (Exception e) {
                ConsoleUI.printError("Something went wrong: " + e.getMessage());
                ConsoleUI.printInfo("No data was lost. Returning to dashboard.");
                ConsoleUI.pause(scanner);
            }
            System.out.println();
        }
    }

    private static int countVerified() {
        int c = 0;
        for (StudentSkill s : currentSkills) if (s.isVerified()) c++;
        return c;
    }

    private static int countGaps() {
        int c = 0;
        for (SkillGap g : currentGaps) if (g.getGap() > 5) c++;
        return c;
    }

    private static void refreshGapsIfPossible() {
        // Keep gaps reasonably fresh without forcing a network call every screen refresh.
    }

    // =====================================================================
    // 1. View Profile
    // =====================================================================

    private static void viewProfile() {
        ConsoleUI.printHeader("STUDENT PROFILE");
        System.out.println("Name        : " + currentStudent.getName());
        System.out.println("Email       : " + currentStudent.getEmail());
        System.out.println("Degree      : " + currentStudent.getDegree());
        System.out.println("Branch      : " + currentStudent.getBranch());
        System.out.println("College     : " + currentStudent.getCollege());
        System.out.println("Year        : " + currentStudent.getYear());
        System.out.println("Career      : " + currentStudent.getTargetCareer());
        System.out.println("Study Time  : " + currentStudent.getHoursPerDay() + " hours/day");
        System.out.println("\nSkills:");
        if (currentSkills.isEmpty()) {
            System.out.println("  (none added yet -- use 'Add Skills')");
        } else {
            for (StudentSkill s : currentSkills) {
                String status = s.isVerified() ? ("Verified: " + s.getVerifiedLevel()) : "Not verified";
                System.out.println("  " + s.getSkillName() + " (self: " + s.getSelfReportedLevel() + ", " + status + ")");
            }
        }
        ConsoleUI.pause(scanner);
    }

    // =====================================================================
    // 2. Add Skills
    // =====================================================================

    private static void addSkills() {
        ConsoleUI.printHeader("ADD SKILLS");
        System.out.println("Available skills:");
        for (int i = 0; i < SKILL_CATALOG.length; i++) {
            System.out.println((i + 1) + ". " + SKILL_CATALOG[i]);
        }
        System.out.println((SKILL_CATALOG.length + 1) + ". Other (custom skill)");

        int choice = input.readIntInRange("\nSelect a skill: ", 1, SKILL_CATALOG.length + 1);
        String skillName = (choice == SKILL_CATALOG.length + 1)
                ? input.readNonEmptyLine("Enter custom skill name: ")
                : SKILL_CATALOG[choice - 1];

        for (StudentSkill s : currentSkills) {
            if (s.getSkillName().equalsIgnoreCase(skillName)) {
                ConsoleUI.printWarning("You've already added " + skillName + ".");
                ConsoleUI.pause(scanner);
                return;
            }
        }

        System.out.println("\nSkill levels:");
        for (int i = 0; i < SKILL_LEVELS.length; i++) System.out.println((i + 1) + ". " + SKILL_LEVELS[i]);
        int levelChoice = input.readIntInRange("\nSelect your self-reported level: ", 1, SKILL_LEVELS.length);

        StudentSkill skill = new StudentSkill(currentStudent.getStudentId(), skillName, SKILL_LEVELS[levelChoice - 1]);
        currentSkills.add(skill);
        FileManager.saveStudentSkill(skill);

        ConsoleUI.printSuccess(skillName + " added (self-reported: " + SKILL_LEVELS[levelChoice - 1] + "). "
                + "Verify it under 'Verify Skills' to make it count toward your roadmap.");
        ConsoleUI.pause(scanner);
    }

    // =====================================================================
    // 3. Verify Skills
    // =====================================================================

    private static void verifySkills() {
        ConsoleUI.printHeader("VERIFY SKILLS");
        List<StudentSkill> unverified = new ArrayList<>();
        for (StudentSkill s : currentSkills) if (!s.isVerified()) unverified.add(s);

        if (unverified.isEmpty()) {
            ConsoleUI.printInfo("All your skills are already verified. Add more skills to verify additional ones.");
            ConsoleUI.pause(scanner);
            return;
        }

        System.out.println("Unverified skills:");
        for (int i = 0; i < unverified.size(); i++) {
            System.out.println((i + 1) + ". " + unverified.get(i).getSkillName());
        }
        int choice = input.readIntInRange("\nSelect a skill to verify: ", 1, unverified.size());
        StudentSkill target = unverified.get(choice - 1);

        QuizResult result = quizEngine.runQuiz(target.getSkillName(), target, input);
        FileManager.saveQuizResult(currentStudent.getStudentId(), result);
        FileManager.saveAllStudentSkills(replaceSkillsForStudent(currentStudent.getStudentId(), currentSkills));

        ConsoleUI.pause(scanner);
    }

    private static List<StudentSkill> replaceSkillsForStudent(int studentId, List<StudentSkill> updated) {
        List<StudentSkill> all = FileManager.loadAllStudentSkills();
        List<StudentSkill> result = new ArrayList<>();
        for (StudentSkill s : all) {
            if (s.getStudentId() != studentId) result.add(s);
        }
        result.addAll(updated);
        return result;
    }

    // =====================================================================
    // 4. Job Market Analysis
    // =====================================================================

    private static void jobMarketAnalysis() {
        ConsoleUI.printHeader("JOB MARKET ANALYSIS");
        System.out.println("Target Role: " + currentStudent.getTargetCareer() + "\n");

        Map<String, Double> demand = jobMarketProvider.getSkillDemand(currentStudent.getTargetCareer());
        List<JobPosting> jobs = jobMarketProvider.getJobs(currentStudent.getTargetCareer());
        boolean live = jobMarketProvider.isLiveData(currentStudent.getTargetCareer());

        if (!live) {
            ConsoleUI.printDemoTag();
        }

        System.out.println("Jobs Analyzed: " + jobs.size() + "\n");
        System.out.println("Skill Demand");
        ConsoleUI.printThinLine();
        System.out.println(ConsoleUI.padRight("Skill", 25) + "Frequency");
        ConsoleUI.printThinLine();

        List<Map.Entry<String, Double>> sorted = new ArrayList<>(demand.entrySet());
        sorted.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));
        for (Map.Entry<String, Double> e : sorted) {
            System.out.println(ConsoleUI.padRight(e.getKey(), 25) + String.format("%.0f%%", e.getValue()));
        }
        ConsoleUI.printThinLine();
        System.out.println("\nLast Updated: " + jobMarketProvider.getLastUpdated());
        System.out.println("Source: " + (live ? "Job Market API (live)" : "Simulated Demo Dataset"));
    }

    // =====================================================================
    // 5. Gap Analysis
    // =====================================================================

    private static void gapAnalysis() {
        ConsoleUI.printHeader("GAP ANALYSIS");
        Map<String, Double> demand = jobMarketProvider.getSkillDemand(currentStudent.getTargetCareer());
        currentGaps = gapAnalysisEngine.analyze(currentSkills, demand);

        System.out.println(ConsoleUI.padRight("Skill", 16) + ConsoleUI.padRight("Verified", 14)
                + ConsoleUI.padRight("Demand", 13) + ConsoleUI.padRight("Gap", 10) + "Status");
        ConsoleUI.printThinLine();
        for (SkillGap g : currentGaps) {
            String statusDisplay;
            switch (g.getStatus()) {
                case "Strong": statusDisplay = ConsoleUI.GREEN + "\u2713 Strong" + ConsoleUI.RESET; break;
                case "Gap": statusDisplay = ConsoleUI.YELLOW + "\u26A0 Gap" + ConsoleUI.RESET; break;
                default: statusDisplay = ConsoleUI.RED + "\u2717 Critical" + ConsoleUI.RESET;
            }
            System.out.println(ConsoleUI.padRight(capitalize(g.getSkill()), 16)
                    + ConsoleUI.padRight(String.format("%.0f%%", g.getSkillLevel()), 14)
                    + ConsoleUI.padRight(String.format("%.0f%%", g.getMarketDemand()), 13)
                    + ConsoleUI.padRight(String.format("%.0f%%", g.getGap()), 10)
                    + statusDisplay);
        }
        ConsoleUI.printThinLine();
    }

    private static String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        String[] words = s.split(" ");
        StringBuilder sb = new StringBuilder();
        for (String w : words) {
            if (!w.isEmpty()) sb.append(Character.toUpperCase(w.charAt(0))).append(w.substring(1)).append(" ");
        }
        return sb.toString().trim();
    }

    // =====================================================================
    // 6 & 7. Roadmap generation / viewing
    // =====================================================================

    private static void generateRoadmap() {
        ConsoleUI.printHeader("PERSONALIZED ROADMAP GENERATION");
        if (currentGaps.isEmpty()) {
            ConsoleUI.printInfo("Running gap analysis first...");
            Map<String, Double> demand = jobMarketProvider.getSkillDemand(currentStudent.getTargetCareer());
            currentGaps = gapAnalysisEngine.analyze(currentSkills, demand);
        }

        LoadingAnimation.step("Loading student profile", 25);
        LoadingAnimation.step("Verifying skills", 50);
        LoadingAnimation.step("Analyzing market demand", 75);
        LoadingAnimation.step("Generating roadmap", 100);
        ConsoleUI.printSuccess("Complete\n");

        int roadmapId = (currentRoadmap != null) ? currentRoadmap.getRoadmapId() : new Random().nextInt(90000) + 10000;
        currentRoadmap = roadmapGenerator.generate(currentStudent, currentGaps, roadmapId);
        FileManager.saveRoadmap(currentRoadmap);

        printRoadmapSummaryAndWeeks();
    }

    private static void viewRoadmap() {
        ConsoleUI.printHeader("PERSONALIZED CAREER ROADMAP");
        if (currentRoadmap == null) {
            currentRoadmap = FileManager.loadRoadmap(currentStudent.getStudentId());
        }
        if (currentRoadmap == null) {
            ConsoleUI.printWarning("No roadmap yet. Generate one first (option 6).");
            return;
        }
        printRoadmapSummaryAndWeeks();
    }

    private static void printRoadmapSummaryAndWeeks() {
        System.out.println("Career: " + currentRoadmap.getCareer() + "\n");
        double weeklyHours = roadmapGenerator.weeklyHoursFor(currentStudent);
        System.out.println("Available Time: " + currentStudent.getHoursPerDay() + " hours/day");
        System.out.println("Weekly Learning Time: " + (int) weeklyHours + " hours");
        System.out.println("\nEstimated Duration: " + currentRoadmap.getTotalWeeks() + " Weeks");

        if (currentRoadmap.getWeeklyPlans().isEmpty()) {
            System.out.println("\nYou have no significant skill gaps right now -- great job! "
                    + "Consider Interview Preparation or Recommended Projects to sharpen further.");
            return;
        }

        for (WeeklyPlan w : currentRoadmap.getWeeklyPlans()) {
            ConsoleUI.printThinLine();
            System.out.println("WEEK " + w.getWeekNumber() + (w.isCompleted() ? "  [COMPLETED]" : ""));
            ConsoleUI.printThinLine();
            System.out.println("\nSkill:\n" + w.getSkill());
            System.out.println("\nHours:\n" + w.getHours());
            System.out.println("\nTopics:");
            for (String t : w.getTopics()) System.out.println("\u2192 " + t);
            System.out.println("\nPractice:");
            for (String t : w.getTasks()) System.out.println(t);
            if (w.getProject() != null && !w.getProject().isEmpty()) {
                System.out.println("\nProject:\n" + w.getProject());
            }
            System.out.println();
        }
        ConsoleUI.printThinLine();
    }

    // =====================================================================
    // 8. Update Progress
    // =====================================================================

    private static void updateProgress() {
        ConsoleUI.printHeader("UPDATE PROGRESS");
        if (currentRoadmap == null || currentRoadmap.getWeeklyPlans().isEmpty()) {
            ConsoleUI.printWarning("Generate a roadmap first so there's something to track.");
            ConsoleUI.pause(scanner);
            return;
        }

        Set<String> skillsInRoadmap = new LinkedHashSet<>();
        for (WeeklyPlan w : currentRoadmap.getWeeklyPlans()) skillsInRoadmap.add(w.getSkill());

        List<String> skillList = new ArrayList<>(skillsInRoadmap);
        System.out.println("Skills in your roadmap:");
        for (int i = 0; i < skillList.size(); i++) System.out.println((i + 1) + ". " + skillList.get(i));
        int choice = input.readIntInRange("\nSelect a skill to update: ", 1, skillList.size());
        String skill = skillList.get(choice - 1);

        double percentage = input.readDouble("Enter completed percentage (0-100): ");
        percentage = Math.max(0, Math.min(100, percentage));
        double hours = input.readPositiveDouble("Enter hours studied so far this week: ");

        Progress progress = new Progress(currentStudent.getStudentId(), skill, percentage, hours);
        progressTracker.recordProgress(progress);

        System.out.println();
        LoadingAnimation.dots("Progress updated", 2);
        System.out.println();

        if (progress.isCompleted()) {
            progressTracker.markSkillCompleted(currentRoadmap, skill);
            FileManager.saveRoadmap(currentRoadmap);
            bumpVerifiedLevelForCompletedSkill(skill);
        }

        // Display overall progress bar
        double overall = currentRoadmap.overallProgress();
        System.out.println(ConsoleUI.padRight(skill, 16) + ConsoleUI.progressBar(percentage, 20));
        System.out.println("\nOverall Roadmap Progress: " + ConsoleUI.progressBar(overall, 20));

        ProgressTracker.Pace pace = progressTracker.evaluatePace(progress, currentRoadmap);
        System.out.println();
        if (pace == ProgressTracker.Pace.STRUGGLING) {
            ConsoleUI.printWarning(skill + " performance is below expected level.");
            System.out.println("\nAdding:\n+4 hours " + skill + " practice");
            addExtraPracticeWeek(skill, 4);
            ConsoleUI.printSuccess("Roadmap adjusted.");
        } else if (pace == ProgressTracker.Pace.AHEAD) {
            ConsoleUI.printSuccess("Performance above expected level.");
            System.out.println("\nAdvanced topics unlocked.");
            boolean shortened = tryShortenRoadmap();
            if (shortened) System.out.println("Roadmap shortened by 1 week.");
        }

        if (input.readYesNo("\nRegenerate full roadmap now based on this update?")) {
            regenerateRoadmap();
        }
        ConsoleUI.pause(scanner);
    }

    private static void bumpVerifiedLevelForCompletedSkill(String skill) {
        for (StudentSkill s : currentSkills) {
            if (s.getSkillName().equalsIgnoreCase(skill)) {
                s.setVerified(true);
                s.setVerifiedLevel(bumpLevel(s.getVerifiedLevel()));
                s.setConfidence(Math.max(s.getConfidence(), 75));
            }
        }
        FileManager.saveAllStudentSkills(replaceSkillsForStudent(currentStudent.getStudentId(), currentSkills));
    }

    private static String bumpLevel(String current) {
        switch (current == null ? "" : current) {
            case "Beginner": return "Intermediate";
            case "Intermediate": return "Advanced";
            case "Advanced": return "Expert";
            case "Expert": return "Expert";
            default: return "Intermediate";
        }
    }

    private static void addExtraPracticeWeek(String skill, double extraHours) {
        int maxWeek = 0;
        for (WeeklyPlan w : currentRoadmap.getWeeklyPlans()) maxWeek = Math.max(maxWeek, w.getWeekNumber());
        WeeklyPlan extra = new WeeklyPlan(maxWeek + 1, skill, extraHours,
                Arrays.asList("Extra Practice", "Targeted Review"),
                Arrays.asList("Additional practice problems focused on weak areas"), "");
        currentRoadmap.getWeeklyPlans().add(extra);
        currentRoadmap.setTotalWeeks(currentRoadmap.getWeeklyPlans().size());
        FileManager.saveRoadmap(currentRoadmap);
    }

    private static boolean tryShortenRoadmap() {
        List<WeeklyPlan> plans = currentRoadmap.getWeeklyPlans();
        for (int i = plans.size() - 1; i >= 0; i--) {
            if (!plans.get(i).isCompleted()) {
                plans.remove(i);
                currentRoadmap.setTotalWeeks(plans.size());
                FileManager.saveRoadmap(currentRoadmap);
                return true;
            }
        }
        return false;
    }

    // =====================================================================
    // 9. Regenerate Roadmap (dynamic regeneration - core feature)
    // =====================================================================

    private static void regenerateRoadmap() {
        ConsoleUI.printHeader("DYNAMIC ROADMAP REGENERATION");
        System.out.println("> Progress updated.\n");
        LoadingAnimation.dots("Rechecking verified skills", 2);
        LoadingAnimation.dots("Recalculating skill gaps", 2);
        LoadingAnimation.dots("Checking market requirements", 2);
        LoadingAnimation.dots("Rechecking dependencies", 2);
        LoadingAnimation.dots("Optimizing weekly schedule", 2);
        System.out.println();

        Set<String> previouslyScheduled = new LinkedHashSet<>();
        if (currentRoadmap != null) {
            for (WeeklyPlan w : currentRoadmap.getWeeklyPlans()) previouslyScheduled.add(w.getSkill().toLowerCase());
        }

        Map<String, Double> demand = jobMarketProvider.getSkillDemand(currentStudent.getTargetCareer());
        currentGaps = gapAnalysisEngine.analyze(currentSkills, demand);

        int roadmapId = (currentRoadmap != null) ? currentRoadmap.getRoadmapId() : new Random().nextInt(90000) + 10000;
        Roadmap newRoadmap = roadmapGenerator.generate(currentStudent, currentGaps, roadmapId);

        Set<String> nowScheduled = new LinkedHashSet<>();
        for (WeeklyPlan w : newRoadmap.getWeeklyPlans()) nowScheduled.add(w.getSkill().toLowerCase());

        for (String skill : previouslyScheduled) {
            if (!nowScheduled.contains(skill)) {
                System.out.println(capitalize(skill) + " \u2713 COMPLETED\n");
                String next = firstUpcomingAfter(newRoadmap, skill);
                if (next != null) {
                    System.out.println("Next dependent skill:\n" + next + "\n");
                }
            }
        }

        currentRoadmap = newRoadmap;
        FileManager.saveRoadmap(currentRoadmap);

        ConsoleUI.printSuccess("Roadmap regenerated.");
        System.out.println("\nNew Estimated Duration: " + currentRoadmap.getTotalWeeks() + " Weeks");
    }

    private static String firstUpcomingAfter(Roadmap roadmap, String completedSkillLower) {
        for (WeeklyPlan w : roadmap.getWeeklyPlans()) {
            if (!w.getSkill().equalsIgnoreCase(completedSkillLower)) {
                return w.getSkill();
            }
        }
        return null;
    }

    // =====================================================================
    // 10. Interview Preparation
    // =====================================================================

    private static void interviewPreparation() {
        ConsoleUI.printHeader("INTERVIEW PREPARATION");

        if (currentGaps.isEmpty()) {
            Map<String, Double> demand = jobMarketProvider.getSkillDemand(currentStudent.getTargetCareer());
            currentGaps = gapAnalysisEngine.analyze(currentSkills, demand);
        }
        System.out.println("Based on your current skill gaps:\n");
        List<String> weakTopics = new ArrayList<>();
        int shown = 0;
        for (SkillGap g : currentGaps) {
            if (g.getGap() <= 5) continue;
            shown++;
            String topic = capitalize(g.getSkill());
            weakTopics.add(topic);
            System.out.println(shown + ". " + topic);
            if (shown >= 5) break;
        }
        if (weakTopics.isEmpty()) {
            System.out.println("(No critical gaps detected -- showing general topics.)");
        }

        boolean browsing = true;
        while (browsing) {
            System.out.println("\nChoose:\n");
            List<String> menuTopics = new ArrayList<>(weakTopics);
            for (int i = 0; i < menuTopics.size(); i++) {
                System.out.println((i + 1) + ". " + menuTopics.get(i) + " Questions");
            }
            int mockOption = menuTopics.size() + 1;
            int skillOption = menuTopics.size() + 2;
            int exitOption = menuTopics.size() + 3;
            System.out.println(mockOption + ". Mock Interview (mixed topics)");
            System.out.println(skillOption + ". Practice by Skill (choose skill & difficulty)");
            System.out.println(exitOption + ". Exit");

            int choice = input.readIntInRange("\nEnter choice: ", 1, exitOption);
            if (choice == exitOption) {
                browsing = false;
            } else if (choice == mockOption) {
                runInterviewSession(interviewEngine.availableTopics().toArray(new String[0]));
            } else if (choice == skillOption) {
                practiceBySkill();
            } else {
                runInterviewSession(new String[]{menuTopics.get(choice - 1)});
            }
        }
    }

    /**
     * Skill-wise interview practice:
     * Select Skill -> Select Difficulty -> Get random questions -> Answer ->
     * Evaluate -> Calculate score -> Display result.
     */
    private static void practiceBySkill() {
        final int QUESTIONS_PER_SESSION = 5;

        // 1. Select a skill
        List<String> skills = interviewEngine.getPracticeSkills();
        ConsoleUI.printHeader("SELECT A SKILL");
        for (int i = 0; i < skills.size(); i++) {
            System.out.println((i + 1) + ". " + skills.get(i));
        }
        int backOption = skills.size() + 1;
        System.out.println(backOption + ". Back");
        int skillChoice = input.readIntInRange("\nEnter your choice: ", 1, backOption);
        if (skillChoice == backOption) return;
        String skill = skills.get(skillChoice - 1);

        // 2. Select difficulty (null = mixed basic -> advanced)
        System.out.println("\nSelect Difficulty:\n");
        System.out.println("1. Basic");
        System.out.println("2. Intermediate");
        System.out.println("3. Advanced");
        System.out.println("4. Mixed (Basic -> Advanced)");
        int levelChoice = input.readIntInRange("\nEnter your choice: ", 1, 4);
        String difficulty = null;
        if (levelChoice == 1) difficulty = InterviewQuestion.BASIC;
        else if (levelChoice == 2) difficulty = InterviewQuestion.INTERMEDIATE;
        else if (levelChoice == 3) difficulty = InterviewQuestion.ADVANCED;

        // 3. Get random questions
        List<InterviewQuestion> questions = interviewEngine.pickQuestions(skill, difficulty, QUESTIONS_PER_SESSION);
        if (questions.isEmpty()) {
            ConsoleUI.printWarning("No questions available for this selection yet.");
            return;
        }

        String levelName = difficulty == null ? "Mixed" : difficulty;
        System.out.println();
        ConsoleUI.printThinLine();
        System.out.println(boldTitle(skill.toUpperCase() + " INTERVIEW (" + levelName + ")"));
        System.out.println(interviewEngine.countQuestions(skill, null) + " questions available in "
                + skill + " -- " + questions.size() + " randomly selected for you.");
        System.out.println("(Press ENTER without typing to skip a question.)");
        ConsoleUI.printThinLine();

        // 4-5. Ask, evaluate, give feedback
        int attempted = 0;
        int correct = 0;
        for (int i = 0; i < questions.size(); i++) {
            InterviewQuestion q = questions.get(i);
            System.out.println("\nQuestion " + (i + 1) + " [" + q.getDifficulty() + "]:\n");
            System.out.println(q.getQuestion());
            String answer = input.readLine("\nYour Answer:\n> ");

            if (answer.isEmpty()) {
                System.out.println("\nSkipped. Try to include concepts like: "
                        + q.getIdealAnswerKeywords().replace(",", ", "));
                continue;
            }
            attempted++;
            if (interviewEngine.isCorrect(q, answer)) correct++;
            System.out.println("\nFeedback:\n" + interviewEngine.evaluateAnswer(q, answer));
        }

        // 6-7. Score and result
        printInterviewResult(skill, levelName, questions.size(), attempted, correct);
        ConsoleUI.pause(scanner);
    }

    private static String boldTitle(String title) {
        return ConsoleUI.BOLD + title + ConsoleUI.RESET;
    }

    private static void printInterviewResult(String skill, String level, int asked, int attempted, int correct) {
        // Score is out of all questions asked, so skipping a question does not help the score
        double score = asked == 0 ? 0 : (correct * 100.0) / asked;
        System.out.println();
        ConsoleUI.printThinLine();
        System.out.println(boldTitle("RESULT"));
        ConsoleUI.printThinLine();
        System.out.println("Skill               : " + skill);
        System.out.println("Difficulty          : " + level);
        System.out.println("Questions Asked     : " + asked);
        System.out.println("Questions Attempted : " + attempted);
        System.out.println("Correct Answers     : " + correct);
        System.out.println("Score               : " + String.format("%.2f%%", score));
        System.out.println(ConsoleUI.progressBar(score, 20));
        System.out.println();
        if (score >= 80) {
            ConsoleUI.printSuccess("Excellent! You are interview ready for " + skill + ".");
        } else if (score >= 50) {
            ConsoleUI.printInfo("Good effort! Review the missed topics and try again.");
        } else {
            ConsoleUI.printWarning("Keep Practicing!");
        }
    }

    private static void runInterviewSession(String[] topics) {
        Random rnd = new Random();
        int rounds = Math.min(3, topics.length == 0 ? 1 : topics.length + 2);
        for (int i = 0; i < rounds; i++) {
            String topic = topics[rnd.nextInt(topics.length)];
            List<InterviewQuestion> questions = interviewEngine.getQuestions(topic);
            if (questions == null || questions.isEmpty()) continue;
            InterviewQuestion q = questions.get(rnd.nextInt(questions.size()));

            ConsoleUI.printThinLine();
            System.out.println("Question:\n\n" + q.getQuestion());
            String answer = input.readLine("\nYour Answer: ");
            String feedback = interviewEngine.evaluateAnswer(q, answer);
            System.out.println("\nFeedback:\n" + feedback + "\n");
        }
        ConsoleUI.pause(scanner);
    }

    // =====================================================================
    // 11. AI Career Assistant
    // =====================================================================

    private static void aiAssistantLoop() {
        ConsoleUI.printHeader("SKILLPATH AI ASSISTANT");
        System.out.println("Ask anything about your roadmap.\n");
        System.out.println("Type 'exit' to return.\n");

        if (currentGaps.isEmpty()) {
            Map<String, Double> demand = jobMarketProvider.getSkillDemand(currentStudent.getTargetCareer());
            currentGaps = gapAnalysisEngine.analyze(currentSkills, demand);
        }

        while (true) {
            String question = input.readLine("You: ");
            if (question.equalsIgnoreCase("exit")) break;
            if (question.isEmpty()) continue;

            LoadingAnimation.spinner("Thinking", 400);
            String answer = careerAssistant.askQuestion(question, currentStudent, currentRoadmap, currentGaps);
            System.out.println("\nAI:\n\n" + answer + "\n");
        }
    }

    // =====================================================================
    // 12. Recommended Projects
    // =====================================================================

    private static void recommendedProjects() {
        ConsoleUI.printHeader("RECOMMENDED PROJECTS");
        if (currentGaps.isEmpty()) {
            Map<String, Double> demand = jobMarketProvider.getSkillDemand(currentStudent.getTargetCareer());
            currentGaps = gapAnalysisEngine.analyze(currentSkills, demand);
        }

        List<ProjectRecommendation> projects = projectService.recommend(currentGaps, 3);
        if (projects.isEmpty()) {
            System.out.println("No specific project gaps detected right now -- you're in great shape!");
            return;
        }

        int idx = 1;
        for (ProjectRecommendation p : projects) {
            System.out.println("Project " + idx++ + ":");
            System.out.println(p.getName() + "\n");
            System.out.println("Skills Covered:");
            for (String s : p.getSkillsCovered()) System.out.println("\u2713 " + s);
            System.out.println("\nGap Coverage:");
            for (Map.Entry<String, String> e : p.getGapCoverage().entrySet()) {
                System.out.println(ConsoleUI.padRight(e.getKey(), 14) + "\u2192 " + e.getValue());
            }
            System.out.println();
        }
    }

    // =====================================================================
    // 13. View Reports
    // =====================================================================

    private static void viewReports() {
        ConsoleUI.printHeader("CAREER READINESS REPORT");
        if (currentGaps.isEmpty()) {
            Map<String, Double> demand = jobMarketProvider.getSkillDemand(currentStudent.getTargetCareer());
            currentGaps = gapAnalysisEngine.analyze(currentSkills, demand);
        }
        double overall = currentRoadmap == null ? 0 : currentRoadmap.overallProgress();
        String report = reportGenerator.buildReport(currentStudent, currentSkills, currentGaps, currentRoadmap, overall);
        System.out.println(report);

        if (input.readYesNo("\nSave this report to career_report.txt?")) {
            reportGenerator.saveToFile(report, "career_report.txt");
            ConsoleUI.printSuccess("Report saved as career_report.txt");
        }
        ConsoleUI.pause(scanner);
    }
}
