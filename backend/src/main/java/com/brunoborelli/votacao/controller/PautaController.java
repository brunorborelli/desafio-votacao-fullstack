package com.brunoborelli.votacao.controller;

import com.brunoborelli.votacao.dto.CriarPautaRequisicao;
import com.brunoborelli.votacao.dto.PautaResposta;
import com.brunoborelli.votacao.entity.Pauta;
import com.brunoborelli.votacao.service.PautaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/pautas")
@Tag(name = "Pautas", description = "Cadastro e consulta de pautas")
public class PautaController {

    private final PautaService pautaService;

    public PautaController(PautaService pautaService) {
        this.pautaService = pautaService;
    }

    @PostMapping
    @Operation(summary = "Cadastrar uma nova pauta")
    public ResponseEntity<PautaResposta> cadastrar(
        @Valid @RequestBody CriarPautaRequisicao requisicao
    ) {
        Pauta pauta = pautaService.cadastrar(requisicao.titulo(), requisicao.descricao());
        URI localizacao = ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(pauta.getId())
            .toUri();

        return ResponseEntity.created(localizacao).body(PautaResposta.de(pauta));
    }

    @GetMapping
    @Operation(summary = "Listar as pautas cadastradas")
    public List<PautaResposta> listar() {
        return pautaService.listar().stream()
            .map(PautaResposta::de)
            .toList();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar uma pauta pelo ID")
    public PautaResposta buscarPorId(@PathVariable Long id) {
        return PautaResposta.de(pautaService.buscarPorId(id));
    }
}