package com.vitor.petize_desafio.api;

import com.vitor.petize_desafio.business.TarefasService;
import com.vitor.petize_desafio.api.dto.AtualizarStatusRequest;
import com.vitor.petize_desafio.api.dto.CriarTarefaRequest;
import com.vitor.petize_desafio.api.dto.TarefasEstatisticasDto;
import com.vitor.petize_desafio.infrastructure.dtos.TarefasDTO;
import com.vitor.petize_desafio.infrastructure.enums.Prioridade;
import com.vitor.petize_desafio.infrastructure.enums.Status;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/tarefas")
@RequiredArgsConstructor
public class TarefasController {

    private final TarefasService tarefasService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TarefasDTO criar(@Valid @RequestBody CriarTarefaRequest request) {
        return tarefasService.criarTarefas(request.toDto());
    }

    @PatchMapping("/{id}/status")
    public TarefasDTO atualizarStatus(
            @PathVariable Long id,
            @Valid @RequestBody AtualizarStatusRequest request
    ) {
        return tarefasService.atualizarStatus(request.toDto(id));
    }

    @GetMapping("/estatisticas")
    public TarefasEstatisticasDto estatisticas() {
        return tarefasService.estatisticas();
    }

    @GetMapping
    public Page<TarefasDTO> listar(
            @RequestParam(required = false) Status status,
            @RequestParam(required = false) Prioridade prioridade,
            @RequestParam(required = false) LocalDate dataVencimento,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        return tarefasService.filtrar(status, prioridade, dataVencimento, pageable);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletar(@PathVariable Long id) {
        tarefasService.deletarTarefas(id);
    }
}
