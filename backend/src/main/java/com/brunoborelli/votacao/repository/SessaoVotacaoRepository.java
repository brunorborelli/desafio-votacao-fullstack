package com.brunoborelli.votacao.repository;

import com.brunoborelli.votacao.entity.SessaoVotacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SessaoVotacaoRepository extends JpaRepository<SessaoVotacao, Long> {

    Optional<SessaoVotacao> findByPautaId(Long pautaId);

    boolean existsByPautaId(Long pautaId);
}