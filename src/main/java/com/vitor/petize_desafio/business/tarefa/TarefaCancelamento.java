package com.vitor.petize_desafio.business.tarefa;

import com.vitor.petize_desafio.infrastructure.entities.TarefasEntity;
import com.vitor.petize_desafio.infrastructure.enums.Status;

/**
 * Política de exclusão lógica: marca a tarefa e toda a árvore de subtarefas como {@link Status#CANCELADA}.
 */
public final class TarefaCancelamento {

    private TarefaCancelamento() {
    }

    public static void aplicarEmCascata(TarefasEntity raiz) {
        raiz.setStatus(Status.CANCELADA);
        if (raiz.getSubTarefas() == null) {
            return;
        }
        for (TarefasEntity sub : raiz.getSubTarefas()) {
            aplicarEmCascata(sub);
        }
    }
}
