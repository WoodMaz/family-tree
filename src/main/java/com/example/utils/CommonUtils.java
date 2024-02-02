package com.example.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.NONE)
public class CommonUtils {

    public static boolean hasContent(String s) {
        return s != null && !s.isBlank();
    }
}
