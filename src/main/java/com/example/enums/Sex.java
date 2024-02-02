package com.example.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Sex {
    MALE("M"),
    FEMALE("F");

    private final String gedcomFormat;


}
