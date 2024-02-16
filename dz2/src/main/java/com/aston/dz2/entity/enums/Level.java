package com.aston.dz2.entity.enums;

import com.aston.dz2.exception.LevelNotFoundException;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum Level {
    BACHELOR("bachelor"),
    MASTER("master"),
    PHD("phd");

    private final String stringValue;
    Level(String stringValue){
        this.stringValue = stringValue;
    }

    public static Level getByStringValue(String stringValue) throws LevelNotFoundException {
        return Arrays.stream(Level.values()).filter(level -> level.stringValue.equalsIgnoreCase(stringValue))
                .findFirst()
                .orElseThrow(() -> new LevelNotFoundException("Level with name " + stringValue + " was not found!"));
    }
}
