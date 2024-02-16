package com.aston.dz2.entity;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@ToString
@AllArgsConstructor
public class StudentDisciplineScore {
    private Student student;
    private Discipline discipline;
    private int score;

    public StudentDisciplineScore(Student student, Discipline discipline){
        this.student = student;
        this.discipline = discipline;
        score = 0;
    }


}
