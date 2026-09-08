package com.brunoborelli.votacao.controller;

import com.brunoborelli.votacao.dto.RegistrarVotoRequisicao;
import com.brunoborelli.votacao.dto.VotoResposta;
import com.brunoborelli.votacao.entity.Voto;
import com.brunoborelli.votacao.service.VotoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/pautas/{pautaId}/votos")
public class VotoController {

    private final VotoService votoService;

    public VotoController(VotoService votoService) {
        this.votoService = votoService;
    }

    @PostMapping
    public ResponseEntity<VotoResposta> registrar(
        @PathVariable Long pautaId,
        @Valid @RequestBody RegistrarVotoRequisicao requisicao
    ) {
        Voto voto = votoService.registrar(
            pautaId,
            requisicao.cpfAssociado(),
            requisicao.escolha()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(VotoResposta.de(voto));
    }
}