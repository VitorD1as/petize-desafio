package com.vitor.petize_desafio.business;

import com.vitor.petize_desafio.api.dto.TarefasEstatisticasDto;
import com.vitor.petize_desafio.business.tarefa.CurrentUserAccessor;
import com.vitor.petize_desafio.business.tarefa.TarefaCancelamento;
import com.vitor.petize_desafio.business.tarefa.TarefaFiltro;
import com.vitor.petize_desafio.business.tarefa.TarefaRegras;
import com.vitor.petize_desafio.infrastructure.dtos.TarefasDTO;
import com.vitor.petize_desafio.infrastructure.entities.TarefasEntity;
import com.vitor.petize_desafio.infrastructure.entities.UserEntity;
import com.vitor.petize_desafio.infrastructure.enums.Prioridade;
import com.vitor.petize_desafio.infrastructure.enums.Status;
import com.vitor.petize_desafio.infrastructure.repository.TarefasRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class TarefasService {

    private final TarefasRepository tarefasRepository;
    private final CurrentUserAccessor currentUserAccessor;

    @Transactional
    public TarefasDTO criarTarefas(TarefasDTO tarefasDTO) {
        UserEntity usuario = currentUserAccessor.usuarioAtual();
        TarefasEntity nova = new TarefasEntity(tarefasDTO);
        nova.setUser(usuario);
        TarefasEntity salva = tarefasRepository.save(nova);
        return new TarefasDTO(salva);
    }

    @Transactional
    public TarefasDTO atualizarStatus(TarefasDTO tarefasDTO) {
        TarefasEntity tarefa = obterTarefaDoUsuarioAtual(tarefasDTO.id());
        TarefaRegras.validarTransicaoPara(tarefa, tarefasDTO.status());
        tarefa.setStatus(tarefasDTO.status());
        return new TarefasDTO(tarefa);
    }

    @Transactional
    public Page<TarefasDTO> filtrar(Status status, Prioridade prioridade, LocalDate dataVencimento, Pageable pageable) {
        UserEntity usuario = currentUserAccessor.usuarioAtual();
        TarefaFiltro filtro = new TarefaFiltro(usuario.getId(), status, prioridade, dataVencimento);
        return tarefasRepository.findAll(filtro.toSpecification(), pageable).map(TarefasDTO::new);
    }

    @Transactional
    public TarefasEstatisticasDto estatisticas() {
        Long userId = currentUserAccessor.usuarioAtual().getId();
        return new TarefasEstatisticasDto(
                tarefasRepository.countByUser_Id(userId),
                tarefasRepository.countByUser_IdAndStatus(userId, Status.CRIADO),
                tarefasRepository.countByUser_IdAndStatus(userId, Status.EM_PROGRESSO),
                tarefasRepository.countByUser_IdAndStatus(userId, Status.FINALIZADA),
                tarefasRepository.countByUser_IdAndStatus(userId, Status.CANCELADA)
        );
    }

    @Transactional
    public void deletarTarefas(Long id) {
        TarefasEntity tarefa = obterTarefaDoUsuarioAtual(id);
        TarefaCancelamento.aplicarEmCascata(tarefa);
    }

    private TarefasEntity obterTarefaDoUsuarioAtual(Long tarefaId) {
        Long userId = currentUserAccessor.usuarioAtual().getId();
        return tarefasRepository.findByIdAndUser_Id(tarefaId, userId)
                .orElseThrow(TarefaNaoEncontradaException::new);
    }
}
