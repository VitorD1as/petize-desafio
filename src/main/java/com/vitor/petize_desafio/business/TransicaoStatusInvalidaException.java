package com.vitor.petize_desafio.business;

public class TransicaoStatusInvalidaException extends RuntimeException {

    public TransicaoStatusInvalidaException(String message) {
        super(message);
    }
}
