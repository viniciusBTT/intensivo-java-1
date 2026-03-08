package com.intensivo.java.util;

public final class MaskingUtils {

    private MaskingUtils() {
    }

    public static String maskDocument(String value) {
        String digits = TextUtils.somenteDigitos(value);
        if (digits.length() <= 4) {
            return "****";
        }
        return "*".repeat(Math.max(0, digits.length() - 4)) + digits.substring(digits.length() - 4);
    }
}
