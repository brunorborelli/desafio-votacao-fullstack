package com.brunoborelli.votacao.controller;

import com.brunoborelli.votacao.dto.RegistrarVotoRequisicao;
import com.brunoborelli.votacao.dto.ResultadoVotacaoResposta;
import com.brunoborelli.votacao.dto.VotoResposta;
import com.brunoborelli.votacao.entity.Voto;
import com.brunoborelli.votacao.service.VotoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/pautas/{pautaId}")
@Tag(name = "Votação", description = "Registro de votos e resultado das pautas")
public class VotoController {

    private final VotoService votoService;

    public VotoController(VotoService votoService) {
        this.votoService = votoService;
    }

    @PostMapping("/votos")
    @Operation(summary = "Registrar o voto de um associado")
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


    @GetMapping("/resultado")
    @Operation(summary = "Consultar o resultado final de uma pauta")
    public ResultadoVotacaoResposta obterResultado(@PathVariable Long pautaId) {
        return votoService.obterResultado(pautaId);
    }
}