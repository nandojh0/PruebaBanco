/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.banco.demo.mapper;

import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

/**
 *
 * @author hernando.hernandez
 */
@Service
public class HttpStatusMapper {

    private static final Map<String, HttpStatus> STATUS_MAP = Map.ofEntries(
            Map.entry("200", HttpStatus.OK),
            Map.entry("201", HttpStatus.CREATED),
            Map.entry("202", HttpStatus.ACCEPTED),
            Map.entry("204", HttpStatus.NO_CONTENT),
            Map.entry("400", HttpStatus.BAD_REQUEST),
            Map.entry("401", HttpStatus.UNAUTHORIZED),
            Map.entry("403", HttpStatus.FORBIDDEN),
            Map.entry("404", HttpStatus.NOT_FOUND),
            Map.entry("409", HttpStatus.CONFLICT),
            Map.entry("422", HttpStatus.UNPROCESSABLE_ENTITY),
            Map.entry("429", HttpStatus.TOO_MANY_REQUESTS),
            Map.entry("500", HttpStatus.INTERNAL_SERVER_ERROR),
            Map.entry("501", HttpStatus.NOT_IMPLEMENTED),
            Map.entry("503", HttpStatus.SERVICE_UNAVAILABLE)
    );

    /**
     * Mapea un código de respuesta (String) a HttpStatus.
     * Si no existe, retorna INTERNAL_SERVER_ERROR por defecto.
     * @param code
     * @return 
     */
    public HttpStatus mapToHttpStatus(String code) {
        return STATUS_MAP.getOrDefault(code, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

