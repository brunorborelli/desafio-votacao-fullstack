package com.brunoborelli.votacao.client;

import com.brunoborelli.votacao.exception.RecursoNaoEncontradoException;
import com.brunoborelli.votacao.validation.ValidadorCpf;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadLocalRandom;
import java.util.function.BooleanSupplier;

@Component
public class ClientAutorizacaoVotoFalso implements ClientAutorizacaoVoto {

    private final ValidadorCpf validadorCpf;
    private final BooleanSupplier sorteador;

    @Autowired
    public ClientAutorizacaoVotoFalso(ValidadorCpf validadorCpf) {
        this(validadorCpf, () -> ThreadLocalRandom.current().nextBoolean());
    }

    ClientAutorizacaoVotoFalso(
        ValidadorCpf validadorCpf,
        BooleanSupplier sorteador
    ) {
        this.validadorCpf = validadorCpf;
        this.sorteador = sorteador;
    }

    @Override
    public StatusAutorizacaoVoto consultar(String cpf) {
        if (!validadorCpf.ehValido(cpf)) {
            throw new RecursoNaoEncontradoException("CPF não encontrado");
        }

        return sorteador.getAsBoolean()
            ? StatusAutorizacaoVoto.ABLE_TO_VOTE
            : StatusAutorizacaoVoto.UNABLE_TO_VOTE;
    }
}