package com.aston.dz2.entity;

import com.aston.dz2.entity.enums.Semester;
import lombok.*;

import java.util.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Discipline {
    private Long id;
    private String name;
    private Teacher lecturer;
    private Teacher assistant;
    private Semester semester;
    private Set<Group> groups;
    private List<StudentDisciplineScore> scores;

    public Discipline(String name, Teacher lecturer, Teacher assistant, Semester semester){
        this.name = name;
        this.lecturer = lecturer;
        this.assistant = assistant;
        this.semester = semester;
        groups = new LinkedHashSet<>();
        scores = new ArrayList<>();
    }

    public void addGroup(Group group){
        groups.add(group);
        for(Student student : group.getStudents()){
            scores.add(new StudentDisciplineScore(student, this));
        }
    }

    public void removeGroup(Group group){
        groups.remove(group);
        scores.removeIf(score -> group.getStudents().contains(score.getStudent()));
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;

        Discipline that = (Discipline) object;

        if (!Objects.equals(id, that.id)) return false;
        if (!Objects.equals(name, that.name)) return false;
        if (!Objects.equals(lecturer, that.lecturer)) return false;
        if (!Objects.equals(assistant, that.assistant)) return false;
        return semester == that.semester;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (lecturer != null ? lecturer.hashCode() : 0);
        return result;
    }

    @Override
    public String toString(){
        return "Discipline: {" +
                "\nid: " + id +
                "\nname: " + name +
                "\nlecturer: " + lecturer.getFirstName() + " " + lecturer.getLastName() +
                "\nassistant: " + assistant.getFirstName() + " " + assistant.getLastName() +
                "\nsemester: " + semester.getStringValue() +
                "\n}";
    }
}
