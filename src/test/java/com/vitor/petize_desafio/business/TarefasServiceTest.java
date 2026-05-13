package com.vitor.petize_desafio.business;

import com.vitor.petize_desafio.api.dto.TarefasEstatisticasDto;
import com.vitor.petize_desafio.infrastructure.dtos.TarefasDTO;
import com.vitor.petize_desafio.infrastructure.entities.TarefasEntity;
import com.vitor.petize_desafio.infrastructure.entities.UserEntity;
import com.vitor.petize_desafio.infrastructure.enums.Prioridade;
import com.vitor.petize_desafio.infrastructure.enums.Status;
import com.vitor.petize_desafio.infrastructure.repository.TarefasRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TarefasServiceTest {

    @Mock
    private TarefasRepository tarefasRepository;

    @InjectMocks
    private TarefasService tarefasService;

    private UserEntity usuario;

    @BeforeEach
    void autenticarUsuario() {
        usuario = UserEntity.builder()
                .id(42L)
                .nome("Usuário")
                .email("user@test.com")
                .senha("{bcrypt}hash")
                .build();
        var auth = new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @AfterEach
    void limparContexto() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void criarTarefas_persisteComUsuarioDoContexto() {
        TarefasDTO dto = new TarefasDTO(null, "Título", "Descrição", LocalDate.now(), Status.CRIADO, Prioridade.ALTA);
        when(tarefasRepository.save(any(TarefasEntity.class))).thenAnswer(invocation -> {
            TarefasEntity e = invocation.getArgument(0);
            e.setId(99L);
            return e;
        });

        TarefasDTO result = tarefasService.criarTarefas(dto);

        assertThat(result.id()).isEqualTo(99L);
        ArgumentCaptor<TarefasEntity> captor = ArgumentCaptor.forClass(TarefasEntity.class);
        verify(tarefasRepository).save(captor.capture());
        assertThat(captor.getValue().getUser()).isSameAs(usuario);
    }

    @Test
    void atualizarStatus_finalizadaComSubtarefaPendente_lancaIllegalState() {
        TarefasEntity sub = new TarefasEntity();
        sub.setStatus(Status.EM_PROGRESSO);
        TarefasEntity pai = new TarefasEntity();
        pai.setId(1L);
        pai.setSubTarefas(List.of(sub));
        when(tarefasRepository.findByIdAndUser_Id(1L, 42L)).thenReturn(Optional.of(pai));

        TarefasDTO input = new TarefasDTO(1L, null, null, null, Status.FINALIZADA, null);

        assertThatThrownBy(() -> tarefasService.atualizarStatus(input))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("subtarefas");
    }

    @Test
    void buscar_tarefaDeOutroUsuario_lancaTarefaNaoEncontrada() {
        when(tarefasRepository.findByIdAndUser_Id(7L, 42L)).thenReturn(Optional.empty());

        TarefasDTO input = new TarefasDTO(7L, null, null, null, Status.EM_PROGRESSO, null);

        assertThatThrownBy(() -> tarefasService.atualizarStatus(input))
                .isInstanceOf(TarefaNaoEncontradaException.class);
    }

    @Test
    void estatisticas_retornaContagensDoRepositorio() {
        when(tarefasRepository.countByUser_Id(42L)).thenReturn(10L);
        when(tarefasRepository.countByUser_IdAndStatus(42L, Status.CRIADO)).thenReturn(2L);
        when(tarefasRepository.countByUser_IdAndStatus(42L, Status.EM_PROGRESSO)).thenReturn(3L);
        when(tarefasRepository.countByUser_IdAndStatus(42L, Status.FINALIZADA)).thenReturn(4L);
        when(tarefasRepository.countByUser_IdAndStatus(42L, Status.CANCELADA)).thenReturn(1L);

        TarefasEstatisticasDto stats = tarefasService.estatisticas();

        assertThat(stats.total()).isEqualTo(10L);
        assertThat(stats.criadas()).isEqualTo(2L);
        assertThat(stats.emProgresso()).isEqualTo(3L);
        assertThat(stats.finalizadas()).isEqualTo(4L);
        assertThat(stats.canceladas()).isEqualTo(1L);
    }
}
