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
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ResultadoVotacaoControllerTest extends IntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PautaRepository pautaRepository;

    @Autowired
    private SessaoVotacaoRepository sessaoVotacaoRepository;

    @Autowired
    private VotoRepository votoRepository;

    @Test
    void deveContabilizarVotosEDarResultado() throws Exception {
        Pauta pauta = cadastrarPautaComSessaoEncerrada();
        Instant instanteDoVoto = Instant.now().minusSeconds(90);
        votoRepository.saveAllAndFlush(List.of(
            new Voto(pauta, "52998224725", EscolhaVoto.SIM, instanteDoVoto),
            new Voto(pauta, "11144477735", EscolhaVoto.SIM, instanteDoVoto),
            new Voto(pauta, "12345678909", EscolhaVoto.NAO, instanteDoVoto)
        ));

        mockMvc.perform(get("/api/v1/pautas/{pautaId}/resultado", pauta.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.pautaId").value(pauta.getId()))
            .andExpect(jsonPath("$.titulo").value("Pauta para resultado"))
            .andExpect(jsonPath("$.quantidadeVotosSim").value(2))
            .andExpect(jsonPath("$.quantidadeVotosNao").value(1))
            .andExpect(jsonPath("$.totalVotos").value(3))
            .andExpect(jsonPath("$.resultado").value("APROVADA"));
    }

    @Test
    void deveInformarResultadoSemVotos() throws Exception {
        Pauta pauta = cadastrarPautaComSessaoEncerrada();

        mockMvc.perform(get("/api/v1/pautas/{pautaId}/resultado", pauta.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.quantidadeVotosSim").value(0))
            .andExpect(jsonPath("$.quantidadeVotosNao").value(0))
            .andExpect(jsonPath("$.totalVotos").value(0))
            .andExpect(jsonPath("$.resultado").value("SEM_VOTOS"));
    }

    @Test
    void deveRecusarConsultaAntesDoEncerramentoDaSessao() throws Exception {
        Pauta pauta = cadastrarPauta();
        sessaoVotacaoRepository.saveAndFlush(new SessaoVotacao(
            pauta,
            Instant.now().minusSeconds(5),
            Instant.now().plusSeconds(60)
        ));

        mockMvc.perform(get("/api/v1/pautas/{pautaId}/resultado", pauta.getId()))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.mensagem")
                .value("O resultado da pauta " + pauta.getId()
                    + " estará disponível após o encerramento da sessão"));
    }

    @Test
    void deveRecusarConsultaQuandoNaoExisteSessao() throws Exception {
        Pauta pauta = cadastrarPauta();

        mockMvc.perform(get("/api/v1/pautas/{pautaId}/resultado", pauta.getId()))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.mensagem")
                .value("Sessão de votação não encontrada para a pauta " + pauta.getId()));
    }

    private Pauta cadastrarPautaComSessaoEncerrada() {
        Pauta pauta = cadastrarPauta();
        sessaoVotacaoRepository.saveAndFlush(new SessaoVotacao(
            pauta,
            Instant.now().minusSeconds(120),
            Instant.now().minusSeconds(60)
        ));
        return pauta;
    }

    private Pauta cadastrarPauta() {
        return pautaRepository.saveAndFlush(
            new Pauta("Pauta para resultado", "Descrição da pauta")
        );
    }
}