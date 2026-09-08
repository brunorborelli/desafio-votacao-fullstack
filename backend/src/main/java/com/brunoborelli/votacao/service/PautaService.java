package com.brunoborelli.votacao.service;

import com.brunoborelli.votacao.entity.Pauta;
import com.brunoborelli.votacao.exception.RecursoNaoEncontradoException;
import com.brunoborelli.votacao.repository.PautaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PautaService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PautaService.class);

    private final PautaRepository pautaRepository;

    public PautaService(PautaRepository pautaRepository) {
        this.pautaRepository = pautaRepository;
    }

    @Transactional
    public Pauta cadastrar(String titulo, String descricao) {
        Pauta pauta = new Pauta(titulo.trim(), descricao.trim());
        Pauta pautaSalva = pautaRepository.save(pauta);
        LOGGER.info("Pauta cadastrada: pautaId={}", pautaSalva.getId());
        return pautaSalva;
    }

    @Transactional(readOnly = true)
    public List<Pauta> listar() {
        return pautaRepository.findAll(Sort.by(Sort.Direction.DESC, "dataCriacao"));
    }

    @Transactional(readOnly = true)
    public Pauta buscarPorId(Long id) {
        return pautaRepository.findById(id)
            .orElseThrow(() -> new RecursoNaoEncontradoException(
                "Pauta não encontrada para o id " + id
            ));
    }
}