package com.vitor.petize_desafio.business;

import com.vitor.petize_desafio.api.dto.LoginRequest;
import com.vitor.petize_desafio.api.dto.RegisterRequest;
import com.vitor.petize_desafio.api.dto.TokenResponse;
import com.vitor.petize_desafio.infrastructure.entities.UserEntity;
import com.vitor.petize_desafio.infrastructure.repository.UserRepository;
import com.vitor.petize_desafio.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;
    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    @Test
    void registrar_emailDuplicado_lancaEmailCadastradoException() {
        when(userRepository.existsByEmail("dup@test.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.registrar(new RegisterRequest("N", "dup@test.com", "senha12345")))
                .isInstanceOf(EmailCadastradoException.class);
    }

    @Test
    void registrar_novoUsuario_salvaSenhaEncriptadaERetornaToken() {
        when(userRepository.existsByEmail("novo@test.com")).thenReturn(false);
        when(passwordEncoder.encode("senha12345")).thenReturn("HASH-BCRYPT");
        when(jwtService.generateToken(any(UserEntity.class))).thenReturn("jwt-token");

        TokenResponse response = authService.registrar(new RegisterRequest("Novo", "novo@test.com", "senha12345"));

        assertThat(response.token()).isEqualTo("jwt-token");
        ArgumentCaptor<UserEntity> captor = ArgumentCaptor.forClass(UserEntity.class);
        verify(userRepository).save(captor.capture());
        assertThat(captor.getValue().getSenha()).isEqualTo("HASH-BCRYPT");
        assertThat(captor.getValue().getEmail()).isEqualTo("novo@test.com");
    }

    @Test
    void login_credenciaisInvalidas_lancaCredenciaisInvalidasException() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("bad"));

        assertThatThrownBy(() -> authService.login(new LoginRequest("x@test.com", "wrong")))
                .isInstanceOf(CredenciaisInvalidasException.class);
    }

    @Test
    void login_sucesso_retornaToken() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);
        UserEntity user = UserEntity.builder()
                .id(1L)
                .nome("A")
                .email("ok@test.com")
                .senha("hash")
                .build();
        when(userRepository.findByEmail("ok@test.com")).thenReturn(Optional.of(user));
        when(jwtService.generateToken(user)).thenReturn("token-ok");

        TokenResponse response = authService.login(new LoginRequest("ok@test.com", "secret12345"));

        assertThat(response.token()).isEqualTo("token-ok");
    }
}
