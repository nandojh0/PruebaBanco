/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.banco.demo.controller;

import com.banco.demo.dto.GenericCodeAndMessageResponse;
import com.banco.demo.dto.MovementDTO;
import com.banco.demo.dto.WsResponse;
import com.banco.demo.mapper.HttpStatusMapper;
import com.banco.demo.service.MovementService;
import com.banco.demo.utils.LogManager;
import com.banco.demo.utils.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author hernando.hernandez
 */

@RestController
@RequestMapping("/movimientos")
@RequiredArgsConstructor
public class MovementController {
    
    private final MovementService movementService;
    private final LogManager logManager;
    private final HttpStatusMapper httpStatusMapper;
    private final Validator validator;
    
     private static final String LOG_PREFIX_CLASS = "MovementController/";
    
    
    @PostMapping()
    public ResponseEntity<WsResponse<MovementDTO>> addMovement(@RequestBody MovementDTO movementDTO) {
        final String LOG_PREFIX = LOG_PREFIX_CLASS + "addMovement() ";
        logManager.logTransactionInfo(LOG_PREFIX + "Request received to add movement for account: " + movementDTO.getAccountNumber());
        try {
                    // Validación de campos
        final String invalidFields = validator.findInvalidFieldsMovement(movementDTO);
        if (!invalidFields.isEmpty()) {
            logManager.logTransactionWarning(LOG_PREFIX + "Bad format at fields: " + invalidFields);
            WsResponse<MovementDTO> response = WsResponse.<MovementDTO>builder()
                    .codeAndMessageBasicResponse(new GenericCodeAndMessageResponse("400",
                            "Bad format with fields: " + invalidFields))
                    .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        WsResponse<MovementDTO> response = movementService.addMovement(movementDTO);
        HttpStatus status = httpStatusMapper.mapToHttpStatus(response.getCodeAndMessageBasicResponse().getCode());
        return ResponseEntity.status(status)
                             .body(response);
        } catch (Exception e) {
            WsResponse<MovementDTO> response = WsResponse.<MovementDTO>builder()
                    .codeAndMessageBasicResponse(new GenericCodeAndMessageResponse("500", "An error has occurred"))
                    .build();
            logManager.logTransactionError(LOG_PREFIX + "Error: " + e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }

    }
    
}
