package com.brunoborelli.votacao.validation;

import org.springframework.stereotype.Component;

@Component
public class ValidadorCpf {

    private static final int TAMANHO_CPF = 11;

    public boolean ehValido(String cpf) {
        if (cpf == null || cpf.length() != TAMANHO_CPF) {
            return false;
        }

        if (!possuiSomenteDigitos(cpf) || possuiTodosOsDigitosIguais(cpf)) {
            return false;
        }

        int primeiroDigitoVerificador = calcularDigitoVerificador(cpf, 9);
        int segundoDigitoVerificador = calcularDigitoVerificador(cpf, 10);

        return primeiroDigitoVerificador == converterParaNumero(cpf.charAt(9))
            && segundoDigitoVerificador == converterParaNumero(cpf.charAt(10));
    }

    private boolean possuiSomenteDigitos(String cpf) {
        for (int indice = 0; indice < cpf.length(); indice++) {
            char caractere = cpf.charAt(indice);
            if (caractere < '0' || caractere > '9') {
                return false;
            }
        }

        return true;
    }

    private boolean possuiTodosOsDigitosIguais(String cpf) {
        char primeiroDigito = cpf.charAt(0);

        for (int indice = 1; indice < cpf.length(); indice++) {
            if (cpf.charAt(indice) != primeiroDigito) {
                return false;
            }
        }

        return true;
    }

    private int calcularDigitoVerificador(String cpf, int quantidadeDigitos) {
        int soma = 0;
        int pesoInicial = quantidadeDigitos + 1;

        for (int indice = 0; indice < quantidadeDigitos; indice++) {
            soma += converterParaNumero(cpf.charAt(indice)) * (pesoInicial - indice);
        }

        int resultado = 11 - (soma % 11);
        return resultado >= 10 ? 0 : resultado;
    }

    private int converterParaNumero(char digito) {
        return digito - '0';
    }
}