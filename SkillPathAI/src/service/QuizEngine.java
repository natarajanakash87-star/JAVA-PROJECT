package service;

import model.QuizQuestion;
import model.QuizResult;
import model.StudentSkill;
import util.ConsoleUI;
import util.InputValidator;
import util.LoadingAnimation;

import java.util.*;

/**
 * Runs adaptive skill-verification quizzes. Difficulty increases after a
 * correct answer and decreases after an incorrect one, so two students
 * who "claim" the same self-reported level can end up with very
 * different verified levels -- which is exactly what drives divergent
 * roadmaps later in the pipeline.
 */
public class QuizEngine {

    private static final int QUESTIONS_PER_QUIZ = 5;
    private final Map<String, List<QuizQuestion>> bank = new HashMap<>();
    private final Random random = new Random();

    public QuizEngine() {
        buildQuestionBank();
    }

    public boolean hasQuestionsFor(String skill) {
        return bank.containsKey(skill.toLowerCase());
    }

    /**
     * Runs an adaptive quiz for the given skill and returns the result.
     * Also mutates the supplied StudentSkill with the verified outcome.
     */
    public QuizResult runQuiz(String skill, StudentSkill studentSkill, InputValidator input) {
        List<QuizQuestion> pool = bank.getOrDefault(skill.toLowerCase(), genericPool(skill));

        ConsoleUI.printHeader(skill.toUpperCase() + " SKILL VERIFICATION");

        String difficulty = "Beginner";
        int correct = 0;
        int asked = 0;
        Set<QuizQuestion> used = new HashSet<>();

        for (int i = 1; i <= QUESTIONS_PER_QUIZ; i++) {
            QuizQuestion q = pickQuestion(pool, difficulty, used);
            if (q == null) break; // ran out of unique questions
            used.add(q);
            asked++;

            System.out.println();
            System.out.println("Question " + i + "/" + QUESTIONS_PER_QUIZ + "  [" + difficulty + "]");
            System.out.println();
            System.out.println(q.getQuestion());
            System.out.println();
            String[] opts = q.getOptions();
            for (int o = 0; o < opts.length; o++) {
                System.out.println((o + 1) + ". " + opts[o]);
            }
            int answer = input.readIntInRange("\nAnswer: ", 1, opts.length) - 1;

            if (q.isCorrect(answer)) {
                correct++;
                ConsoleUI.printSuccess("Correct!");
                difficulty = increaseDifficulty(difficulty);
            } else {
                System.out.println("Correct answer was: " + (q.getCorrectAnswer() + 1) + ". " + opts[q.getCorrectAnswer()]);
                difficulty = decreaseDifficulty(difficulty);
            }
        }

        System.out.println();
        LoadingAnimation.dots("Evaluating answers", 3);

        double score = asked == 0 ? 0 : (correct * 100.0) / asked;
        String verifiedLevel = levelFromScore(score);
        double confidence = Math.min(100, score * 0.9 + 10);

        System.out.println();
        ConsoleUI.printSuccess("Verification completed.");
        System.out.println();
        System.out.println("Skill: " + skill);
        System.out.println("Score: " + correct + "/" + asked);
        System.out.println("Accuracy: " + String.format("%.0f%%", score));
        System.out.println("Verified Level: " + verifiedLevel);
        System.out.println("Confidence: " + String.format("%.0f%%", confidence));
        System.out.println();
        ConsoleUI.printSuccess("Skill verified");

        if (studentSkill != null) {
            studentSkill.setQuizScore(correct);
            studentSkill.setVerifiedLevel(verifiedLevel);
            studentSkill.setConfidence(confidence);
            studentSkill.setVerified(true);
        }

        return new QuizResult(skill, asked, correct, score, verifiedLevel);
    }

