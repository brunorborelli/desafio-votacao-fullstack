package com.brunoborelli.votacao.repository;

import com.brunoborelli.votacao.entity.SessaoVotacao;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SessaoVotacaoRepository extends JpaRepository<SessaoVotacao, Long> {
}