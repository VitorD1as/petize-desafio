package com.vitor.petize_desafio.business.tarefa;

import com.vitor.petize_desafio.infrastructure.entities.TarefasEntity;
import com.vitor.petize_desafio.infrastructure.enums.Status;

/**
 * Invariantes e regras de domínio sobre tarefas (independente de persistência).
 */
public final class TarefaRegras {

    private TarefaRegras() {
    }

    /**
     * Impede finalizar tarefa pai enquanto existir subtarefa cujo status não seja {@link Status#FINALIZADA}.
     * Mantém o mesmo critério da implementação anterior ({@code != FINALIZADA}).
     */
    public static void validarTransicaoPara(TarefasEntity tarefa, Status novoStatus) {
        if (novoStatus != Status.FINALIZADA) {
            return;
        }
        if (possuiSubtarefaNaoFinalizada(tarefa)) {
            throw new TransicaoStatusInvalidaException(
                    "Não é possível concluir uma tarefa com subtarefas pendentes!");
        }
    }

    private static boolean possuiSubtarefaNaoFinalizada(TarefasEntity tarefa) {
        if (tarefa.getSubTarefas() == null || tarefa.getSubTarefas().isEmpty()) {
            return false;
        }
        return tarefa.getSubTarefas().stream().anyMatch(st -> st.getStatus() != Status.FINALIZADA);
    }
}
