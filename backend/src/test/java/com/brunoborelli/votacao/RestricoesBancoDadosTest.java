package com.brunoborelli.votacao;

import com.brunoborelli.votacao.entity.EscolhaVoto;
import com.brunoborelli.votacao.entity.Pauta;
import com.brunoborelli.votacao.entity.SessaoVotacao;
import com.brunoborelli.votacao.entity.Voto;
import com.brunoborelli.votacao.repository.PautaRepository;
import com.brunoborelli.votacao.repository.SessaoVotacaoRepository;
import com.brunoborelli.votacao.repository.VotoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class RestricoesBancoDadosTest extends IntegrationTest {

    private static final String CPF_ASSOCIADO = "52998224725";

    @Autowired
    private PautaRepository pautaRepository;

    @Autowired
    private SessaoVotacaoRepository sessaoVotacaoRepository;

    @Autowired
    private VotoRepository votoRepository;

    @Test
    void deveImpedirDuasSessoesParaMesmaPautaNoBancoDeDados() {
        Pauta pauta = cadastrarPauta("Pauta com uma sessão");
        Instant inicio = Instant.now();

        sessaoVotacaoRepository.saveAndFlush(
            new SessaoVotacao(pauta, inicio, inicio.plusSeconds(60))
        );

        assertThatThrownBy(() -> sessaoVotacaoRepository.saveAndFlush(
            new SessaoVotacao(pauta, inicio, inicio.plusSeconds(120))
        )).isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void deveImpedirDoisVotosDoMesmoAssociadoNaMesmaPautaNoBancoDeDados() {
        Pauta pauta = cadastrarPauta("Pauta com um voto por associado");

        votoRepository.saveAndFlush(
            new Voto(pauta, CPF_ASSOCIADO, EscolhaVoto.SIM)
        );

        assertThatThrownBy(() -> votoRepository.saveAndFlush(
            new Voto(pauta, CPF_ASSOCIADO, EscolhaVoto.NAO)
        )).isInstanceOf(DataIntegrityViolationException.class);
    }

    private Pauta cadastrarPauta(String titulo) {
        return pautaRepository.saveAndFlush(
            new Pauta(titulo, "Descrição da pauta")
        );
    }
}