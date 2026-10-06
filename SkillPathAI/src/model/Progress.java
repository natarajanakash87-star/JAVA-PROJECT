package model;

/**
 * Tracks a student's self-reported progress on a specific skill within
 * the roadmap.
 */
public class Progress {
    private int studentId;
    private String skill;
    private double percentage;
    private double hoursSpent;
    private boolean completed;

    public Progress(int studentId, String skill, double percentage, double hoursSpent) {
        this.studentId = studentId;
        this.skill = skill;
        this.percentage = percentage;
        this.hoursSpent = hoursSpent;
        this.completed = percentage >= 100.0;
    }

    public int getStudentId() { return studentId; }
    public String getSkill() { return skill; }
    public double getPercentage() { return percentage; }
    public void setPercentage(double percentage) {
        this.percentage = percentage;
        this.completed = percentage >= 100.0;
    }
    public double getHoursSpent() { return hoursSpent; }
    public void setHoursSpent(double hoursSpent) { this.hoursSpent = hoursSpent; }
    public boolean isCompleted() { return completed; }

    public String toFileLine() {
        return studentId + "|" + skill + "|" + percentage + "|" + hoursSpent + "|" + completed;
    }

    public static Progress fromFileLine(String line) {
        String[] p = line.split("\\|", -1);
        Progress pr = new Progress(Integer.parseInt(p[0]), p[1], Double.parseDouble(p[2]), Double.parseDouble(p[3]));
        return pr;
    }
}
