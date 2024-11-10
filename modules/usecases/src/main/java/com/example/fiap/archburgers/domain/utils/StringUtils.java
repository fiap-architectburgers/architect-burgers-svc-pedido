package com.example.fiap.archburgers.domain.utils;

public class StringUtils {

    public static boolean isEmpty(String string) {
        return string == null || string.trim().isEmpty();
    }
}
