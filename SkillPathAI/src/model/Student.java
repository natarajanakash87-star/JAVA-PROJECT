package model;

import java.io.Serializable;

/**
 * Represents a student's profile within SkillPath AI.
 */
public class Student implements Serializable {
    private int studentId;
    private String name;
    private String email;
    private String degree;
    private String branch;
    private String college;
    private int year;
    private String targetCareer;
    private double hoursPerDay;

    public Student() {
    }

    public Student(int studentId, String name, String email, String degree, String branch,
                   String college, int year, String targetCareer, double hoursPerDay) {
        this.studentId = studentId;
        this.name = name;
        this.email = email;
        this.degree = degree;
        this.branch = branch;
        this.college = college;
        this.year = year;
        this.targetCareer = targetCareer;
        this.hoursPerDay = hoursPerDay;
    }

    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getDegree() { return degree; }
    public void setDegree(String degree) { this.degree = degree; }

    public String getBranch() { return branch; }
    public void setBranch(String branch) { this.branch = branch; }

    public String getCollege() { return college; }
    public void setCollege(String college) { this.college = college; }

    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }

    public String getTargetCareer() { return targetCareer; }
    public void setTargetCareer(String targetCareer) { this.targetCareer = targetCareer; }

    public double getHoursPerDay() { return hoursPerDay; }
    public void setHoursPerDay(double hoursPerDay) { this.hoursPerDay = hoursPerDay; }

    /**
     * Serializes this student to a single pipe-delimited line for file storage.
     */
    public String toFileLine() {
        return studentId + "|" + name + "|" + email + "|" + degree + "|" + branch + "|"
                + college + "|" + year + "|" + targetCareer + "|" + hoursPerDay;
    }

    public static Student fromFileLine(String line) {
        String[] p = line.split("\\|", -1);
        Student s = new Student();
        s.studentId = Integer.parseInt(p[0]);
        s.name = p[1];
        s.email = p[2];
        s.degree = p[3];
        s.branch = p[4];
        s.college = p[5];
        s.year = Integer.parseInt(p[6]);
        s.targetCareer = p[7];
        s.hoursPerDay = Double.parseDouble(p[8]);
        return s;
    }

    @Override
    public String toString() {
        return "Student{" + "id=" + studentId + ", name='" + name + "', career='" + targetCareer + "'}";
    }
}
