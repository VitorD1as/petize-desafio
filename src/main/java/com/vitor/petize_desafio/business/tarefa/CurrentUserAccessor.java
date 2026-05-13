package com.vitor.petize_desafio.business.tarefa;

import com.vitor.petize_desafio.infrastructure.entities.UserEntity;

/**
 * Abstrai a origem do usuário autenticado (ex.: {@code SecurityContextHolder}),
 * permitindo testar serviços sem acoplar a API web.
 */
public interface CurrentUserAccessor {

    UserEntity usuarioAtual();
}
