package com.intensivo.java.model;

public enum Role {
    ADMIN,
    ATENDENTE;

    public String asAuthority() {
        return "ROLE_" + name();
    }
}
