package com.projeto.ecommerce.exception;

import com.projeto.ecommerce.exception.BadRequestException;
import com.projeto.ecommerce.exception.ResourceNotFoundException;
import com.projeto.ecommerce.response.ErroResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroResponse> handleTodasExcecoes(Exception ex, WebRequest request) {
        ErroResponse erroResponse = new ErroResponse(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Erro Interno do Servidor",
                ex.getLocalizedMessage(),
                request.getDescription(false)
        );
        return new ResponseEntity<>(erroResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErroResponse> handleRecursoNaoEncontradoException(ResourceNotFoundException ex, WebRequest request) {
        ErroResponse erroResponse = new ErroResponse(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                "Recurso Não Encontrado",
                ex.getLocalizedMessage(),
                request.getDescription(false)
        );
        return new ResponseEntity<>(erroResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErroResponse> handleRequisicaoInvalidaException(BadRequestException ex, WebRequest request) {
        ErroResponse erroResponse = new ErroResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Requisição Inválida",
                ex.getLocalizedMessage(),
                request.getDescription(false)
        );
        return new ResponseEntity<>(erroResponse, HttpStatus.BAD_REQUEST);
    }
}
