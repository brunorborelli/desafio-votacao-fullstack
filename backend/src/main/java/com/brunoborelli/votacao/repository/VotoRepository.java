package com.brunoborelli.votacao.repository;

import com.brunoborelli.votacao.entity.Voto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VotoRepository extends JpaRepository<Voto, Long> {
}