package com.aston.dz2.utility;

import com.aston.dz2.entity.Discipline;
import com.aston.dz2.entity.Student;
import com.aston.dz2.entity.StudentDisciplineScore;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Default;
import jakarta.enterprise.inject.Produces;

import java.time.LocalDateTime;

@ApplicationScoped
public class GsonProducer {

    @Produces
    @Default
    public Gson gson(){
        return new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeFormatter())
                .registerTypeAdapter(Student.class, new StudentGroupAdapter())
                .registerTypeAdapter(Discipline.class, new DisciplineAdapter())
                .registerTypeAdapter(StudentDisciplineScore.class, new StudentDisciplineScoreAdapter())
                .registerTypeAdapter(Student.class, new StudentAdapter())
                .create();
    }
}
