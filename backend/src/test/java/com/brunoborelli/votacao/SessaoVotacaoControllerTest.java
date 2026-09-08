package com.brunoborelli.votacao;

import com.brunoborelli.votacao.entity.Pauta;
import com.brunoborelli.votacao.entity.SessaoVotacao;
import com.brunoborelli.votacao.repository.PautaRepository;
import com.brunoborelli.votacao.repository.SessaoVotacaoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class SessaoVotacaoControllerTest extends IntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PautaRepository pautaRepository;

    @Autowired
    private SessaoVotacaoRepository sessaoVotacaoRepository;

    @Test
    void deveAbrirSessaoComDuracaoPadraoDeUmMinuto() throws Exception {
        Pauta pauta = cadastrarPauta();

        mockMvc.perform(post("/api/v1/pautas/{pautaId}/sessao", pauta.getId()))
            .andExpect(status().isCreated())
            .andExpect(header().exists("Location"))
            .andExpect(jsonPath("$.id").isNumber())
            .andExpect(jsonPath("$.pautaId").value(pauta.getId()))
            .andExpect(jsonPath("$.dataInicio").exists())
            .andExpect(jsonPath("$.dataFim").exists());

        SessaoVotacao sessao = sessaoVotacaoRepository.findByPautaId(pauta.getId())
            .orElseThrow();
        assertThat(Duration.between(sessao.getDataInicio(), sessao.getDataFim()))
            .isEqualTo(Duration.ofMinutes(1));
    }

    @Test
    void deveAbrirSessaoComDuracaoInformada() throws Exception {
        Pauta pauta = cadastrarPauta();

        mockMvc.perform(post("/api/v1/pautas/{pautaId}/sessao", pauta.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "duracaoMinutos": 5
                    }
                    """))
            .andExpect(status().isCreated());

        SessaoVotacao sessao = sessaoVotacaoRepository.findByPautaId(pauta.getId())
            .orElseThrow();
        assertThat(Duration.between(sessao.getDataInicio(), sessao.getDataFim()))
            .isEqualTo(Duration.ofMinutes(5));
    }

    @Test
    void deveBuscarSessaoPorPauta() throws Exception {
        Pauta pauta = cadastrarPauta();
        SessaoVotacao sessao = sessaoVotacaoRepository.saveAndFlush(
            new SessaoVotacao(pauta, Instant.now(), Instant.now().plusSeconds(60))
        );

        mockMvc.perform(get("/api/v1/pautas/{pautaId}/sessao", pauta.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(sessao.getId()))
            .andExpect(jsonPath("$.pautaId").value(pauta.getId()));
    }

    @Test
    void deveRecusarSegundaSessaoParaMesmaPauta() throws Exception {
        Pauta pauta = cadastrarPauta();
        sessaoVotacaoRepository.saveAndFlush(
            new SessaoVotacao(pauta, Instant.now(), Instant.now().plusSeconds(60))
        );

        mockMvc.perform(post("/api/v1/pautas/{pautaId}/sessao", pauta.getId()))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.status").value(409))
            .andExpect(jsonPath("$.mensagem")
                .value("Já existe uma sessão de votação para a pauta " + pauta.getId()));
    }

    @Test
    void deveRetornarErroAoAbrirSessaoParaPautaInexistente() throws Exception {
        mockMvc.perform(post("/api/v1/pautas/{pautaId}/sessao", 999999))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.mensagem")
                .value("Pauta não encontrada para o id 999999"));
    }

    @Test
    void deveValidarDuracaoDaSessao() throws Exception {
        Pauta pauta = cadastrarPauta();

        mockMvc.perform(post("/api/v1/pautas/{pautaId}/sessao", pauta.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "duracaoMinutos": 0
                    }
                    """))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.campos.duracaoMinutos")
                .value("A duração deve ser maior que zero"));
    }

    private Pauta cadastrarPauta() {
        return pautaRepository.saveAndFlush(
            new Pauta("Pauta para sessão", "Descrição da pauta")
        );
    }
}