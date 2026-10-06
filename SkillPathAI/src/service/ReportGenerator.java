package service;

import model.*;
import util.FileManager;

import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Builds the Career Readiness Report shown on-screen and optionally
 * saved to career_report.txt.
 */
public class ReportGenerator {

    public String buildReport(Student student, List<StudentSkill> verifiedSkills, List<SkillGap> gaps,
                               Roadmap roadmap, double overallProgress) {
        long strong = gaps.stream().filter(g -> "Strong".equals(g.getStatus())).count();
        long partial = gaps.stream().filter(g -> "Gap".equals(g.getStatus())).count();
        long critical = gaps.stream().filter(g -> "Critical".equals(g.getStatus())).count();
        long verifiedCount = verifiedSkills.stream().filter(StudentSkill::isVerified).count();

        StringBuilder sb = new StringBuilder();
        sb.append("============================================================\n");
        sb.append("                CAREER READINESS REPORT\n");
        sb.append("============================================================\n\n");
        sb.append("Student: ").append(student.getName()).append("\n");
        sb.append("Career: ").append(student.getTargetCareer()).append("\n\n");
        sb.append("Verified Skills: ").append(verifiedCount).append("\n");
        sb.append("Strong Skills: ").append(strong).append("\n");
        sb.append("Partial Gaps: ").append(partial).append("\n");
        sb.append("Critical Gaps: ").append(critical).append("\n\n");
        sb.append("Market Skills Analyzed: ").append(gaps.size()).append("\n\n");
        sb.append("Roadmap Duration: ").append(roadmap == null ? 0 : roadmap.getTotalWeeks()).append(" Weeks\n\n");
        sb.append("Overall Progress: ").append(String.format("%.0f%%", overallProgress)).append("\n\n");
        sb.append("------------------------------------------------------------\n");
        sb.append("TOP SKILL GAPS\n");
        sb.append("------------------------------------------------------------\n\n");

        int shown = 0;
        for (SkillGap g : gaps) {
            if (g.getGap() <= 5) continue;
            shown++;
            sb.append(shown).append(". ").append(capitalize(g.getSkill())).append("\n");
            if (shown >= 5) break;
        }
        if (shown == 0) sb.append("None -- you're market ready for your target skills!\n");

        sb.append("\n------------------------------------------------------------\n\n");
        sb.append("Recommended Next Step:\n\n");
        if (shown > 0) {
            SkillGap top = gaps.stream().filter(g -> g.getGap() > 5).findFirst().orElse(null);
            if (top != null) {
                sb.append("Complete ").append(capitalize(top.getSkill())).append(" fundamentals.\n");
            }
        } else {
            sb.append("Consider exploring an advanced specialization or interview prep.\n");
        }

        sb.append("\nGenerated: ").append(java.time.LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))).append("\n");
        return sb.toString();
    }

    public void saveToFile(String content, String path) {
        FileManager.writeTextFile(path, content);
    }

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }
}
