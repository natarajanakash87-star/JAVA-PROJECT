package service;

import model.Roadmap;
import model.SkillGap;
import model.Student;
import model.WeeklyPlan;
import util.FileManager;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A grounded career chatbot: every answer is built from the student's
 * actual profile, verified skills, current roadmap, and skill gaps --
 * never generic advice.
 *
 * If an ANTHROPIC_API_KEY is present in a local .env file, the assistant
 * will try to enrich its answer using the Claude API (passing the
 * student's real context as grounding). If no key is configured, or the
 * API call fails for any reason (no network, invalid key, etc.), it
 * falls back to a fully offline, rule-based answer built from the same
 * context -- so the feature never breaks the app.
 */
public class CareerAssistant {

    private final Map<String, String> env;

    public CareerAssistant() {
        this.env = FileManager.loadEnv(".env");
    }

    public String askQuestion(String question, Student student, Roadmap roadmap, List<SkillGap> gaps) {
        String apiKey = env.get("ANTHROPIC_API_KEY");
        if (apiKey != null && !apiKey.isBlank()) {
            try {
                return askViaApi(question, student, roadmap, gaps, apiKey);
            } catch (Exception e) {
                // Silent, graceful fallback -- the offline assistant below always works.
            }
        }
        return askOffline(question, student, roadmap, gaps);
    }

    // ------------------------------------------------------------------
    // Offline, rule-based assistant (default / fallback path)
    // ------------------------------------------------------------------

    private String askOffline(String question, Student student, Roadmap roadmap, List<SkillGap> gaps) {
        String q = question.toLowerCase();

        if (q.contains("progress") || q.contains("how am i doing")) {
            if (roadmap == null) return "You don't have a roadmap yet. Generate one first from the main menu (option 6).";
            double pct = roadmap.overallProgress();
            return "Your current roadmap progress is " + String.format("%.0f%%", pct) + " ("
                    + roadmap.getWeeklyPlans().stream().filter(WeeklyPlan::isCompleted).count() + " of "
                    + roadmap.getTotalWeeks() + " weeks completed).";
        }

        if (q.contains("gap") || q.contains("weak") || q.contains("missing")) {
            if (gaps == null || gaps.isEmpty()) return "Run Gap Analysis first so I have data on your skill gaps.";
            StringBuilder sb = new StringBuilder("Based on your verified skills vs. current market demand, your top gaps are:\n\n");
            int shown = 0;
            for (SkillGap g : gaps) {
                if (g.getGap() <= 5) continue;
                shown++;
                sb.append(shown).append(". ").append(capitalize(g.getSkill()))
                        .append(" (gap: ").append(String.format("%.0f%%", g.getGap())).append(")\n");
                if (shown >= 5) break;
            }
            if (shown == 0) return "Great news -- you have no significant skill gaps for " + student.getTargetCareer() + " right now.";
            return sb.toString().trim();
        }

        if (q.contains("after") || q.contains("next")) {
            String mentionedSkill = findMentionedSkill(q, roadmap);
            if (roadmap == null) return "Generate a roadmap first so I can tell you what comes next.";
            List<WeeklyPlan> plans = roadmap.getWeeklyPlans();
            if (mentionedSkill != null) {
                for (int i = 0; i < plans.size(); i++) {
                    if (plans.get(i).getSkill().equalsIgnoreCase(mentionedSkill)) {
                        for (int j = i + 1; j < plans.size(); j++) {
                            if (!plans.get(j).getSkill().equalsIgnoreCase(mentionedSkill)) {
                                return "Based on your verified skills and current roadmap:\n\n1. " + plans.get(j).getSkill()
                                        + "\n\nReason:\nThis follows " + mentionedSkill
                                        + " in your roadmap because it depends on that foundation and addresses your remaining skill gaps.";
                            }
                        }
                    }
                }
            }
            // Fall back to "what's next overall" -- first uncompleted week.
            for (WeeklyPlan w : plans) {
                if (!w.isCompleted()) {
                    return "Your next focus area is " + w.getSkill() + " (Week " + w.getWeekNumber() + ", "
                            + w.getHours() + " hours planned).\n\nTopics:\n- " + String.join("\n- ", w.getTopics());
                }
            }
            return "You've completed every week in your current roadmap! Consider regenerating it to unlock the next tier of skills.";
        }

        if (q.contains("hour") || q.contains("time")) {
            return "You've set aside " + student.getHoursPerDay() + " hours/day for learning. Your roadmap is built around that pace -- "
                    + "increasing your daily hours would shorten the timeline, and decreasing them would lengthen it.";
        }

        if (q.contains("career") || q.contains("role") || q.contains("job")) {
            return "You're currently targeting " + student.getTargetCareer() + ". Your roadmap and skill priorities are all built around "
                    + "closing the gaps between your verified skills and what employers are currently asking for in that role.";
        }

        // Generic but still grounded fallback.
        StringBuilder sb = new StringBuilder();
        sb.append("Here's where things stand for you, ").append(student.getName()).append(":\n\n");
        sb.append("Target Career: ").append(student.getTargetCareer()).append("\n");
        if (roadmap != null) {
            sb.append("Roadmap Progress: ").append(String.format("%.0f%%", roadmap.overallProgress())).append("\n");
        }
        if (gaps != null && !gaps.isEmpty()) {
            long critical = gaps.stream().filter(g -> "Critical".equals(g.getStatus())).count();
            sb.append("Critical Skill Gaps: ").append(critical).append("\n");
        }
        sb.append("\nTry asking things like \"What should I learn after Spring Boot?\", \"What are my biggest gaps?\", or \"How is my progress?\"");
        return sb.toString();
    }

    private String findMentionedSkill(String q, Roadmap roadmap) {
        if (roadmap == null) return null;
        for (WeeklyPlan w : roadmap.getWeeklyPlans()) {
            if (q.contains(w.getSkill().toLowerCase())) return w.getSkill();
        }
        return null;
    }

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    // ------------------------------------------------------------------
    // Optional live LLM enrichment (only used if ANTHROPIC_API_KEY is set)
    // ------------------------------------------------------------------

    private String askViaApi(String question, Student student, Roadmap roadmap, List<SkillGap> gaps, String apiKey) throws Exception {
        StringBuilder context = new StringBuilder();
        context.append("Student profile: name=").append(student.getName())
                .append(", targetCareer=").append(student.getTargetCareer())
                .append(", hoursPerDay=").append(student.getHoursPerDay()).append(".\n");
        if (roadmap != null) {
            context.append("Roadmap progress: ").append(String.format("%.0f", roadmap.overallProgress()))
                    .append("% across ").append(roadmap.getTotalWeeks()).append(" weeks.\n");
        }
        if (gaps != null) {
            context.append("Top skill gaps: ");
            int count = 0;
            for (SkillGap g : gaps) {
                if (g.getGap() <= 5) continue;
                context.append(g.getSkill()).append(" (").append(String.format("%.0f", g.getGap())).append("%), ");
                if (++count >= 5) break;
            }
            context.append("\n");
        }

        String systemPrompt = "You are SkillPath AI's Career Assistant. Answer briefly and concretely, "
                + "using only the student's real context provided. Do not give generic career advice.";
        String userMessage = context + "\nStudent question: " + question;

        String body = "{"
                + "\"model\":\"claude-3-5-haiku-20241022\","
                + "\"max_tokens\":400,"
                + "\"system\":" + jsonString(systemPrompt) + ","
                + "\"messages\":[{\"role\":\"user\",\"content\":" + jsonString(userMessage) + "}]"
                + "}";

        HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(8)).build();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.anthropic.com/v1/messages"))
                .timeout(Duration.ofSeconds(10))
                .header("content-type", "application/json")
                .header("x-api-key", apiKey)
                .header("anthropic-version", "2023-06-01")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new RuntimeException("Career Assistant API returned status " + response.statusCode());
        }

        Matcher m = Pattern.compile("\"text\"\\s*:\\s*\"(.*?)(?<!\\\\)\"", Pattern.DOTALL).matcher(response.body());
        if (m.find()) {
            return unescapeJson(m.group(1));
        }
        throw new RuntimeException("Could not parse Career Assistant API response.");
    }

    private String jsonString(String s) {
        return "\"" + s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n") + "\"";
    }

    private String unescapeJson(String s) {
        return s.replace("\\n", "\n").replace("\\\"", "\"").replace("\\\\", "\\");
    }
}
