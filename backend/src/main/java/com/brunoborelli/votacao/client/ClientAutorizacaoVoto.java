package com.brunoborelli.votacao.client;

public interface ClientAutorizacaoVoto {

    StatusAutorizacaoVoto consultar(String cpf);
}