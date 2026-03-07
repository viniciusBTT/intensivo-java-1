package com.intensivo.java.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.security")
public record DefaultUsersProperties(
        String defaultAdminUsername,
        String defaultAdminPassword,
        String defaultAttendantUsername,
        String defaultAttendantPassword) {
}
