package com.vitor.petize_desafio.api.dto;

import com.vitor.petize_desafio.infrastructure.dtos.TarefasDTO;
import com.vitor.petize_desafio.infrastructure.enums.Prioridade;
import com.vitor.petize_desafio.infrastructure.enums.Status;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public record CriarTarefaRequest(
        @NotBlank String titulo,
        @NotBlank String descricao,
        LocalDate dataVencimento,
        Status status,
        Prioridade prioridade
) {
    public TarefasDTO toDto() {
        return new TarefasDTO(null, titulo, descricao, dataVencimento, status, prioridade);
    }
}
