package com.brunoborelli.votacao.service;

import com.brunoborelli.votacao.entity.EscolhaVoto;
import com.brunoborelli.votacao.entity.SessaoVotacao;
import com.brunoborelli.votacao.entity.Voto;
import com.brunoborelli.votacao.exception.ConflitoDeNegocioException;
import com.brunoborelli.votacao.exception.RequisicaoInvalidaException;
import com.brunoborelli.votacao.repository.VotoRepository;
import com.brunoborelli.votacao.validation.ValidadorCpf;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class VotoService {

    private final VotoRepository votoRepository;
    private final SessaoVotacaoService sessaoVotacaoService;
    private final ValidadorCpf validadorCpf;

    public VotoService(
        VotoRepository votoRepository,
        SessaoVotacaoService sessaoVotacaoService,
        ValidadorCpf validadorCpf
    ) {
        this.votoRepository = votoRepository;
        this.sessaoVotacaoService = sessaoVotacaoService;
        this.validadorCpf = validadorCpf;
    }

    @Transactional
    public Voto registrar(Long pautaId, String cpfAssociado, EscolhaVoto escolha) {
        if (!validadorCpf.ehValido(cpfAssociado)) {
            throw new RequisicaoInvalidaException("CPF inválido");
        }

        SessaoVotacao sessaoVotacao = sessaoVotacaoService.buscarPorPautaId(pautaId);
        Instant instanteVoto = Instant.now();

        if (!sessaoVotacao.estaAbertaEm(instanteVoto)) {
            throw new ConflitoDeNegocioException(
                "A sessão de votação da pauta " + pautaId + " não está aberta"
            );
        }

        if (votoRepository.existsByPautaIdAndCpfAssociado(pautaId, cpfAssociado)) {
            throw votoJaRegistrado(pautaId, cpfAssociado);
        }

        Voto voto = new Voto(
            sessaoVotacao.getPauta(),
            cpfAssociado,
            escolha,
            instanteVoto
        );

        try {
            return votoRepository.saveAndFlush(voto);
        } catch (DataIntegrityViolationException excecao) {
            throw new ConflitoDeNegocioException(
                "O associado com CPF " + cpfAssociado
                    + " já votou na pauta " + pautaId,
                excecao
            );
        }
    }

    private ConflitoDeNegocioException votoJaRegistrado(
        Long pautaId,
        String cpfAssociado
    ) {
        return new ConflitoDeNegocioException(
            "O associado com CPF " + cpfAssociado
                + " já votou na pauta " + pautaId
        );
    }
}