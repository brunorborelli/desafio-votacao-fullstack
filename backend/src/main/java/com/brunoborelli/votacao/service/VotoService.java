package com.brunoborelli.votacao.service;

import com.brunoborelli.votacao.client.ClientAutorizacaoVoto;
import com.brunoborelli.votacao.client.StatusAutorizacaoVoto;
import com.brunoborelli.votacao.dto.ResultadoVotacaoResposta;
import com.brunoborelli.votacao.entity.EscolhaVoto;
import com.brunoborelli.votacao.entity.SessaoVotacao;
import com.brunoborelli.votacao.entity.SituacaoResultadoVotacao;
import com.brunoborelli.votacao.entity.Voto;
import com.brunoborelli.votacao.exception.ConflitoDeNegocioException;
import com.brunoborelli.votacao.exception.RecursoNaoEncontradoException;
import com.brunoborelli.votacao.repository.ContagemVotoPorEscolha;
import com.brunoborelli.votacao.repository.VotoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class VotoService {

    private static final Logger LOGGER = LoggerFactory.getLogger(VotoService.class);

    private final VotoRepository votoRepository;
    private final SessaoVotacaoService sessaoVotacaoService;
    private final ClientAutorizacaoVoto clientAutorizacaoVoto;

    public VotoService(
        VotoRepository votoRepository,
        SessaoVotacaoService sessaoVotacaoService,
        ClientAutorizacaoVoto clientAutorizacaoVoto
    ) {
        this.votoRepository = votoRepository;
        this.sessaoVotacaoService = sessaoVotacaoService;
        this.clientAutorizacaoVoto = clientAutorizacaoVoto;
    }

    @Transactional
    public Voto registrar(Long pautaId, String cpfAssociado, EscolhaVoto escolha) {
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


        StatusAutorizacaoVoto statusAutorizacao =
                clientAutorizacaoVoto.consultar(cpfAssociado);

        if (statusAutorizacao != StatusAutorizacaoVoto.ABLE_TO_VOTE) {
            throw new RecursoNaoEncontradoException(
                    "Associado não autorizado a votar"
            );
        }

        Voto voto = new Voto(
            sessaoVotacao.getPauta(),
            cpfAssociado,
            escolha,
            instanteVoto
        );

        try {

            Voto votoSalvo = votoRepository.saveAndFlush(voto);
            LOGGER.debug(
                    "Voto registrado: votoId={}, pautaId={}, escolha={}",
                    votoSalvo.getId(),
                    pautaId,
                    escolha
            );
            return votoSalvo;
        } catch (DataIntegrityViolationException excecao) {
            throw new ConflitoDeNegocioException(
                "O associado com CPF " + cpfAssociado
                    + " já votou na pauta " + pautaId,
                excecao
            );
        }
    }

    @Transactional(readOnly = true)
    public ResultadoVotacaoResposta obterResultado(Long pautaId) {
        SessaoVotacao sessaoVotacao = sessaoVotacaoService.buscarPorPautaId(pautaId);

        if (!sessaoVotacao.estaEncerradaEm(Instant.now())) {
            throw new ConflitoDeNegocioException(
                    "O resultado da pauta " + pautaId
                            + " estará disponível após o encerramento da sessão"
            );
        }

        long quantidadeVotosSim = 0;
        long quantidadeVotosNao = 0;

        for (ContagemVotoPorEscolha contagem : votoRepository.contarPorPautaId(pautaId)) {
            if (contagem.getEscolha() == EscolhaVoto.SIM) {
                quantidadeVotosSim = contagem.getQuantidade();
            } else if (contagem.getEscolha() == EscolhaVoto.NAO) {
                quantidadeVotosNao = contagem.getQuantidade();
            }
        }


        ResultadoVotacaoResposta resultado = new ResultadoVotacaoResposta(
                pautaId,
                sessaoVotacao.getPauta().getTitulo(),
                quantidadeVotosSim,
                quantidadeVotosNao,
                quantidadeVotosSim + quantidadeVotosNao,
                SituacaoResultadoVotacao.de(quantidadeVotosSim, quantidadeVotosNao)
        );

        LOGGER.info(
                "Resultado apurado: pautaId={}, votosSim={}, votosNao={}, resultado={}",
                pautaId,
                quantidadeVotosSim,
                quantidadeVotosNao,
                resultado.resultado()
        );
        return resultado;
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