package com.brunoborelli.votacao.controller;

import com.brunoborelli.votacao.dto.AbrirSessaoVotacaoRequisicao;
import com.brunoborelli.votacao.dto.SessaoVotacaoResposta;
import com.brunoborelli.votacao.entity.SessaoVotacao;
import com.brunoborelli.votacao.service.SessaoVotacaoService;
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

@RestController
@RequestMapping("/api/v1/pautas/{pautaId}/sessao")
public class SessaoVotacaoController {

    private final SessaoVotacaoService sessaoVotacaoService;

    public SessaoVotacaoController(SessaoVotacaoService sessaoVotacaoService) {
        this.sessaoVotacaoService = sessaoVotacaoService;
    }

    @PostMapping
    public ResponseEntity<SessaoVotacaoResposta> abrir(
        @PathVariable Long pautaId,
        @Valid @RequestBody(required = false) AbrirSessaoVotacaoRequisicao requisicao
    ) {
        Integer duracaoMinutos = requisicao == null ? null : requisicao.duracaoMinutos();
        SessaoVotacao sessaoVotacao = sessaoVotacaoService.abrir(pautaId, duracaoMinutos);
        URI localizacao = ServletUriComponentsBuilder.fromCurrentRequest().build().toUri();

        return ResponseEntity.created(localizacao)
            .body(SessaoVotacaoResposta.de(sessaoVotacao));
    }

    @GetMapping
    public SessaoVotacaoResposta buscarPorPautaId(@PathVariable Long pautaId) {
        return SessaoVotacaoResposta.de(sessaoVotacaoService.buscarPorPautaId(pautaId));
    }
}