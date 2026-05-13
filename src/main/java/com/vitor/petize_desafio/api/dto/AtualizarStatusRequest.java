package com.vitor.petize_desafio.api.dto;

import com.vitor.petize_desafio.infrastructure.dtos.TarefasDTO;
import com.vitor.petize_desafio.infrastructure.enums.Status;
import jakarta.validation.constraints.NotNull;

public record AtualizarStatusRequest(@NotNull Status status) {
    public TarefasDTO toDto(Long id) {
        return new TarefasDTO(id, null, null, null, status, null);
    }
}
