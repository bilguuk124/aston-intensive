package com.aston.dz2.entity;

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

    @Override
    public String toString(){
        return "Student{ " +
                "id = " + id +
                ", firstname = " + firstName +
                ", lastname = " + lastName +
                "} ";
    }

    @Override
    public boolean equals(Object obj) {
        try{
            Student other = (Student) obj;
            if (!id.equals(other.id)) return false;
            if (!firstName.equals(other.firstName)) return false;
            if (!lastName.equals(other.lastName)) return false;
            return true;
        } catch (ClassCastException e){
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstName, lastName);
    }
}
