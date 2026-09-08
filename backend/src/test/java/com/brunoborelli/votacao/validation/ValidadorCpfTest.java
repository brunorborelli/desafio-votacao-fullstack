package com.brunoborelli.votacao.validation;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ValidadorCpfTest {

    private final ValidadorCpf validadorCpf = new ValidadorCpf();

    @Test
    void deveAceitarCpfValido() {
        assertThat(validadorCpf.ehValido("52998224725")).isTrue();
        assertThat(validadorCpf.ehValido("11144477735")).isTrue();
    }

    @Test
    void deveRecusarCpfComDigitoVerificadorInvalido() {
        assertThat(validadorCpf.ehValido("52998224724")).isFalse();
    }

    @Test
    void deveRecusarCpfComTodosOsDigitosIguais() {
        assertThat(validadorCpf.ehValido("00000000000")).isFalse();
        assertThat(validadorCpf.ehValido("11111111111")).isFalse();
    }

    @Test
    void deveRecusarCpfComFormatoInvalido() {
        assertThat(validadorCpf.ehValido(null)).isFalse();
        assertThat(validadorCpf.ehValido("")).isFalse();
        assertThat(validadorCpf.ehValido("5299822472")).isFalse();
        assertThat(validadorCpf.ehValido("529982247250")).isFalse();
        assertThat(validadorCpf.ehValido("529.982.247-25")).isFalse();
        assertThat(validadorCpf.ehValido("5299822472A")).isFalse();
    }
}