    private QuizQuestion pickQuestion(List<QuizQuestion> pool, String difficulty, Set<QuizQuestion> used) {
        List<QuizQuestion> candidates = new ArrayList<>();
        for (QuizQuestion q : pool) {
            if (q.getDifficulty().equalsIgnoreCase(difficulty) && !used.contains(q)) candidates.add(q);
        }
        if (candidates.isEmpty()) {
            for (QuizQuestion q : pool) {
                if (!used.contains(q)) candidates.add(q);
            }
        }
        if (candidates.isEmpty()) return null;
        return candidates.get(random.nextInt(candidates.size()));
    }

    private String increaseDifficulty(String current) {
        switch (current) {
            case "Beginner": return "Intermediate";
            case "Intermediate": return "Advanced";
            default: return "Advanced";
        }
    }

    private String decreaseDifficulty(String current) {
        switch (current) {
            case "Advanced": return "Intermediate";
            case "Intermediate": return "Beginner";
            default: return "Beginner";
        }
    }

    private String levelFromScore(double score) {
        if (score <= 40) return "Beginner";
        if (score <= 70) return "Intermediate";
        if (score <= 90) return "Advanced";
        return "Expert";
    }

    private List<QuizQuestion> genericPool(String skill) {
        List<QuizQuestion> pool = new ArrayList<>();
        pool.add(new QuizQuestion("Which of these best describes " + skill + "?",
                new String[]{"A tool/technology relevant to software development", "A cooking technique",
                        "A type of hardware", "None of the above"}, 0, "Beginner"));
        pool.add(new QuizQuestion("How comfortable are you applying " + skill + " to solve a real problem without help?",
                new String[]{"Not at all", "With guidance", "Mostly independently", "Fully independently"}, 2, "Intermediate"));
        pool.add(new QuizQuestion("Which practice is generally considered good practice when using " + skill + "?",
                new String[]{"Ignoring documentation", "Following established conventions and documentation",
                        "Avoiding version control", "Skipping testing entirely"}, 1, "Beginner"));
        pool.add(new QuizQuestion("In a production project, " + skill + " related code should be:",
                new String[]{"Untested and undocumented", "Reviewed, tested, and documented",
                        "Hidden from teammates", "Written once and never revisited"}, 1, "Intermediate"));
        pool.add(new QuizQuestion("An advanced use case of " + skill + " typically involves:",
                new String[]{"Basic syntax only", "Integrating it with other tools/systems effectively",
                        "Avoiding it in larger systems", "Manual repetitive work only"}, 1, "Advanced"));
        return pool;
    }

