package com.vitor.petize_desafio.api.dto;

public record TarefasEstatisticasDto(
        long total,
        long criadas,
        long emProgresso,
        long finalizadas,
        long canceladas
) {
}
