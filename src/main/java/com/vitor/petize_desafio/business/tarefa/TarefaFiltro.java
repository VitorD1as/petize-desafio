package com.vitor.petize_desafio.business.tarefa;

import com.vitor.petize_desafio.infrastructure.entities.TarefasEntity;
import com.vitor.petize_desafio.infrastructure.enums.Prioridade;
import com.vitor.petize_desafio.infrastructure.enums.Status;
import com.vitor.petize_desafio.infrastructure.specification.TarefaSpecification;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

/**
 * Parâmetros de listagem de tarefas do usuário, convertidos em {@link Specification} JPA.
 */
public record TarefaFiltro(
        Long usuarioId,
        Status status,
        Prioridade prioridade,
        LocalDate dataVencimento
) {
    public Specification<TarefasEntity> toSpecification() {
        return Specification.where(TarefaSpecification.doUsuario(usuarioId))
                .and(TarefaSpecification.naoDeletado())
                .and(TarefaSpecification.comStatus(status))
                .and(TarefaSpecification.comPrioridade(prioridade))
                .and(TarefaSpecification.comDataVencimento(dataVencimento));
    }
}
