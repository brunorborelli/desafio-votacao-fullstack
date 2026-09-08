package com.brunoborelli.votacao.client;

import com.brunoborelli.votacao.exception.RecursoNaoEncontradoException;
import com.brunoborelli.votacao.validation.ValidadorCpf;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ClientAutorizacaoVotoFalsoTest {

    private final ValidadorCpf validadorCpf = new ValidadorCpf();

    @Test
    void deveRetornarAutorizacaoParaVotar() {
        ClientAutorizacaoVotoFalso cliente = new ClientAutorizacaoVotoFalso(
            validadorCpf,
            () -> true
        );

        assertThat(cliente.consultar("52998224725"))
            .isEqualTo(StatusAutorizacaoVoto.ABLE_TO_VOTE);
    }

    @Test
    void deveRetornarAusenciaDeAutorizacaoParaVotar() {
        ClientAutorizacaoVotoFalso cliente = new ClientAutorizacaoVotoFalso(
            validadorCpf,
            () -> false
        );

        assertThat(cliente.consultar("52998224725"))
            .isEqualTo(StatusAutorizacaoVoto.UNABLE_TO_VOTE);
    }

    @Test
    void deveRetornarErroParaCpfInvalido() {
        ClientAutorizacaoVotoFalso cliente = new ClientAutorizacaoVotoFalso(
            validadorCpf,
            () -> true
        );

        assertThatThrownBy(() -> cliente.consultar("52998224724"))
            .isInstanceOf(RecursoNaoEncontradoException.class)
            .hasMessage("CPF não encontrado");
    }
}