package com.brunoborelli.votacao.exception;

public class ConflitoDeNegocioException extends RuntimeException {

    public ConflitoDeNegocioException(String mensagem) {
        super(mensagem);
    }

    public ConflitoDeNegocioException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}