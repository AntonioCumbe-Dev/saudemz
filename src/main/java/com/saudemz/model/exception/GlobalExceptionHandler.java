
package com.saudemz.model.exception;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({
        AccessDeniedException.class,
        AuthorizationDeniedException.class
    })
    public ResponseEntity<Map<String, String>> tratarAcessoNegado(
            RuntimeException ex) {

        Map<String, String> resposta =
                Map.of("erro", "Não tem permissão para aceder a este recurso.");

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(resposta);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> tratarErroDeValidacao(
            IllegalArgumentException ex) {

        Map<String, String> resposta =
                Map.of("erro", ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(resposta);
    }

    @ExceptionHandler(ConflitoAgendaException.class)
    public ResponseEntity<Map<String, String>> tratarConflitoDeAgenda(
            ConflitoAgendaException ex) {

        Map<String, String> resposta =
                Map.of("erro", ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(resposta);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> tratarErroInterno(
            Exception ex) {

        Map<String, String> resposta =
                Map.of("erro",
                        "Ocorreu um erro interno. Tente novamente mais tarde.");

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(resposta);
    }
}
