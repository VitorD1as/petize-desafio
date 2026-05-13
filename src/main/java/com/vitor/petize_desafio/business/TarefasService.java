package com.vitor.petize_desafio.business;

import com.vitor.petize_desafio.api.dto.TarefasEstatisticasDto;
import com.vitor.petize_desafio.infrastructure.dtos.TarefasDTO;
import com.vitor.petize_desafio.infrastructure.entities.TarefasEntity;
import com.vitor.petize_desafio.infrastructure.entities.UserEntity;
import com.vitor.petize_desafio.infrastructure.enums.Prioridade;
import com.vitor.petize_desafio.infrastructure.enums.Status;
import com.vitor.petize_desafio.infrastructure.repository.TarefasRepository;
import com.vitor.petize_desafio.infrastructure.specification.TarefaSpecification;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Pageable;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Transactional
public class TarefasService {
    private final TarefasRepository tarefasRepository;

    public TarefasDTO criarTarefas(TarefasDTO tarefasDTO){
        var usuario = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        TarefasEntity tarefasEntity = new TarefasEntity(tarefasDTO);
        tarefasEntity.setUser(usuario);
        tarefasRepository.save(tarefasEntity);
        return new TarefasDTO(tarefasEntity);
    }

    public TarefasDTO atualizarStatus(TarefasDTO tarefasDTO){
        var tarefas = buscar(tarefasDTO.id());

        if(tarefasDTO.status() == Status.FINALIZADA){
            boolean pendentes = tarefas.getSubTarefas() != null
                    && tarefas.getSubTarefas().stream().anyMatch(st -> st.getStatus() != Status.FINALIZADA);
            if(pendentes){
                throw new IllegalStateException("Não é possível concluir uma tarefa com subtarefas pendentes!");
            }
        }
        tarefas.setStatus(tarefasDTO.status());
        return new TarefasDTO(tarefas);
    }

    public Page<TarefasDTO> filtrar(Status status, Prioridade prioridade, LocalDate dataVencimento, Pageable pageable) {

        var usuario = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Specification<TarefasEntity> specification = Specification.where(TarefaSpecification.doUsuario(usuario.getId()))
                .and(TarefaSpecification.naoDeletado())
                .and((TarefaSpecification.comStatus(status)))
                .and(TarefaSpecification.comPrioridade(prioridade))
                .and(TarefaSpecification.comDataVencimento(dataVencimento));
        var page = tarefasRepository.findAll(specification, pageable);

        return page.map(TarefasDTO::new);
    }

    public TarefasEstatisticasDto estatisticas() {
        var usuario = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = usuario.getId();
        return new TarefasEstatisticasDto(
                tarefasRepository.countByUser_Id(userId),
                tarefasRepository.countByUser_IdAndStatus(userId, Status.CRIADO),
                tarefasRepository.countByUser_IdAndStatus(userId, Status.EM_PROGRESSO),
                tarefasRepository.countByUser_IdAndStatus(userId, Status.FINALIZADA),
                tarefasRepository.countByUser_IdAndStatus(userId, Status.CANCELADA)
        );
    }

    public void deletarTarefas(Long id){
        var tarefas = buscar(id);
        deletarSubTarefas(tarefas);
    }

    private void deletarSubTarefas(TarefasEntity tarefasEntity){
        tarefasEntity.setStatus(Status.CANCELADA);
        if(tarefasEntity.getSubTarefas() != null){
            for (TarefasEntity subTarefas : tarefasEntity.getSubTarefas()) {
                deletarSubTarefas(subTarefas);
            }
        }
    }

    private TarefasEntity buscar(Long id) {
        var usuario = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return tarefasRepository.findByIdAndUser_Id(id, usuario.getId())
                .orElseThrow(TarefaNaoEncontradaException::new);
    }
}
