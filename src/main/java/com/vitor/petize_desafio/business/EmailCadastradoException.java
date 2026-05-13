package com.vitor.petize_desafio.business;

public class EmailCadastradoException extends RuntimeException {

    public EmailCadastradoException() {
        super("E-mail já cadastrado");
    }
}
