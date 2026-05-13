package com.vitor.petize_desafio.infrastructure.entities;

import com.vitor.petize_desafio.infrastructure.dtos.TarefasDTO;
import com.vitor.petize_desafio.infrastructure.enums.Prioridade;
import com.vitor.petize_desafio.infrastructure.enums.Status;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "TB_TASK")
@Getter
@Setter
public class TarefasEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank
    private String titulo;

    @NotBlank
    private String descricao;

    private LocalDate dataVencimento;

    @Setter
    @Enumerated(EnumType.STRING)
    private Status status;

    @Enumerated(EnumType.STRING)
    private Prioridade prioridade;

    @ManyToOne
    @JoinColumn(name = "idTarefaPrincipal")
    private TarefasEntity taskPrincipal;

    @OneToMany(mappedBy = "taskPrincipal", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TarefasEntity> subTarefas;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idUsuario")
    private UserEntity user;

    public TarefasEntity(TarefasDTO tarefas) {
        this.titulo = tarefas.titulo();
        this.descricao = tarefas.descricao();
        this.dataVencimento = tarefas.dataVencimento();
        this.status = tarefas.status();
        this.prioridade = tarefas.prioridade();
    }

    public TarefasEntity() {

    }
}
