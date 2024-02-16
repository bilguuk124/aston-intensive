package com.aston.dz2.entity;

import lombok.*;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;


@Getter
@Setter
@Builder
@NoArgsConstructor
@ToString
@AllArgsConstructor
public class Group {
    private Long id;
    private String name;
    private Department department;
    private Set<Student> students;

    public Group(String name, Department department){
        this.name = name;
        this.department = department;
        students = new LinkedHashSet<>();
    }

    public Group (Long id, String name, Department department){
        this.id = id;
        this.name = name;
        this.department = department;
        students = new LinkedHashSet<>();
    }

    public void addStudent(Student student) {
        students.add(student);
        student.setGroup(this);
    }

    public void removeStudent(Student student) {
        students.remove(student);
        student.setGroup(null);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;

        Group group = (Group) object;

        if (!Objects.equals(id, group.id)) return false;
        if (!Objects.equals(name, group.name)) return false;
        return Objects.equals(department, group.department);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (department != null ? department.hashCode() : 0);
        return result;
    }
}
