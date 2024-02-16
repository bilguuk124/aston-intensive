package com.aston.dz2.entity;

import com.aston.dz2.entity.enums.TeacherRank;
import lombok.*;

import java.util.Objects;

@NoArgsConstructor
@Data
@Builder
@AllArgsConstructor
@ToString
public class Teacher {
    private Long id;
    private String firstName;
    private String lastName;
    private TeacherRank rank;
    private Department department;

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;

        Teacher teacher = (Teacher) object;

        if (!Objects.equals(id, teacher.id)) return false;
        if (!Objects.equals(firstName, teacher.firstName)) return false;
        if (!Objects.equals(lastName, teacher.lastName)) return false;
        if (rank != teacher.rank) return false;
        return Objects.equals(department, teacher.department);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (firstName != null ? firstName.hashCode() : 0);
        result = 31 * result + (lastName != null ? lastName.hashCode() : 0);
        result = 31 * result + (rank != null ? rank.hashCode() : 0);
        result = 31 * result + (department != null ? department.hashCode() : 0);
        return result;
    }
}
