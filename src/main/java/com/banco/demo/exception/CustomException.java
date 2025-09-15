/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.banco.demo.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 *
 * @author nando
 */
@Builder
@AllArgsConstructor
@Getter
public class CustomException extends RuntimeException {

    private final String message;
    private final String errorCode;
    private final Throwable cause;

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public Throwable getCause() {
        return cause;
    }

    // Método para obtener el estado HTTP basado en el código de error
    public HttpStatus getHttpStatus() {
        return switch (errorCode) {
            case "400" ->
                HttpStatus.BAD_REQUEST;
            case "401" ->
                HttpStatus.UNAUTHORIZED;
            case "404" ->
                HttpStatus.NOT_FOUND;
            case "409" ->
                HttpStatus.CONFLICT;
            case "500" ->
                HttpStatus.INTERNAL_SERVER_ERROR;
            default ->
                HttpStatus.INTERNAL_SERVER_ERROR;
        };
    }
}
