package com.brunoborelli.votacao.exception;

public class RequisicaoInvalidaException extends RuntimeException {

    public RequisicaoInvalidaException(String mensagem) {
        super(mensagem);
    }
}