package com.aston.dz2.entity.enums;

import com.aston.dz2.exception.SemesterNotFoundException;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum Semester {
    FIRST(1, "first"),
    SECOND(2, "second"),
    THIRD(3, "third"),
    FOURTH(4, "fourth"),
    FIFTH(5, "fifth"),
    SIXTH(6, "sixth"),
    SEVENTH(7, "seventh"),
    EIGHT(8, "eight");

    private final int numberValue;
    private final String stringValue;

    Semester(int numberValue, String stringValue){
        this.numberValue = numberValue;
        this.stringValue = stringValue;
    }

    public static Semester getByNumberValue(int numberValue) throws SemesterNotFoundException {
        return Arrays.stream(Semester.values())
                .filter(n -> n.numberValue == numberValue)
                .findFirst()
                .orElseThrow(() -> new SemesterNotFoundException("Semester of value = " + numberValue + " does not exist"));
    }

    public static Semester getByStringValue(String stringValue) throws SemesterNotFoundException {
        return Arrays.stream(Semester.values())
                .filter(n -> n.getStringValue().equalsIgnoreCase(stringValue))
                .findFirst().orElseThrow(() -> new SemesterNotFoundException("Semester of value + " + stringValue + " does not exist" ));
    }
}
