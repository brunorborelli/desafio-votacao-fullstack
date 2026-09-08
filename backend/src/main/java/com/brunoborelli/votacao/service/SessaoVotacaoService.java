package com.brunoborelli.votacao.service;

import com.brunoborelli.votacao.entity.Pauta;
import com.brunoborelli.votacao.entity.SessaoVotacao;
import com.brunoborelli.votacao.exception.ConflitoDeNegocioException;
import com.brunoborelli.votacao.exception.RecursoNaoEncontradoException;
import com.brunoborelli.votacao.repository.SessaoVotacaoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;

@Service
public class SessaoVotacaoService {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(SessaoVotacaoService.class);

    private static final int DURACAO_PADRAO_MINUTOS = 1;

    private final SessaoVotacaoRepository sessaoVotacaoRepository;
    private final PautaService pautaService;

    public SessaoVotacaoService(
        SessaoVotacaoRepository sessaoVotacaoRepository,
        PautaService pautaService
    ) {
        this.sessaoVotacaoRepository = sessaoVotacaoRepository;
        this.pautaService = pautaService;
    }

    @Transactional
    public SessaoVotacao abrir(Long pautaId, Integer duracaoMinutos) {
        Pauta pauta = pautaService.buscarPorId(pautaId);

        if (sessaoVotacaoRepository.existsByPautaId(pautaId)) {
            throw sessaoJaExiste(pautaId);
        }

        int duracao = duracaoMinutos == null ? DURACAO_PADRAO_MINUTOS : duracaoMinutos;
        Instant dataInicio = Instant.now();
        Instant dataFim = dataInicio.plus(Duration.ofMinutes(duracao));
        SessaoVotacao sessaoVotacao = new SessaoVotacao(pauta, dataInicio, dataFim);

        try {
            SessaoVotacao sessaoSalva =
                    sessaoVotacaoRepository.saveAndFlush(sessaoVotacao);
            LOGGER.info(
                    "Sessão de votação aberta: sessaoId={}, pautaId={}, dataFim={}",
                    sessaoSalva.getId(),
                    pautaId,
                    dataFim
            );
            return sessaoSalva;
        } catch (DataIntegrityViolationException excecao) {
            throw new ConflitoDeNegocioException(
                "Já existe uma sessão de votação para a pauta " + pautaId,
                excecao
            );
        }
    }

    @Transactional(readOnly = true)
    public SessaoVotacao buscarPorPautaId(Long pautaId) {
        pautaService.buscarPorId(pautaId);

        return sessaoVotacaoRepository.findByPautaId(pautaId)
            .orElseThrow(() -> new RecursoNaoEncontradoException(
                "Sessão de votação não encontrada para a pauta " + pautaId
            ));
    }

    private ConflitoDeNegocioException sessaoJaExiste(Long pautaId) {
        return new ConflitoDeNegocioException(
            "Já existe uma sessão de votação para a pauta " + pautaId
        );
    }
}