SkillPath AI
Adaptive Career Roadmap Generator
SkillPath AI is an adaptive career roadmap system that generates personalized learning paths using verified skills, job-market demand, skill dependencies, available learning time, and continuous progress.

A pure Java console application — no frameworks, no browser, no build tool required. Just javac and java.

1. Problem Statement
Most "career roadmap" tools show every student the same fixed checklist for a given career. That's not useful: a student who already knows Java deeply and one who has never written a class both get told "Learn Java, then Spring Boot, then Docker..." in the same order, over the same number of weeks.

Two real problems make that approach unreliable:

Self-reported skill is not trustworthy. A student who rates themselves "Intermediate" in SQL might really be a beginner, or might be underselling real expertise.
Job requirements change constantly. A roadmap built from a professor's memory of "what employers want" in 2020 is stale.
2. Solution
SkillPath AI builds a roadmap from live data about the student, not assumptions:

Student Profile + Verified Skills + Target Career + Available Learning Time
                    + Real Job-Market Skill Demand + Learning Progress
                                        |
                                        v
                          Personalized, Continuously-Updated Roadmap
Skills are verified with an adaptive quiz, not just self-reported.
Market demand is pulled from a live public jobs API when reachable (with a clearly labeled simulated fallback when it isn't).
The roadmap generator computes priority scores (demand × gap × dependency importance), respects a skill dependency graph (you can't schedule Spring Boot before OOP), and sizes the number of weeks to the student's actual hours/day — never a fixed 12-week template.
As the student logs progress, the roadmap regenerates: completed skills unlock their dependents, struggling skills get extra practice time, and students who are ahead get their timeline shortened.
3. Key Features
#	Feature	Description
1	Student Profile	Degree, branch, college, year, target career, learning hours/day
2	Skill Management	Add skills from a catalog or custom entries, with a self-reported level
3	Adaptive Skill Verification	Quiz difficulty rises/falls per answer; final score maps to Beginner → Expert
4	Job Market Analysis	Live API (Remotive) with automatic fallback to labeled demo data
5	Gap Analysis	Verified skill vs. market demand, with computed priority scores
6	Dependency Engine	Topological ordering so prerequisites are always scheduled first
7	Roadmap Generator	Week-by-week plan sized to the student's real gaps and hours/day
8	Progress Tracking	Per-skill % complete and hours spent, with visual progress bars
9	Dynamic Regeneration	Re-runs the whole pipeline when progress changes
10	AI Career Assistant	Chatbot grounded in the student's real profile/roadmap/gaps
11	Interview Preparation	Questions targeted at the student's weak areas, plus Practice by Skill: 14 skills x Basic/Intermediate/Advanced questions, random selection, feedback, score & result
12	Project Recommendations	Projects chosen by overlap with current skill gaps
13	Career Readiness Report	Summary report, exportable to career_report.txt
14	Demo Mode	Runs the entire pipeline automatically for quick evaluation
4. Novelty — Why Two Students Get Different Roadmaps
Student A                          Student B
Software Developer                 Software Developer
Verified Java = Advanced           Verified Java = Beginner
SQL = Intermediate                 SQL = Beginner
Learning = 3 hrs/day                Learning = 1 hr/day
        |                                   |
        v                                   v
Roadmap A: shorter Java review,     Roadmap B: full Java + OOP
straight into Spring Boot,          foundation, SQL basics,
backend project focus               Spring Boot scheduled later,
                                     longer overall timeline
Every number in this comparison — which skills appear, in what order, and over how many weeks — is computed from SkillGap priority scores and the dependency graph. There is no if (career.equals("Software Developer")) showFixedRoadmap(); anywhere in the codebase.

5. Architecture
                STUDENT
                   |
                   v
            PROFILE INPUT
                   |
                   v
           SELF-REPORTED SKILLS
                   |
                   v
           ADAPTIVE QUIZ (QuizEngine)
                   |
                   v
          VERIFIED SKILL STATE
                   |
         +---------+---------+
         v                   v
  JOB MARKET DATA      DEPENDENCIES
  (JobMarketDataProvider)  (DependencyEngine)
         |                   |
         +---------+---------+
                   v
          GAP ANALYSIS (GapAnalysisEngine)
                   |
                   v
         PRIORITY CALCULATION
                   |
                   v
        ROADMAP GENERATOR (RoadmapGenerator)
                   |
                   v
             WEEKLY ROADMAP
                   |
                   v
         PROGRESS TRACKING (ProgressTracker)
                   |
                   v
         ROADMAP REGENERATION
Package layout
SkillPathAI/
├── src/
│   ├── Main.java                 # Entry point, menus, dashboard flow
│   ├── model/                    # Plain data classes (Student, Skill, Roadmap, ...)
│   ├── service/                  # Business logic (QuizEngine, RoadmapGenerator, ...)
│   ├── util/                     # ConsoleUI, InputValidator, FileManager, LoadingAnimation
│   └── data/
│       ├── DemoData.java         # Preloaded profile/skills for Demo Mode
│       └── QuestionBank.java     # Interview questions per skill & difficulty
├── data/                         # Runtime file storage (auto-created if missing)
│   ├── students.txt
│   ├── skills.txt
│   ├── quiz_results.txt
│   ├── progress.txt
│   ├── roadmaps.txt
│   └── job_market.txt
├── .env.example
└── README.md
6. Technologies
Java 17+ (standard library only — no external dependencies, no build tool)
java.net.http.HttpClient for the live Job Market API and optional Claude API integration
Plain-text file persistence (File, FileReader/FileWriter, BufferedReader/BufferedWriter) — designed so a real database (MySQL/MongoDB) can be swapped in later by replacing FileManager's internals only
7. OOP Concepts Demonstrated
Encapsulation — every model class exposes state only via getters/setters
Interfaces & Polymorphism — JobMarketService is implemented by both LiveJobMarketService and MockJobMarketService; the rest of the app depends only on the interface
Abstraction — services (RoadmapGenerator, GapAnalysisEngine, etc.) hide their internal algorithms behind simple public methods
Composition — RoadmapGenerator and GapAnalysisEngine both depend on DependencyEngine rather than duplicating dependency logic
Exception handling — a dedicated JobMarketException, plus try/catch around all file I/O, network calls, and user input
8. Algorithms
Adaptive quiz difficulty adjustment (state machine: Beginner ↔ Intermediate ↔ Advanced)
Topological sort over the skill dependency graph (DependencyEngine.topologicalOrder)
Priority scoring: Priority = MarketDemand × SkillGap × DependencyImportance
Dynamic week estimation: weeks = ceil(neededHours / (hoursPerDay × learningDaysPerWeek))
Pace evaluation comparing actual vs. expected progress-per-hour to trigger regeneration
9. Java Collections in Use
Collection	Where	Why
ArrayList	Roadmap weeks, quiz question pools	Ordered, indexable sequences
HashMap	Market skill frequency, dependency graph	Fast key lookup
HashSet	De-duplicating skills per job posting, topological sort visited-set	No duplicates
PriorityQueue	GapAnalysisEngine.priorityQueue	Pull highest-priority skill gap first
10. Installation & Running
Requires a JDK (Java 17 or newer). No external dependencies, no Maven/Gradle needed.

Linux / macOS:

find src -name "*.java" > sources.txt
javac -d out @sources.txt
java -cp out Main
Windows (PowerShell/cmd), or if your OS doesn't support the wildcard glob:

dir /s /b src\*.java > sources.txt
javac -d out @sources.txt
java -cp out Main
If your platform supports it, the shorter form also works:

javac -d out src/*.java src/model/*.java src/service/*.java src/util/*.java src/data/*.java
java -cp out Main
The app creates a data/ folder automatically on first run if it doesn't already exist.

Optional: enabling the live AI Career Assistant
Copy .env.example to .env in the same directory you run java from, and set ANTHROPIC_API_KEY. Without it, the AI Career Assistant automatically uses its built-in offline, rule-based engine — grounded in your real profile, roadmap, and skill gaps either way.

11. Demo Instructions
From the welcome menu, choose 3. Demo Mode. This loads a pre-built student profile and skill set, then automatically walks through the entire pipeline — skill verification, market analysis, gap analysis, roadmap generation, a sample progress update, roadmap regeneration, and one AI Assistant question — before dropping you into the main dashboard to explore manually.

Interview Practice by Skill
10. Interview Preparation -> Practice by Skill:

Select Skill -> Select Difficulty -> 5 random questions -> Answer -> Feedback -> Score -> Result
Skills: C, C++, Java, Python, HTML, CSS, JavaScript, SQL, Data Structures, DBMS, Operating Systems, Computer Networks, Machine Learning, Artificial Intelligence. An answer counts as correct when it covers at least 50% of the question's expected keywords (InterviewEngine.PASS_PERCENT).

Adding a new skill: in data/QuestionBank.java (1) add the name to SKILLS, (2) write an addMySkill() method using q("MySkill", BASIC, "Question?", "keyword1,keyword2"), (3) call it from getAllQuestions(). The menus update automatically.

12. Screenshots
(Add terminal screenshots here when presenting — e.g. the boot banner, adaptive quiz, gap analysis table, and weekly roadmap output.)

13. Future Enhancements
Swap file-based storage for MySQL or MongoDB (the FileManager interface was designed for this)
Replace the keyword-based interview answer grading with an LLM-based evaluator
Add a richer live job-market source with structured "required skills" fields instead of keyword-scanning descriptions
Multi-student cohort analytics for instructors/mentors
Persist a roadmap version history so regenerations can be diffed and reviewed over time
