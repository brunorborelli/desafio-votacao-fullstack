package com.brunoborelli.votacao.dto;

import java.time.Instant;
import java.util.Map;

public record ErroResposta(
    Instant instante,
    int status,
    String erro,
    String mensagem,
    String caminho,
    Map<String, String> campos
) {
}