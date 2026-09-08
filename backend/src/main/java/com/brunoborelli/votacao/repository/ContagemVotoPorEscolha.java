package com.brunoborelli.votacao.repository;

import com.brunoborelli.votacao.entity.EscolhaVoto;

public interface ContagemVotoPorEscolha {

    EscolhaVoto getEscolha();

    long getQuantidade();
}