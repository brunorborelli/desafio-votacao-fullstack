package com.brunoborelli.votacao.dto;

import com.brunoborelli.votacao.entity.EscolhaVoto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record RegistrarVotoRequisicao(
    @NotBlank(message = "O CPF do associado é obrigatório")
    @Pattern(regexp = "\\d{11}", message = "O CPF deve conter 11 dígitos")
    String cpfAssociado,

    @NotNull(message = "O voto é obrigatório")
    EscolhaVoto escolha
) {
}