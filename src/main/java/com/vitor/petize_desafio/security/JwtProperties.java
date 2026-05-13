package com.vitor.petize_desafio.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.jwt")
public record JwtProperties(long expiration, String key) {
}
