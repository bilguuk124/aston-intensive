package com.aston.dz2.utility;

import com.aston.dz2.entity.Department;
import com.aston.dz2.entity.Group;
import com.aston.dz2.entity.Student;
import com.aston.dz2.entity.enums.Level;
import com.google.gson.*;


public class StudentGroupAdapter implements JsonSerializer<Student>, JsonDeserializer<Student> {
    @Override
    public JsonElement serialize(Student student, java.lang.reflect.Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", student.getId());
        jsonObject.addProperty("firstName", student.getFirstName());
        jsonObject.addProperty("lastName", student.getLastName());
        jsonObject.add("department", jsonSerializationContext.serialize(student.getDepartment()));
        jsonObject.add("level", jsonSerializationContext.serialize(student.getLevel()));
        jsonObject.add("group", serializeGroup(student.getGroup()));
        return jsonObject;
    }

    @Override
    public Student deserialize(JsonElement jsonElement, java.lang.reflect.Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        Long id = jsonObject.getAsJsonPrimitive("id").getAsLong();
        String firstName = jsonObject.getAsJsonPrimitive("firstName").getAsString();
        String lastName = jsonObject.getAsJsonPrimitive("lastName").getAsString();
        Department department = jsonDeserializationContext.deserialize(jsonObject.get("department"), Department.class);
        Level level = jsonDeserializationContext.deserialize(jsonObject.get("level"), Level.class);
        Group group = deserializeGroup(jsonObject.getAsJsonObject("group"));

        Student student = new Student();
        student.setId(id);
        student.setFirstName(firstName);
        student.setLastName(lastName);
        student.setDepartment(department);
        student.setLevel(level);
        student.setGroup(group);

        return student;
    }

    private JsonObject serializeGroup(Group group) {
        JsonObject groupObject = new JsonObject();
        groupObject.addProperty("id", group.getId());
        groupObject.addProperty("name", group.getName());
        return groupObject;
    }

    private Group deserializeGroup(JsonObject groupObject) {
        Long id = groupObject.getAsJsonPrimitive("id").getAsLong();
        String name = groupObject.getAsJsonPrimitive("name").getAsString();
        Group group = new Group();
        group.setId(id);
        group.setName(name);
        return group;
    }
}
