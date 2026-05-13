package com.vitor.petize_desafio.business;

public class TarefaNaoEncontradaException extends RuntimeException {

    public TarefaNaoEncontradaException() {
        super("Tarefa não encontrada!");
    }
}
