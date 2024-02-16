package com.aston.dz2.entity;

import com.aston.dz2.entity.enums.Level;
import lombok.*;

import java.util.Objects;

@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
@Builder
public class Student {
    private Long id;
    private String firstName;
    private String lastName;
    private Department department;
    private Level level;
    private Group group;

    public Student(String firstName, String lastName, Department department, Level level){
        this.firstName = firstName;
        this.lastName = lastName;
        this.department = department;
        this.level = level;
    }

    @Override
    public String toString() {
        return "Student{" + "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", department=" + ((department != null) ? department.getName() : "null") +
                ", group= " + ((group != null) ? group.getName() : "null") +
                '}';
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;

        Student student = (Student) object;

        if (!Objects.equals(id, student.id)) return false;
        if (!Objects.equals(firstName, student.firstName)) return false;
        if (!Objects.equals(lastName, student.lastName)) return false;
        return Objects.equals(department, student.department);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (firstName != null ? firstName.hashCode() : 0);
        result = 31 * result + (lastName != null ? lastName.hashCode() : 0);
        result = 31 * result + (department != null ? department.hashCode() : 0);
        return result;
    }
}
