/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.banco.demo.controller;

import com.banco.demo.dto.ClientDTO;
import com.banco.demo.dto.GenericCodeAndMessageResponse;
import com.banco.demo.dto.WsResponse;
import com.banco.demo.mapper.HttpStatusMapper;
import com.banco.demo.service.ClientService;
import com.banco.demo.utils.LogManager;
import com.banco.demo.utils.Validator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author hernando.hernandez
 */
@RestController
@RequestMapping("/clientes")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;
    private final LogManager logManager;
    private final HttpStatusMapper httpStatusMapper;
    private final Validator validator;
            
    private static final String LOG_PREFIX_CLASS = "ClientController/";

    @GetMapping
    public ResponseEntity<WsResponse<List<ClientDTO>>> getAll() {
        final String LOG_PREFIX = LOG_PREFIX_CLASS + "getAll() ";
        try {
            WsResponse<List<ClientDTO>> response = clientService.findAll();
            logManager.logTransactionInfo(LOG_PREFIX + "Response: " + response);
            HttpStatus status = httpStatusMapper.mapToHttpStatus(response.getCodeAndMessageBasicResponse().getCode());
            return ResponseEntity.status(status).body(response);
        } catch (Exception e) {
            WsResponse<List<ClientDTO>> response = WsResponse.<List<ClientDTO>>builder()
                    .codeAndMessageBasicResponse(new GenericCodeAndMessageResponse("500", "An error has occurred"))
                    .build();
            logManager.logTransactionError(LOG_PREFIX + "Error: " + e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<WsResponse<ClientDTO>> getById(@PathVariable Long id) {
        final String LOG_PREFIX = LOG_PREFIX_CLASS + "getById() ";
        try {
            WsResponse<ClientDTO> response = clientService.findById(id);
            HttpStatus status = httpStatusMapper.mapToHttpStatus(response.getCodeAndMessageBasicResponse().getCode());
            logManager.logTransactionInfo(LOG_PREFIX + "Response: " + response);
            return ResponseEntity.status(status).body(response);
        } catch (Exception e) {
            WsResponse<ClientDTO> response = WsResponse.<ClientDTO>builder()
                    .codeAndMessageBasicResponse(new GenericCodeAndMessageResponse("500", "An error has occurred"))
                    .build();
            logManager.logTransactionError(LOG_PREFIX + "Error: " + e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping
    public ResponseEntity<WsResponse<ClientDTO>> create(@RequestBody ClientDTO dto) {
        final String LOG_PREFIX = LOG_PREFIX_CLASS + "create() ";
        try {
            // Validación de campos
            final String findInvalidFieldsInUrlWs = validator.findInvalidFieldsClient(dto);
            if (!findInvalidFieldsInUrlWs.isEmpty()) {
                logManager.logTransactionInfo("CertificateController/loadCertificatesFromUrl() "
                        + "Bad format at fields: " + findInvalidFieldsInUrlWs);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(WsResponse.<ClientDTO>builder()
                                .codeAndMessageBasicResponse(new GenericCodeAndMessageResponse("400",
                                        "Bad format with fields: " + findInvalidFieldsInUrlWs))
                                .build()
                        );
            }
            WsResponse<ClientDTO> response = clientService.save(dto);
            HttpStatus status = httpStatusMapper.mapToHttpStatus(response.getCodeAndMessageBasicResponse().getCode());
            logManager.logTransactionInfo(LOG_PREFIX + "Response: " + response);
            return ResponseEntity.status(status).body(response);
        } catch (Exception e) {
            WsResponse<ClientDTO> response = WsResponse.<ClientDTO>builder()
                    .codeAndMessageBasicResponse(new GenericCodeAndMessageResponse("500", "An error has occurred"))
                    .build();
            logManager.logTransactionError(LOG_PREFIX + "Error: " + e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<WsResponse<ClientDTO>> update(@PathVariable Long id, @RequestBody ClientDTO dto) {
        final String LOG_PREFIX = LOG_PREFIX_CLASS + "update() ";
        try {
            WsResponse<ClientDTO> response = clientService.update(id, dto);
            HttpStatus status = httpStatusMapper.mapToHttpStatus(response.getCodeAndMessageBasicResponse().getCode());
            logManager.logTransactionInfo(LOG_PREFIX + "Response: " + response);
            return ResponseEntity.status(status).body(response);
        } catch (Exception e) {
            WsResponse<ClientDTO> response = WsResponse.<ClientDTO>builder()
                    .codeAndMessageBasicResponse(new GenericCodeAndMessageResponse("500", "An error has occurred"))
                    .build();
            logManager.logTransactionError(LOG_PREFIX + "Error: " + e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<WsResponse<String>> delete(@PathVariable Long id) {
        final String LOG_PREFIX = LOG_PREFIX_CLASS + "delete() ";
        try {
            WsResponse<String> response = clientService.delete(id);
            HttpStatus status = httpStatusMapper.mapToHttpStatus(response.getCodeAndMessageBasicResponse().getCode());
            logManager.logTransactionInfo(LOG_PREFIX + "Response: " + response);
            return ResponseEntity.status(status).body(response);
        } catch (Exception e) {
            WsResponse<String> response = WsResponse.<String>builder()
                    .codeAndMessageBasicResponse(new GenericCodeAndMessageResponse("500", "An error has occurred"))
                    .build();
            logManager.logTransactionError(LOG_PREFIX + "Error: " + e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
