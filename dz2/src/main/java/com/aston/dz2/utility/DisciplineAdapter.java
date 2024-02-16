package com.aston.dz2.utility;

import com.aston.dz2.entity.Discipline;
import com.aston.dz2.entity.Group;
import com.aston.dz2.entity.Teacher;
import com.aston.dz2.entity.enums.Semester;
import com.google.gson.*;

import java.util.HashSet;
import java.util.Set;

public class DisciplineAdapter implements JsonSerializer<Discipline>, JsonDeserializer<Discipline> {
    @Override
    public JsonElement serialize(Discipline discipline, java.lang.reflect.Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", discipline.getId());
        jsonObject.addProperty("name", discipline.getName());
        jsonObject.add("lecturer", context.serialize(discipline.getLecturer()));
        jsonObject.add("assistant", context.serialize(discipline.getAssistant()));
        jsonObject.add("semester", context.serialize(discipline.getSemester()));

        JsonArray groupsArray = new JsonArray();
        for (Group group : discipline.getGroups()){
            groupsArray.add(context.serialize(group));
        }
        jsonObject.add("groups", groupsArray);

        return jsonObject;
    }

    @Override
    public Discipline deserialize(JsonElement json, java.lang.reflect.Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        Discipline discipline = new Discipline();
        discipline.setId(jsonObject.get("id").getAsLong());
        discipline.setName(jsonObject.get("name").getAsString());
        discipline.setLecturer(context.deserialize(jsonObject.get("lecturer"), Teacher.class));
        discipline.setAssistant(context.deserialize(jsonObject.get("assistant"), Teacher.class));
        discipline.setSemester(context.deserialize(jsonObject.get("semester"), Semester.class));

        JsonArray groupsArray = jsonObject.getAsJsonArray("groups");
        Set<Group> groups = new HashSet<>();
        for (JsonElement groupElement : groupsArray){
            groups.add(context.deserialize(groupElement, Group.class));
        }
        discipline.setGroups(groups);

        return discipline;
    }
}