    private void buildQuestionBank() {
        // ---------------- Java ----------------
        List<QuizQuestion> java = new ArrayList<>();
        java.add(new QuizQuestion("What is the output?\n\nint x = 10;\nSystem.out.println(x++);",
                new String[]{"10", "11", "9", "Error"}, 0, "Beginner"));
        java.add(new QuizQuestion("Which keyword is used to inherit a class in Java?",
                new String[]{"implements", "extends", "inherits", "super"}, 1, "Beginner"));
        java.add(new QuizQuestion("Which collection does NOT allow duplicate elements?",
                new String[]{"ArrayList", "LinkedList", "HashSet", "Vector"}, 2, "Intermediate"));
        java.add(new QuizQuestion("What does the 'final' keyword do when applied to a variable?",
                new String[]{"Makes it static", "Prevents reassignment", "Makes it private", "Deletes it after use"}, 1, "Intermediate"));
        java.add(new QuizQuestion("Which interface must a class implement to be used with a PriorityQueue's natural ordering?",
                new String[]{"Runnable", "Comparable", "Serializable", "Cloneable"}, 1, "Intermediate"));
        java.add(new QuizQuestion("What is the time complexity of retrieving an element by key from a HashMap on average?",
                new String[]{"O(n)", "O(log n)", "O(1)", "O(n^2)"}, 2, "Advanced"));
        java.add(new QuizQuestion("Which of these correctly describes Java's exception hierarchy?",
                new String[]{"Exception extends Error", "Throwable is the superclass of Exception and Error",
                        "RuntimeException is a checked exception", "Error must always be caught"}, 1, "Advanced"));
        java.add(new QuizQuestion("What does 'synchronized' guarantee in Java?",
                new String[]{"Faster execution", "Mutual exclusion for the block/method across threads",
                        "Automatic garbage collection", "Static binding"}, 1, "Advanced"));
        bank.put("java", java);

        // ---------------- Python ----------------
        List<QuizQuestion> python = new ArrayList<>();
        python.add(new QuizQuestion("What is the output of print(2 ** 3)?",
                new String[]{"6", "8", "9", "Error"}, 1, "Beginner"));
        python.add(new QuizQuestion("Which keyword defines a function in Python?",
                new String[]{"func", "def", "function", "lambda"}, 1, "Beginner"));
        python.add(new QuizQuestion("What does a list comprehension like [x*2 for x in range(3)] produce?",
                new String[]{"[0, 2, 4]", "[1, 2, 3]", "[0, 1, 2]", "Error"}, 0, "Intermediate"));
        python.add(new QuizQuestion("Which data structure is immutable in Python?",
                new String[]{"list", "dict", "tuple", "set"}, 2, "Intermediate"));
        python.add(new QuizQuestion("What does the 'self' parameter refer to in a class method?",
                new String[]{"The class itself", "The instance calling the method", "A global variable", "Nothing, it's optional"}, 1, "Intermediate"));
        python.add(new QuizQuestion("What is a Python decorator primarily used for?",
                new String[]{"Styling console output", "Wrapping/extending function behavior", "Declaring variables", "Memory management"}, 1, "Advanced"));
        python.add(new QuizQuestion("What does the GIL (Global Interpreter Lock) primarily affect?",
                new String[]{"Disk I/O", "True parallel execution of threads in CPython", "Network latency", "Import speed"}, 1, "Advanced"));
        bank.put("python", python);

        // ---------------- SQL ----------------
        List<QuizQuestion> sql = new ArrayList<>();
        sql.add(new QuizQuestion("Which statement retrieves data from a table?",
                new String[]{"GET", "SELECT", "FETCH", "SHOW"}, 1, "Beginner"));
        sql.add(new QuizQuestion("Which clause filters rows before grouping?",
                new String[]{"HAVING", "WHERE", "GROUP BY", "ORDER BY"}, 1, "Beginner"));
        sql.add(new QuizQuestion("What is the difference between INNER JOIN and LEFT JOIN?",
                new String[]{"They are identical", "INNER JOIN returns only matching rows; LEFT JOIN also returns unmatched left rows",
                        "LEFT JOIN never returns NULLs", "INNER JOIN is used only for one table"}, 1, "Intermediate"));
        sql.add(new QuizQuestion("Which clause filters groups after aggregation?",
                new String[]{"WHERE", "HAVING", "GROUP BY", "LIMIT"}, 1, "Intermediate"));
        sql.add(new QuizQuestion("What does a PRIMARY KEY constraint guarantee?",
                new String[]{"Values can repeat", "Uniqueness and non-null for the column(s)", "Automatic indexing is disabled", "Foreign key relation"}, 1, "Intermediate"));
        sql.add(new QuizQuestion("What is a correlated subquery?",
                new String[]{"A subquery that references the outer query's columns", "A subquery with no WHERE clause",
                        "A subquery that always runs once", "A join without conditions"}, 0, "Advanced"));
        sql.add(new QuizQuestion("Which normal form eliminates transitive dependencies?",
                new String[]{"1NF", "2NF", "3NF", "0NF"}, 2, "Advanced"));
        bank.put("sql", sql);

        // ---------------- Git ----------------
        List<QuizQuestion> git = new ArrayList<>();
        git.add(new QuizQuestion("Which command creates a new local branch?",
                new String[]{"git branch <name>", "git commit <name>", "git init <name>", "git clone <name>"}, 0, "Beginner"));
        git.add(new QuizQuestion("Which command stages changes for commit?",
                new String[]{"git add", "git push", "git merge", "git status"}, 0, "Beginner"));
        git.add(new QuizQuestion("What does 'git rebase' do differently from 'git merge'?",
                new String[]{"Nothing, they're identical", "Rewrites commit history onto a new base instead of creating a merge commit",
                        "Deletes all branches", "Only works on remote repos"}, 1, "Intermediate"));
        git.add(new QuizQuestion("What is a merge conflict caused by?",
                new String[]{"Two branches changing the same lines differently", "Running git status", "Cloning a repo", "Using .gitignore"}, 0, "Intermediate"));
        git.add(new QuizQuestion("Which command lets you temporarily shelve uncommitted changes?",
                new String[]{"git stash", "git hide", "git pause", "git freeze"}, 0, "Advanced"));
        bank.put("git", git);

        // ---------------- HTML ----------------
        List<QuizQuestion> html = new ArrayList<>();
        html.add(new QuizQuestion("Which tag defines a hyperlink?",
                new String[]{"<link>", "<a>", "<href>", "<url>"}, 1, "Beginner"));
        html.add(new QuizQuestion("Which tag is used for the largest heading?",
                new String[]{"<h6>", "<head>", "<h1>", "<header>"}, 2, "Beginner"));
        html.add(new QuizQuestion("Which attribute provides alternate text for an image?",
                new String[]{"alt", "src", "title only", "text"}, 0, "Intermediate"));
        html.add(new QuizQuestion("Which HTML5 element is used for independent, self-contained content?",
                new String[]{"<div>", "<article>", "<span>", "<section only>"}, 1, "Advanced"));
        bank.put("html", html);

        // ---------------- CSS ----------------
        List<QuizQuestion> css = new ArrayList<>();
        css.add(new QuizQuestion("Which property changes text color?",
                new String[]{"font-color", "text-color", "color", "background"}, 2, "Beginner"));
        css.add(new QuizQuestion("Which selector targets an element with class 'card'?",
                new String[]{"#card", ".card", "*card", "card{}"}, 1, "Beginner"));
        css.add(new QuizQuestion("What does 'flex-direction: column' do in Flexbox?",
                new String[]{"Stacks items vertically", "Stacks items horizontally", "Hides overflow", "Removes spacing"}, 0, "Intermediate"));
        css.add(new QuizQuestion("What is the CSS Box Model order from inside out?",
                new String[]{"Margin, Border, Padding, Content", "Content, Padding, Border, Margin", "Border, Content, Margin, Padding", "Padding, Content, Margin, Border"}, 1, "Advanced"));
        bank.put("css", css);

        // ---------------- JavaScript ----------------
        List<QuizQuestion> js = new ArrayList<>();
        js.add(new QuizQuestion("Which keyword declares a block-scoped variable?",
                new String[]{"var", "let", "global", "static"}, 1, "Beginner"));
        js.add(new QuizQuestion("What does '===' check in JavaScript?",
                new String[]{"Value only", "Value and type", "Type only", "Reference only"}, 1, "Beginner"));
        js.add(new QuizQuestion("What is a Promise used for?",
                new String[]{"Styling", "Handling asynchronous operations", "Declaring classes", "Looping"}, 1, "Intermediate"));
        js.add(new QuizQuestion("What does 'this' refer to inside a regular function called as a method?",
                new String[]{"The global object always", "The object the method was called on", "undefined always", "The function itself"}, 1, "Advanced"));
        bank.put("javascript", js);

        // ---------------- C ----------------
        List<QuizQuestion> c = new ArrayList<>();
        c.add(new QuizQuestion("Which function is the entry point of a C program?",
                new String[]{"start()", "main()", "run()", "init()"}, 1, "Beginner"));
        c.add(new QuizQuestion("What does 'malloc' do?",
                new String[]{"Frees memory", "Allocates memory dynamically", "Declares a constant", "Opens a file"}, 1, "Intermediate"));
        c.add(new QuizQuestion("What is undefined behavior typically caused by?",
                new String[]{"Well-formed standard code", "Accessing memory outside allocated bounds", "Using comments", "Declaring variables"}, 1, "Advanced"));
        bank.put("c", c);

        // ---------------- C++ ----------------
        List<QuizQuestion> cpp = new ArrayList<>();
        cpp.add(new QuizQuestion("Which keyword is used to create a class in C++?",
                new String[]{"class", "struct only", "object", "type"}, 0, "Beginner"));
        cpp.add(new QuizQuestion("What does a destructor do?",
                new String[]{"Creates an object", "Cleans up when an object goes out of scope", "Copies an object", "Compares objects"}, 1, "Intermediate"));
        cpp.add(new QuizQuestion("What is the main benefit of using smart pointers?",
                new String[]{"Faster compilation", "Automatic memory management", "Smaller binaries", "Better syntax highlighting"}, 1, "Advanced"));
        bank.put("c++", cpp);

        // ---------------- React ----------------
        List<QuizQuestion> react = new ArrayList<>();
        react.add(new QuizQuestion("What is used to manage state in a functional component?",
                new String[]{"this.state", "useState", "setState only", "React.state"}, 1, "Beginner"));
        react.add(new QuizQuestion("What does JSX compile down to?",
                new String[]{"HTML directly", "React.createElement calls", "CSS", "JSON"}, 1, "Intermediate"));
        react.add(new QuizQuestion("What is the purpose of useEffect?",
                new String[]{"Styling components", "Running side effects after render", "Declaring components", "Routing"}, 1, "Advanced"));
        bank.put("react", react);

        // ---------------- Spring Boot ----------------
        List<QuizQuestion> spring = new ArrayList<>();
        spring.add(new QuizQuestion("Which annotation marks a class as a REST controller?",
                new String[]{"@Controller only", "@RestController", "@Service", "@Repository"}, 1, "Beginner"));
        spring.add(new QuizQuestion("What does @Autowired do?",
                new String[]{"Compiles the project", "Injects a dependency automatically", "Starts the server", "Creates a database"}, 1, "Intermediate"));
        spring.add(new QuizQuestion("Which annotation defines an application's entry point in Spring Boot?",
                new String[]{"@Entry", "@SpringBootApplication", "@Main", "@Bootstrap"}, 1, "Advanced"));
        bank.put("spring boot", spring);

        // ---------------- Docker ----------------
        List<QuizQuestion> docker = new ArrayList<>();
        docker.add(new QuizQuestion("What file defines how a Docker image is built?",
                new String[]{"docker.yml", "Dockerfile", "compose.json", "image.txt"}, 1, "Beginner"));
        docker.add(new QuizQuestion("What is the difference between an image and a container?",
                new String[]{"No difference", "An image is a blueprint; a container is a running instance of it", "A container is bigger", "An image can only run once"}, 1, "Intermediate"));
        docker.add(new QuizQuestion("What does 'docker-compose' primarily help manage?",
                new String[]{"Single container builds only", "Multi-container applications and their configuration", "Only networking", "Only volumes"}, 1, "Advanced"));
        bank.put("docker", docker);

        // ---------------- AWS ----------------
        List<QuizQuestion> aws = new ArrayList<>();
        aws.add(new QuizQuestion("Which AWS service provides object storage?",
                new String[]{"EC2", "S3", "RDS", "Lambda"}, 1, "Beginner"));
        aws.add(new QuizQuestion("What is AWS Lambda used for?",
                new String[]{"Running serverless functions", "Object storage", "DNS management", "Load testing"}, 0, "Intermediate"));
        aws.add(new QuizQuestion("What does an IAM role primarily control?",
                new String[]{"Billing currency", "Permissions and access to AWS resources", "Server CPU speed", "Network latency"}, 1, "Advanced"));
        bank.put("aws", aws);
    }
}
