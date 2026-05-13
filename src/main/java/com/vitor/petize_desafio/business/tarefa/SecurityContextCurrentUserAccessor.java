package com.vitor.petize_desafio.business.tarefa;

import com.vitor.petize_desafio.infrastructure.entities.UserEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityContextCurrentUserAccessor implements CurrentUserAccessor {

    @Override
    public UserEntity usuarioAtual() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UserEntity usuario)) {
            throw new IllegalStateException("Nenhum usuário autenticado no contexto de segurança");
        }
        return usuario;
    }
}
