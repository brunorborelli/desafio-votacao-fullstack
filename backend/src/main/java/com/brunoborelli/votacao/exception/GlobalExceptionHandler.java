package com.brunoborelli.votacao.exception;

import com.brunoborelli.votacao.dto.ErroResposta;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErroResposta> tratarCorpoDaRequisicaoInvalido(
            HttpMessageNotReadableException excecao,
            HttpServletRequest requisicao
    ) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ErroResposta erro = new ErroResposta(
                Instant.now(),
                status.value(),
                "Requisição inválida",
                "O corpo da requisição está ausente ou possui valores inválidos",
                requisicao.getRequestURI(),
                Map.of()
        );

        return ResponseEntity.status(status).body(erro);
    }


    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<ErroResposta> tratarRecursoNaoEncontrado(
        RecursoNaoEncontradoException excecao,
        HttpServletRequest requisicao
    ) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        ErroResposta erro = new ErroResposta(
            Instant.now(),
            status.value(),
            "Recurso não encontrado",
            excecao.getMessage(),
            requisicao.getRequestURI(),
            Map.of()
        );

        return ResponseEntity.status(status).body(erro);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroResposta> tratarValidacao(
        MethodArgumentNotValidException excecao,
        HttpServletRequest requisicao
    ) {
        Map<String, String> campos = new LinkedHashMap<>();
        for (FieldError erroCampo : excecao.getBindingResult().getFieldErrors()) {
            campos.putIfAbsent(erroCampo.getField(), erroCampo.getDefaultMessage());
        }

        HttpStatus status = HttpStatus.BAD_REQUEST;
        ErroResposta erro = new ErroResposta(
            Instant.now(),
            status.value(),
            "Requisição inválida",
            "Um ou mais campos estão inválidos",
            requisicao.getRequestURI(),
            campos
        );

        return ResponseEntity.status(status).body(erro);
    }

    @ExceptionHandler(ConflitoDeNegocioException.class)
    public ResponseEntity<ErroResposta> tratarConflitoDeNegocio(
            ConflitoDeNegocioException excecao,
            HttpServletRequest requisicao
    ) {
        HttpStatus status = HttpStatus.CONFLICT;
        ErroResposta erro = new ErroResposta(
                Instant.now(),
                status.value(),
                "Conflito",
                excecao.getMessage(),
                requisicao.getRequestURI(),
                Map.of()
        );

        return ResponseEntity.status(status).body(erro);
    }
}