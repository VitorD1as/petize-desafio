package com.vitor.petize_desafio.security;

import com.vitor.petize_desafio.infrastructure.entities.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        JwtProperties props = new JwtProperties(120_000, "unit-test-jwt-secret-at-least-32-chars!!");
        jwtService = new JwtService(props);
    }

    @Test
    void tokenContemSubjectEmailEValidaParaUserDetails() {
        UserEntity user = UserEntity.builder()
                .id(1L)
                .nome("Teste")
                .email("jwt@test.com")
                .senha("{bcrypt}x")
                .build();

        String token = jwtService.generateToken(user);

        assertThat(jwtService.extractUsername(token)).isEqualTo("jwt@test.com");
        assertThat(jwtService.isTokenValid(token, user)).isTrue();
    }
}
