package com.brunoborelli.votacao;

import com.brunoborelli.votacao.entity.Pauta;
import com.brunoborelli.votacao.repository.PautaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class PautaControllerTest extends IntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PautaRepository pautaRepository;

    @Test
    void deveCadastrarPauta() throws Exception {
        mockMvc.perform(post("/api/v1/pautas")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "titulo": "Melhoria na área de lazer",
                      "descricao": "Proposta de reforma da área de lazer"
                    }
                    """))
            .andExpect(status().isCreated())
            .andExpect(header().exists("Location"))
            .andExpect(jsonPath("$.id").isNumber())
            .andExpect(jsonPath("$.titulo").value("Melhoria na área de lazer"))
            .andExpect(jsonPath("$.descricao").value("Proposta de reforma da área de lazer"))
            .andExpect(jsonPath("$.dataCriacao").exists());
    }

    @Test
    void deveListarPautas() throws Exception {
        pautaRepository.saveAndFlush(new Pauta("Pauta existente", "Descrição da pauta"));

        mockMvc.perform(get("/api/v1/pautas"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].titulo").value("Pauta existente"));
    }

    @Test
    void deveBuscarPautaPorId() throws Exception {
        Pauta pauta = pautaRepository.saveAndFlush(
            new Pauta("Pauta para consulta", "Descrição da pauta")
        );

        mockMvc.perform(get("/api/v1/pautas/{id}", pauta.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(pauta.getId()))
            .andExpect(jsonPath("$.titulo").value("Pauta para consulta"));
    }

    @Test
    void deveRetornarErroAoBuscarPautaInexistente() throws Exception {
        mockMvc.perform(get("/api/v1/pautas/{id}", 999999))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(404))
            .andExpect(jsonPath("$.mensagem").value("Pauta não encontrada para o id 999999"));
    }

    @Test
    void deveValidarCamposObrigatorios() throws Exception {
        mockMvc.perform(post("/api/v1/pautas")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "titulo": " ",
                      "descricao": ""
                    }
                    """))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.campos.titulo").value("O título é obrigatório"))
            .andExpect(jsonPath("$.campos.descricao").value("A descrição é obrigatória"));
    }
}