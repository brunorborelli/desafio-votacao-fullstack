package com.brunoborelli.votacao.repository;

import com.brunoborelli.votacao.entity.Voto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VotoRepository extends JpaRepository<Voto, Long> {

    Optional<Voto> findByPautaIdAndCpfAssociado(Long pautaId, String cpfAssociado);

    boolean existsByPautaIdAndCpfAssociado(Long pautaId, String cpfAssociado);
}