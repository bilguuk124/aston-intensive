package com.aston.dz2.utility;

import com.aston.dz2.entity.Department;
import com.aston.dz2.entity.Group;
import com.aston.dz2.entity.Student;
import com.aston.dz2.entity.enums.Level;
import com.aston.dz2.exception.LevelNotFoundException;
import com.google.gson.*;


public class StudentAdapter implements JsonSerializer<Student>, JsonDeserializer<Student> {
    @Override
    public JsonElement serialize(Student student, java.lang.reflect.Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", student.getId());
        jsonObject.addProperty("firstName", student.getFirstName());
        jsonObject.addProperty("lastName", student.getLastName());
        jsonObject.add("department", context.serialize(student.getDepartment()));
        jsonObject.addProperty("level", student.getLevel().getStringValue());
        // Include group only if it is not null
        if (student.getGroup() != null) {
            JsonObject groupObject = new JsonObject();
            groupObject.addProperty("id", student.getGroup().getId());
            groupObject.addProperty("name", student.getGroup().getName());
            jsonObject.add("group", groupObject);
        }

        return jsonObject;
    }

    @Override
    public Student deserialize(JsonElement json, java.lang.reflect.Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        Student student = new Student();
        student.setId(jsonObject.get("id").getAsLong());
        student.setFirstName(jsonObject.get("firstName").getAsString());
        student.setLastName(jsonObject.get("lastName").getAsString());
        student.setDepartment(context.deserialize(jsonObject.get("department"), Department.class));
        try{
            student.setLevel(Level.getByStringValue(jsonObject.get("level").getAsString()));
        }
        catch (LevelNotFoundException e){
            student.setLevel(null);
        }

        // Include group only if it is present in the JSON
        if (jsonObject.has("group") && !jsonObject.get("group").isJsonNull()) {
            JsonObject groupObject = jsonObject.getAsJsonObject("group");
            Group group = new Group();
            group.setId(groupObject.get("id").getAsLong());
            group.setName(groupObject.get("name").getAsString());
            student.setGroup(group);
        }

        return student;
    }
}
