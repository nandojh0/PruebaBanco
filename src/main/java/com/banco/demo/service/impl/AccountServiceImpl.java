/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.banco.demo.service.impl;

import com.banco.demo.dto.AccountDTO;
import com.banco.demo.dto.GenericCodeAndMessageResponse;
import com.banco.demo.dto.MovementDTO;
import com.banco.demo.dto.WsResponse;
import com.banco.demo.exception.CustomException;
import com.banco.demo.mapper.AccountMapper;
import com.banco.demo.mapper.MovementMapper;
import com.banco.demo.model.Account;
import com.banco.demo.model.Client;
import com.banco.demo.model.Movement;
import com.banco.demo.repository.AccountRepository;
import com.banco.demo.repository.ClientRepository;
import com.banco.demo.repository.MovementRepository;
import com.banco.demo.service.AccountService;
import com.banco.demo.service.MovementService;
import com.banco.demo.utils.LogManager;
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
public class AccountServiceImpl implements AccountService, MovementService {

    private final AccountRepository accountRepository;
    private final MovementRepository movementRepository;
    private final ClientRepository clientRepository;
    private final AccountMapper accountMapper;
    private final MovementMapper movementMapper;
    private final LogManager logManager;

    private static final String LOG_PREFIX_CLASS = "AccountServiceImpl/";

    @Override
    public WsResponse<AccountDTO> findById(Long accountNumber) {
        final String LOG_PREFIX = LOG_PREFIX_CLASS + "findById() ";
        logManager.logTransactionInfo(LOG_PREFIX + "Start fetching account with number: " + accountNumber);
        try {
            return accountRepository.findById(accountNumber)
                    .map(accountMapper::toDto)
                    .map(dto -> {
                        List<MovementDTO> last10Movements = movementRepository
                                .findTop10ByAccount_AccountNumberOrderByDateDesc(accountNumber)
                                .stream()
                                .map(movementMapper::toDto)
                                .toList();
                        dto.setMovements(last10Movements);
                        logManager.logTransactionInfo(LOG_PREFIX + "Successfully fetched account: " + dto.getAccountNumber());
                        return WsResponse.<AccountDTO>builder()
                                .codeAndMessageBasicResponse(new GenericCodeAndMessageResponse("200", "success"))
                                .data(dto)
                                .build();
                    })
                    .orElseGet(() -> {
                        logManager.logTransactionInfo(LOG_PREFIX + "Account not found with number: " + accountNumber);
                        return WsResponse.<AccountDTO>builder()
                                .codeAndMessageBasicResponse(new GenericCodeAndMessageResponse("404", "not found"))
                                .build();
                    });
        } catch (Exception e) {
            logManager.logTransactionError(LOG_PREFIX + "Error fetching account by number: ", e);
            return WsResponse.<AccountDTO>builder()
                    .codeAndMessageBasicResponse(new GenericCodeAndMessageResponse("500", "An error has occurred"))
                    .build();
        }
    }

    @Override
    public WsResponse<AccountDTO> save(AccountDTO accountDTO) {
        final String LOG_PREFIX = LOG_PREFIX_CLASS + "save() ";
        logManager.logTransactionInfo(LOG_PREFIX + "Start saving account: " + accountDTO.getAccountNumber());
        try {
            Client client = clientRepository.findById(accountDTO.getClientId())
                    .orElseThrow(() -> CustomException.builder()
                    .message("Cliente no encontrado")
                    .errorCode("404")
                    .build());

            Account account = accountMapper.toEntity(accountDTO);
            account.setClient(client);
            account.setStatus(true);

            Account saved = accountRepository.save(account);

            logManager.logTransactionInfo(LOG_PREFIX + "Successfully saved account with number: " + saved.getAccountNumber());
            return WsResponse.<AccountDTO>builder()
                    .codeAndMessageBasicResponse(new GenericCodeAndMessageResponse("201", "Cuenta creada"))
                    .data(accountMapper.toDto(saved))
                    .build();
        } catch (CustomException ce) {
            logManager.logTransactionError(LOG_PREFIX + "Custom error: ", ce);
            return WsResponse.<AccountDTO>builder()
                    .codeAndMessageBasicResponse(new GenericCodeAndMessageResponse(ce.getErrorCode(), ce.getMessage()))
                    .build();
        } catch (Exception e) {
            logManager.logTransactionError(LOG_PREFIX + "Error saving account: ", e);
            return WsResponse.<AccountDTO>builder()
                    .codeAndMessageBasicResponse(new GenericCodeAndMessageResponse("500", "An error has occurred"))
                    .build();
        }
    }

