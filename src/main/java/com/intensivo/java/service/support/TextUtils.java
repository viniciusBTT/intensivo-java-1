package com.intensivo.java.service.support;

public final class TextUtils {

    private TextUtils() {
    }

    public static String somenteDigitos(String valor) {
        if (valor == null) {
            return "";
        }
        return valor.replaceAll("\\D", "");
    }

    public static String textoNormalizado(String valor) {
        return valor == null ? "" : valor.trim().replaceAll("\\s{2,}", " ");
    }

    public static String emailNormalizado(String valor) {
        return textoNormalizado(valor).toLowerCase();
    }
}
