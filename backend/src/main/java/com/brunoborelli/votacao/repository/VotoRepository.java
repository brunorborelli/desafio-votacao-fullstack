package com.brunoborelli.votacao.repository;

import com.brunoborelli.votacao.entity.Voto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface VotoRepository extends JpaRepository<Voto, Long> {

    Optional<Voto> findByPautaIdAndCpfAssociado(Long pautaId, String cpfAssociado);

    boolean existsByPautaIdAndCpfAssociado(Long pautaId, String cpfAssociado);

    @Query("""
        SELECT voto.escolha AS escolha, COUNT(voto) AS quantidade
        FROM Voto voto
        WHERE voto.pauta.id = :pautaId
        GROUP BY voto.escolha
        """)
    List<ContagemVotoPorEscolha> contarPorPautaId(@Param("pautaId") Long pautaId);
}