    @Override
    public WsResponse<AccountDTO> update(Long accountNumber, AccountDTO accountDTO) {
        final String LOG_PREFIX = LOG_PREFIX_CLASS + "update() ";
        logManager.logTransactionInfo(LOG_PREFIX + "Start updating account with number: " + accountNumber);
        try {
            Account existing = accountRepository.findById(accountNumber)
                    .orElseThrow(() -> CustomException.builder()
                    .message("Cuenta no encontrada")
                    .errorCode("404")
                    .build());

            accountMapper.updateFromDto(accountDTO, existing);
            Account updated = accountRepository.save(existing);

            logManager.logTransactionInfo(LOG_PREFIX + "Successfully updated account: " + updated.getAccountNumber());
            return WsResponse.<AccountDTO>builder()
                    .codeAndMessageBasicResponse(new GenericCodeAndMessageResponse("200", "success"))
                    .data(accountMapper.toDto(updated))
                    .build();
        } catch (CustomException ce) {
            logManager.logTransactionError(LOG_PREFIX + "Custom error: ", ce);
            return WsResponse.<AccountDTO>builder()
                    .codeAndMessageBasicResponse(new GenericCodeAndMessageResponse(ce.getErrorCode(), ce.getMessage()))
                    .build();
        } catch (Exception e) {
            logManager.logTransactionError(LOG_PREFIX + "Error updating account: ", e);
            return WsResponse.<AccountDTO>builder()
                    .codeAndMessageBasicResponse(new GenericCodeAndMessageResponse("500", "An error has occurred"))
                    .build();
        }
    }

    @Override
    public WsResponse<String> delete(Long accountNumber) {
        final String LOG_PREFIX = LOG_PREFIX_CLASS + "delete() ";
        logManager.logTransactionInfo(LOG_PREFIX + "Start deleting account with number: " + accountNumber);
        try {
            if (!accountRepository.existsById(accountNumber)) {
                logManager.logTransactionInfo(LOG_PREFIX + "Account not found with number: " + accountNumber);
                return WsResponse.<String>builder()
                        .codeAndMessageBasicResponse(new GenericCodeAndMessageResponse("404", "Cuenta no encontrada"))
                        .build();
            }
            accountRepository.deleteById(accountNumber);
            logManager.logTransactionInfo(LOG_PREFIX + "Successfully deleted account: " + accountNumber);
            return WsResponse.<String>builder()
                    .codeAndMessageBasicResponse(new GenericCodeAndMessageResponse("200", "success"))
                    .build();
        } catch (Exception e) {
            logManager.logTransactionError(LOG_PREFIX + "Error deleting account: ", e);
            return WsResponse.<String>builder()
                    .codeAndMessageBasicResponse(new GenericCodeAndMessageResponse("500", "An error has occurred"))
                    .build();
        }
    }

    @Override
    public WsResponse<MovementDTO> addMovement(MovementDTO movementDTO) {
        final String LOG_PREFIX = LOG_PREFIX_CLASS + "addMovement() ";
        logManager.logTransactionInfo(LOG_PREFIX + "Start adding movement to account: " + movementDTO.getAccountNumber());
        try {
            Account account = accountRepository.findById(movementDTO.getAccountNumber())
                    .orElseThrow(()
                            -> CustomException.builder()
                            .message("Cuenta no encontrada")
                            .errorCode("404")
                            .build());

            double newBalance = account.getBalance() + movementDTO.getValue();
            double initianBalance = account.getBalance();
            if (newBalance < 0) {
                logManager.logTransactionInfo(LOG_PREFIX + "Saldo no disponible for movement: " + movementDTO.getValue());
                return WsResponse.<MovementDTO>builder()
                        .codeAndMessageBasicResponse(new GenericCodeAndMessageResponse("400", "Saldo no disponible"))
                        .build();
            }

            account.setBalance(newBalance);
            accountRepository.save(account);

            Movement movement = Movement.builder()
                    .account(account)
                    .date(LocalDateTime.now())
                    .movementType(movementDTO.getValue() > 0 ? "DEPOSIT" : "WITHDRAWAL")
                    .value(movementDTO.getValue())
                    .initialBalance(initianBalance)
                    .balance(newBalance)
                    .build();

            Movement savedMovement = movementRepository.save(movement);
            logManager.logTransactionInfo(LOG_PREFIX + "Successfully added movement with ID: " + savedMovement.getMovementId());

            return WsResponse.<MovementDTO>builder()
                    .codeAndMessageBasicResponse(new GenericCodeAndMessageResponse("201", "Movimiento registrado"))
                    .data(movementMapper.toDto(savedMovement))
                    .build();
        } catch (CustomException ce) {
            logManager.logTransactionError(LOG_PREFIX + "Custom error: ", ce);
            return WsResponse.<MovementDTO>builder()
                    .codeAndMessageBasicResponse(new GenericCodeAndMessageResponse(ce.getErrorCode(), ce.getMessage()))
                    .build();
        } catch (Exception e) {
            logManager.logTransactionError(LOG_PREFIX + "Error adding movement: ", e);
            return WsResponse.<MovementDTO>builder()
                    .codeAndMessageBasicResponse(new GenericCodeAndMessageResponse("500", "An error has occurred"))
                    .build();
        }
    }

}
