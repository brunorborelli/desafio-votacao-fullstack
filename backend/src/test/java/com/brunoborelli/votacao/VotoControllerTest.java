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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class VotoControllerTest extends IntegrationTest {

    private static final String CPF_VALIDO = "52998224725";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PautaRepository pautaRepository;

    @Autowired
    private SessaoVotacaoRepository sessaoVotacaoRepository;

    @Autowired
    private VotoRepository votoRepository;

    @Test
    void deveRegistrarVotoEmSessaoAberta() throws Exception {
        Pauta pauta = cadastrarPautaComSessaoAberta();

        mockMvc.perform(post("/api/v1/pautas/{pautaId}/votos", pauta.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "cpfAssociado": "52998224725",
                      "escolha": "SIM"
                    }
                    """))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").isNumber())
            .andExpect(jsonPath("$.pautaId").value(pauta.getId()))
            .andExpect(jsonPath("$.cpfAssociado").value(CPF_VALIDO))
            .andExpect(jsonPath("$.escolha").value("SIM"))
            .andExpect(jsonPath("$.dataCriacao").exists());

        Voto voto = votoRepository.findByPautaIdAndCpfAssociado(
            pauta.getId(),
            CPF_VALIDO
        ).orElseThrow();
        assertThat(voto.getEscolha()).isEqualTo(EscolhaVoto.SIM);
    }

    @Test
    void deveRegistrarVotoNao() throws Exception {
        Pauta pauta = cadastrarPautaComSessaoAberta();

        mockMvc.perform(post("/api/v1/pautas/{pautaId}/votos", pauta.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "cpfAssociado": "11144477735",
                      "escolha": "NAO"
                    }
                    """))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.escolha").value("NAO"));
    }

    @Test
    void deveRecusarCpfInvalido() throws Exception {
        Pauta pauta = cadastrarPautaComSessaoAberta();

        mockMvc.perform(post("/api/v1/pautas/{pautaId}/votos", pauta.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "cpfAssociado": "52998224724",
                      "escolha": "SIM"
                    }
                    """))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.mensagem").value("CPF inválido"));
    }

    @Test
    void deveRecusarVotoSemSessao() throws Exception {
        Pauta pauta = cadastrarPauta();

        mockMvc.perform(post("/api/v1/pautas/{pautaId}/votos", pauta.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(requisicaoDeVoto(CPF_VALIDO, "SIM")))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.mensagem")
                .value("Sessão de votação não encontrada para a pauta " + pauta.getId()));
    }

    @Test
    void deveRecusarVotoComSessaoEncerrada() throws Exception {
        Pauta pauta = cadastrarPauta();
        sessaoVotacaoRepository.saveAndFlush(new SessaoVotacao(
            pauta,
            Instant.now().minusSeconds(120),
            Instant.now().minusSeconds(60)
        ));

        mockMvc.perform(post("/api/v1/pautas/{pautaId}/votos", pauta.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(requisicaoDeVoto(CPF_VALIDO, "SIM")))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.mensagem")
                .value("A sessão de votação da pauta " + pauta.getId() + " não está aberta"));
    }

    @Test
    void deveRecusarSegundoVotoDoMesmoAssociadoNaPauta() throws Exception {
        Pauta pauta = cadastrarPautaComSessaoAberta();
        votoRepository.saveAndFlush(new Voto(pauta, CPF_VALIDO, EscolhaVoto.SIM));

        mockMvc.perform(post("/api/v1/pautas/{pautaId}/votos", pauta.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(requisicaoDeVoto(CPF_VALIDO, "NAO")))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.mensagem")
                .value("O associado com CPF " + CPF_VALIDO
                    + " já votou na pauta " + pauta.getId()));
    }

    @Test
    void deveRecusarEscolhaInvalida() throws Exception {
        Pauta pauta = cadastrarPautaComSessaoAberta();

        mockMvc.perform(post("/api/v1/pautas/{pautaId}/votos", pauta.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(requisicaoDeVoto(CPF_VALIDO, "TALVEZ")))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.mensagem")
                .value("O corpo da requisição está ausente ou possui valores inválidos"));
    }

    private Pauta cadastrarPautaComSessaoAberta() {
        Pauta pauta = cadastrarPauta();
        sessaoVotacaoRepository.saveAndFlush(new SessaoVotacao(
            pauta,
            Instant.now().minusSeconds(5),
            Instant.now().plusSeconds(60)
        ));
        return pauta;
    }

    private Pauta cadastrarPauta() {
        return pautaRepository.saveAndFlush(
            new Pauta("Pauta para votação", "Descrição da pauta")
        );
    }

    private String requisicaoDeVoto(String cpfAssociado, String escolha) {
        return """
            {
              "cpfAssociado": "%s",
              "escolha": "%s"
            }
            """.formatted(cpfAssociado, escolha);
    }
}