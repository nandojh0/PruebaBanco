/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.banco.demo.controller;

import com.banco.demo.dto.AccountDTO;
import com.banco.demo.dto.GenericCodeAndMessageResponse;
import com.banco.demo.dto.MovementDTO;
import com.banco.demo.dto.WsResponse;
import com.banco.demo.mapper.HttpStatusMapper;
import com.banco.demo.service.AccountService;
import com.banco.demo.utils.LogManager;
import com.banco.demo.utils.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("/cuentas")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;
    private final LogManager logManager;
    private final HttpStatusMapper httpStatusMapper;

    @Autowired
    private Validator validator;

    private static final String LOG_PREFIX_CLASS = "AccountController/";

    @GetMapping("/{accountNumber}")
    public ResponseEntity<WsResponse<AccountDTO>> getAccountById(@PathVariable Long accountNumber) {
        final String LOG_PREFIX = LOG_PREFIX_CLASS + "getAccountById() ";
        logManager.logTransactionInfo(LOG_PREFIX + "Request received for account: " + accountNumber);
        WsResponse<AccountDTO> response = accountService.findById(accountNumber);
        HttpStatus status = httpStatusMapper.mapToHttpStatus(response.getCodeAndMessageBasicResponse().getCode());
        return ResponseEntity.status(status)
                             .body(response);
    }

    @PostMapping
    public ResponseEntity<WsResponse<AccountDTO>> createAccount(@RequestBody AccountDTO accountDTO) {
        final String LOG_PREFIX = LOG_PREFIX_CLASS + "createAccount() ";
        logManager.logTransactionInfo(LOG_PREFIX + "Request received to create account: " + accountDTO.getAccountNumber());

        // Validación de campos
        final String invalidFields = validator.findInvalidFieldsAccount(accountDTO);
        if (!invalidFields.isEmpty()) {
            logManager.logTransactionWarning(LOG_PREFIX + "Bad format at fields: " + invalidFields);
            WsResponse<AccountDTO> response = WsResponse.<AccountDTO>builder()
                    .codeAndMessageBasicResponse(new GenericCodeAndMessageResponse("400",
                            "Bad format with fields: " + invalidFields))
                    .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        WsResponse<AccountDTO> response = accountService.save(accountDTO);
        HttpStatus status = httpStatusMapper.mapToHttpStatus(response.getCodeAndMessageBasicResponse().getCode());
        return ResponseEntity.status(status)
                             .body(response);
    }

    @PutMapping("/{accountNumber}")
    public ResponseEntity<WsResponse<AccountDTO>> updateAccount(@PathVariable Long accountNumber,
                                                                @RequestBody AccountDTO accountDTO) {
        final String LOG_PREFIX = LOG_PREFIX_CLASS + "updateAccount() ";
        logManager.logTransactionInfo(LOG_PREFIX + "Request received to update account: " + accountNumber);

        WsResponse<AccountDTO> response = accountService.update(accountNumber, accountDTO);
        HttpStatus status = httpStatusMapper.mapToHttpStatus(response.getCodeAndMessageBasicResponse().getCode());
        return ResponseEntity.status(status)
                             .body(response);
    }

    @DeleteMapping("/{accountNumber}")
    public ResponseEntity<WsResponse<String>> deleteAccount(@PathVariable Long accountNumber) {
        final String LOG_PREFIX = LOG_PREFIX_CLASS + "deleteAccount() ";
        logManager.logTransactionInfo(LOG_PREFIX + "Request received to delete account: " + accountNumber);

        WsResponse<String> response = accountService.delete(accountNumber);
        HttpStatus status = httpStatusMapper.mapToHttpStatus(response.getCodeAndMessageBasicResponse().getCode());
        return ResponseEntity.status(status)
                             .body(response);
    }

}