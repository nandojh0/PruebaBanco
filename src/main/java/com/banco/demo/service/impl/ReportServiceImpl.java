/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.banco.demo.service.impl;

import com.banco.demo.dto.AccountDTO;
import com.banco.demo.dto.GenericCodeAndMessageResponse;
import com.banco.demo.dto.MovementDTO;
import com.banco.demo.dto.MovementResponseDTO;
import com.banco.demo.dto.WsResponse;
import com.banco.demo.exception.CustomException;
import com.banco.demo.mapper.MovementMapper;
import com.banco.demo.model.Client;
import com.banco.demo.model.Movement;
import com.banco.demo.repository.AccountRepository;
import com.banco.demo.repository.ClientRepository;
import com.banco.demo.repository.MovementRepository;
import com.banco.demo.service.ReportService;
import com.banco.demo.utils.LogManager;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 *
 * @author hernando.hernandez
 */
@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final MovementRepository movementRepository;
    private final ClientRepository clientRepository;
    private final AccountRepository accountRepository;
    private final LogManager logManager;
    private final MovementMapper movementMapper;

    private static final String LOG_PREFIX_CLASS = "ReportServiceImpl/";

    @Override
    public WsResponse<List<MovementResponseDTO>> getReporte(Long clientId, LocalDateTime fechaDesde, LocalDateTime fechaHasta) {
        final String LOG_PREFIX = LOG_PREFIX_CLASS + "getReport() ";
        logManager.logTransactionInfo(LOG_PREFIX + "Start fetching report for clientId: " + clientId);

        try {
            // Verificar cliente
            Client client = clientRepository.findById(clientId)
                    .orElseThrow(() -> CustomException.builder()
                    .message("Cliente no encontrado con id " + clientId)
                    .errorCode("404")
                    .build());

            // Buscar movimientos filtrados
            List<Movement> movements = movementRepository.findMovementsByClientAndDateRange(clientId, fechaDesde, fechaHasta);

            if (movements.isEmpty()) {
                logManager.logTransactionInfo(LOG_PREFIX + "No movements found for client " + client.getClientId());
                return WsResponse.<List<MovementResponseDTO>>builder()
                        .codeAndMessageBasicResponse(new GenericCodeAndMessageResponse("404", "No hay movimientos en el rango de fechas"))
                        .build();
            }

            // Mapear a DTO
            List<MovementResponseDTO> report = movements.stream()
                    .map(MovementMapper::toResponseDTO)
                    .toList();

            logManager.logTransactionInfo(LOG_PREFIX + "Successfully generated report for client " + client.getClientId());

            return WsResponse.<List<MovementResponseDTO>>builder()
                    .codeAndMessageBasicResponse(new GenericCodeAndMessageResponse("200", "success"))
                    .data(report)
                    .build();

        } catch (CustomException ce) {
            logManager.logTransactionError(LOG_PREFIX + "Custom error: ", ce);
            return WsResponse.<List<MovementResponseDTO>>builder()
                    .codeAndMessageBasicResponse(new GenericCodeAndMessageResponse(ce.getErrorCode(), ce.getMessage()))
                    .build();
        } catch (Exception e) {
            logManager.logTransactionError(LOG_PREFIX + "Error generating report: ", e);
            return WsResponse.<List<MovementResponseDTO>>builder()
                    .codeAndMessageBasicResponse(new GenericCodeAndMessageResponse("500", "An error has occurred"))
                    .build();
        }
    }

    @Override
    public WsResponse<List<AccountDTO>> getAccountReport(Long clientId, LocalDate startDate, LocalDate endDate) {
        final String LOG_PREFIX = LOG_PREFIX_CLASS + "getAccountReport() ";
        logManager.logTransactionInfo(LOG_PREFIX + "Start generating report for client: " + clientId
                + ", from " + startDate + " to " + endDate);
        try {
            Client client = clientRepository.findById(clientId)
                    .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

            List<AccountDTO> accountDTOs = accountRepository.findByClient(client)
                    .stream()
                    .map(account -> {
                        List<MovementDTO> movements = movementRepository
                                .findByAccountAndDateBetween(account, startDate.atStartOfDay(), endDate.atTime(23, 59, 59))
                                .stream()
                                .map(movementMapper::toDto)
                                .toList();

                        return AccountDTO.builder()
                                .accountNumber(account.getAccountNumber())
                                .accountType(account.getAccountType())
                                .balance(account.getBalance())
                                .status(account.getStatus())
                                .clientId(client.getClientId())
                                .movements(movements)
                                .build();
                    })
                    .toList();

            logManager.logTransactionInfo(LOG_PREFIX + "Report generated successfully for client: " + clientId);

            return WsResponse.<List<AccountDTO>>builder()
                    .codeAndMessageBasicResponse(new GenericCodeAndMessageResponse("200", "success"))
                    .data(accountDTOs)
                    .build();

        } catch (Exception e) {
            logManager.logTransactionError(LOG_PREFIX + "Error generating report: ", e);
            return WsResponse.<List<AccountDTO>>builder()
                    .codeAndMessageBasicResponse(new GenericCodeAndMessageResponse("500", "An error has occurred"))
                    .build();
        }
    }

}
