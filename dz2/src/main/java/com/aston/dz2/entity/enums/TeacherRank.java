package com.aston.dz2.entity.enums;

import com.aston.dz2.exception.TeacherRankNotFoundException;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum TeacherRank {
    ASSISTANT("assistant"),
    ASSOCIATE("associate"),
    PROFESSOR("professor"),
    DISTINGUISHED("distinguished");

    private final String stringValue;

    TeacherRank(String stringValue){
        this.stringValue = stringValue;
    }

    public static TeacherRank getByStringValue(String stringValue) throws TeacherRankNotFoundException {
        return Arrays.stream(TeacherRank.values())
                .filter(rank -> rank.stringValue.equalsIgnoreCase(stringValue))
                .findFirst()
                .orElseThrow(() -> new TeacherRankNotFoundException("Teacher rank with name " + stringValue + " was not found!"));
    }

}
