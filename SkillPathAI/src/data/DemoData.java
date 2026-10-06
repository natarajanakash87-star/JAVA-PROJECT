package data;

import model.Student;
import model.StudentSkill;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides a pre-populated student profile and skill set for Demo Mode,
 * so the full pipeline (verification -> market analysis -> gap analysis
 * -> roadmap -> progress -> regeneration -> AI assistant) can be shown
 * end-to-end without manual data entry.
 */
public class DemoData {

    public static Student demoStudent(int id) {
        return new Student(id, "Demo Student", "demo.student@example.com",
                "B.E Computer Science", "Computer Science", "Demo Institute of Technology",
                3, "Software Developer", 2.0);
    }

    public static List<StudentSkill> demoSkills(int studentId) {
        List<StudentSkill> skills = new ArrayList<>();
        skills.add(new StudentSkill(studentId, "Java", "Intermediate"));
        skills.add(new StudentSkill(studentId, "Python", "Intermediate"));
        skills.add(new StudentSkill(studentId, "SQL", "Beginner"));
        skills.add(new StudentSkill(studentId, "Git", "Beginner"));
        skills.add(new StudentSkill(studentId, "HTML", "Intermediate"));
        skills.add(new StudentSkill(studentId, "CSS", "Beginner"));
        return skills;
    }
}
