package com.aston.dz2.utility;

import com.aston.dz2.entity.Student;
import com.aston.dz2.entity.StudentDisciplineScore;
import com.google.gson.*;

import java.lang.reflect.Type;

public class StudentDisciplineScoreAdapter implements JsonSerializer<StudentDisciplineScore>, JsonDeserializer<StudentDisciplineScore> {
    @Override
    public StudentDisciplineScore deserialize(JsonElement json, java.lang.reflect.Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        StudentDisciplineScore score = new StudentDisciplineScore();
        score.setStudent(context.deserialize(jsonObject.get("student"), Student.class));
        score.setScore(jsonObject.get("score").getAsInt());
        return score;
    }

    @Override
    public JsonElement serialize(StudentDisciplineScore studentDisciplineScore, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("student", jsonSerializationContext.serialize(studentDisciplineScore.getStudent()));
        jsonObject.addProperty("score", studentDisciplineScore.getScore());
        return jsonObject;
    }
}
