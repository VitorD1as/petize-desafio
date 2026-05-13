package com.vitor.petize_desafio.business;

import com.vitor.petize_desafio.infrastructure.entities.UserEntity;
import com.vitor.petize_desafio.infrastructure.repository.UserRepository;
import com.vitor.petize_desafio.security.JwtService;
import com.vitor.petize_desafio.api.dto.LoginRequest;
import com.vitor.petize_desafio.api.dto.RegisterRequest;
import com.vitor.petize_desafio.api.dto.TokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public TokenResponse registrar(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new EmailCadastradoException();
        }
        UserEntity user = UserEntity.builder()
                .nome(request.nome())
                .email(request.email())
                .senha(passwordEncoder.encode(request.senha()))
                .build();
        userRepository.save(user);
        return new TokenResponse(jwtService.generateToken(user));
    }

    public TokenResponse login(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email(), request.senha()));
        } catch (BadCredentialsException e) {
            throw new CredenciaisInvalidasException();
        }
        UserEntity user = userRepository.findByEmail(request.email())
                .orElseThrow(CredenciaisInvalidasException::new);
        return new TokenResponse(jwtService.generateToken(user));
    }
}
