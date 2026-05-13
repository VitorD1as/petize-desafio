package com.vitor.petize_desafio.infrastructure.dtos;


import com.vitor.petize_desafio.infrastructure.entities.TarefasEntity;
import com.vitor.petize_desafio.infrastructure.enums.Prioridade;
import com.vitor.petize_desafio.infrastructure.enums.Status;

import java.time.LocalDate;

public record TarefasDTO(Long id, String titulo, String descricao, LocalDate dataVencimento, Status status, Prioridade prioridade){
    public TarefasDTO(TarefasEntity entity) {
        this(
                entity.getId(),
                entity.getTitulo(),
                entity.getDescricao(),
                entity.getDataVencimento(),
                entity.getStatus(),
                entity.getPrioridade()
        );
    }
}
