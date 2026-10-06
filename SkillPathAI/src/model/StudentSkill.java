package model;

/**
 * Represents a skill claimed and/or verified for a specific student.
 * The self-reported level is what the student claims; the verified level
 * is derived from adaptive quiz performance and should be trusted for
 * downstream analysis (gap analysis, roadmap generation, etc.).
 */
public class StudentSkill {
    private int studentId;
    private String skillName;
    private String selfReportedLevel;
    private String verifiedLevel;
    private int quizScore;
    private double confidence;
    private boolean verified;

    public StudentSkill(int studentId, String skillName, String selfReportedLevel) {
        this.studentId = studentId;
        this.skillName = skillName;
        this.selfReportedLevel = selfReportedLevel;
        this.verifiedLevel = "Unverified";
        this.quizScore = 0;
        this.confidence = 0.0;
        this.verified = false;
    }

    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }

    public String getSkillName() { return skillName; }
    public void setSkillName(String skillName) { this.skillName = skillName; }

    public String getSelfReportedLevel() { return selfReportedLevel; }
    public void setSelfReportedLevel(String selfReportedLevel) { this.selfReportedLevel = selfReportedLevel; }

    public String getVerifiedLevel() { return verifiedLevel; }
    public void setVerifiedLevel(String verifiedLevel) { this.verifiedLevel = verifiedLevel; }

    public int getQuizScore() { return quizScore; }
    public void setQuizScore(int quizScore) { this.quizScore = quizScore; }

    public double getConfidence() { return confidence; }
    public void setConfidence(double confidence) { this.confidence = confidence; }

    public boolean isVerified() { return verified; }
    public void setVerified(boolean verified) { this.verified = verified; }

    /**
     * Converts a level name into a normalized numeric strength (0-100),
     * used across gap analysis and roadmap generation.
     */
    public double numericLevel() {
        String lvl = verified ? verifiedLevel : selfReportedLevel;
        if (lvl == null) return 0;
        switch (lvl.toLowerCase()) {
            case "expert": return 100;
            case "advanced": return 85;
            case "intermediate": return 55;
            case "beginner": return 25;
            default: return 0;
        }
    }

    public String toFileLine() {
        return studentId + "|" + skillName + "|" + selfReportedLevel + "|" + verifiedLevel + "|"
                + quizScore + "|" + confidence + "|" + verified;
    }

    public static StudentSkill fromFileLine(String line) {
        String[] p = line.split("\\|", -1);
        StudentSkill s = new StudentSkill(Integer.parseInt(p[0]), p[1], p[2]);
        s.verifiedLevel = p[3];
        s.quizScore = Integer.parseInt(p[4]);
        s.confidence = Double.parseDouble(p[5]);
        s.verified = Boolean.parseBoolean(p[6]);
        return s;
    }

    @Override
    public String toString() {
        return skillName + " [self=" + selfReportedLevel + ", verified=" + verifiedLevel + "]";
    }
}
