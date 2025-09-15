/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.banco.demo.controller;

import com.banco.demo.dto.AccountDTO;
import com.banco.demo.dto.GenericCodeAndMessageResponse;
import com.banco.demo.dto.MovementResponseDTO;
import com.banco.demo.dto.WsResponse;
import com.banco.demo.mapper.HttpStatusMapper;
import com.banco.demo.service.ReportService;
import com.banco.demo.utils.LogManager;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author hernando.hernandez
 */
@RestController
@RequestMapping("/reportes")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;
    private final LogManager logManager;
    private final HttpStatusMapper httpStatusMapper;

    private static final String LOG_PREFIX_CLASS = "ReportController/";

    /**
     * Endpoint para obtener reportes de movimientos por cliente y rango de fechas
     *
     * @param clientId   ID del cliente
     * @param fechaDesde Fecha inicial del rango (ISO_LOCAL_DATE_TIME)
     * @param fechaHasta Fecha final del rango (ISO_LOCAL_DATE_TIME)
     * @return Lista de movimientos con detalles de cuenta y cliente
     */
    @GetMapping
    public ResponseEntity<WsResponse<List<MovementResponseDTO>>> getReport(
            @RequestParam Long clientId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaDesde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaHasta
    ) {
        final String LOG_PREFIX = LOG_PREFIX_CLASS + "getReport() ";
        try {
            logManager.logTransactionInfo(LOG_PREFIX + "Fetching report for clientId=" + clientId +
                    ", fechaDesde=" + fechaDesde + ", fechaHasta=" + fechaHasta);

            WsResponse<List<MovementResponseDTO>> response = reportService.getReporte(clientId, fechaDesde, fechaHasta);

            logManager.logTransactionInfo(LOG_PREFIX + "Response: " + response);
            HttpStatus status = httpStatusMapper.mapToHttpStatus(response.getCodeAndMessageBasicResponse().getCode());
            return ResponseEntity.status(status).body(response);

        } catch (Exception e) {
            WsResponse<List<MovementResponseDTO>> response = WsResponse.<List<MovementResponseDTO>>builder()
                    .codeAndMessageBasicResponse(new GenericCodeAndMessageResponse("500", "An error has occurred"))
                    .build();
            logManager.logTransactionError(LOG_PREFIX + "Error: " + e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @PostMapping("/estado-cuenta")
    public ResponseEntity<WsResponse<List<AccountDTO>>> getAccountReport(
            @RequestParam Long clientId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaDesde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaHasta
    ) {
        final String LOG_PREFIX = LOG_PREFIX_CLASS + "getAccountReport() ";
        try {
            logManager.logTransactionInfo(LOG_PREFIX + "Fetching account report for clientId=" + clientId +
                    ", fechaDesde=" + fechaDesde + ", fechaHasta=" + fechaHasta);

            WsResponse<List<AccountDTO>> response =
                    reportService.getAccountReport(clientId, fechaDesde, fechaHasta);

            logManager.logTransactionInfo(LOG_PREFIX + "Response generated successfully");
            HttpStatus status = httpStatusMapper.mapToHttpStatus(
                    response.getCodeAndMessageBasicResponse().getCode()
            );
            return ResponseEntity.status(status).body(response);

        } catch (Exception e) {
            logManager.logTransactionError(LOG_PREFIX + "Error generating account report", e);
            WsResponse<List<AccountDTO>> response =
                    WsResponse.<List<AccountDTO>>builder()
                            .codeAndMessageBasicResponse(new GenericCodeAndMessageResponse("500", "An error has occurred"))
                            .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}

