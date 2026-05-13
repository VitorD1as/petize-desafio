package com.vitor.petize_desafio.infrastructure.specification;


import com.vitor.petize_desafio.infrastructure.entities.TarefasEntity;
import com.vitor.petize_desafio.infrastructure.enums.Prioridade;
import com.vitor.petize_desafio.infrastructure.enums.Status;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class TarefaSpecification {

    public static Specification<TarefasEntity> doUsuario(Long id) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("user").get("id"), id);
    }

    public static Specification<TarefasEntity> naoDeletado() {
        return (root, query, cb) -> cb.notEqual(root.get("status"), Status.CANCELADA);
    }

    public static Specification<TarefasEntity> comStatus(Status status) {
        return (root, query, criteriaBuilder) -> status == null ? criteriaBuilder.conjunction() : criteriaBuilder.equal(root.get("status"), status);
    }

    public static Specification<TarefasEntity> comPrioridade(Prioridade prioridade) {
        return (root, query, criteriaBuilder) -> prioridade == null ? criteriaBuilder.conjunction() : criteriaBuilder.equal(root.get("prioridade"), prioridade);
    }

    public static Specification<TarefasEntity> comDataVencimento(LocalDate dataVencimento) {
        return (root, query, criteriaBuilder) -> dataVencimento == null ? criteriaBuilder.conjunction() : criteriaBuilder.equal(root.get("dataVencimento"), dataVencimento);
    }
}