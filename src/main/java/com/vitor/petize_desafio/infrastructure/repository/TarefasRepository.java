package com.vitor.petize_desafio.infrastructure.repository;

import com.vitor.petize_desafio.infrastructure.entities.TarefasEntity;
import com.vitor.petize_desafio.infrastructure.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface TarefasRepository extends JpaRepository<TarefasEntity, Long>, JpaSpecificationExecutor<TarefasEntity> {

    Optional<TarefasEntity> findByIdAndUser_Id(Long id, Long userId);

    long countByUser_Id(Long userId);

    long countByUser_IdAndStatus(Long userId, Status status);
}
