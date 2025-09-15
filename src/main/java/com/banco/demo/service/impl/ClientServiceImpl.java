/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.banco.demo.service.impl;

import com.banco.demo.dto.ClientDTO;
import com.banco.demo.dto.GenericCodeAndMessageResponse;
import com.banco.demo.dto.WsResponse;
import com.banco.demo.exception.CustomException;
import com.banco.demo.mapper.ClientMapper;
import com.banco.demo.model.Client;
import com.banco.demo.repository.ClientRepository;
import com.banco.demo.service.ClientService;
import com.banco.demo.utils.LogManager;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 *
 * @author hernando.hernandez
 */
@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;
    private final LogManager logManager;
    private final PasswordEncoder passwordEncoder;

    private static final String LOG_PREFIX_CLASS = "ClientServiceImpl/";

    @Override
    public WsResponse<List<ClientDTO>> findAll() {
        final String LOG_PREFIX = LOG_PREFIX_CLASS + "findAll() ";
        logManager.logTransactionInfo(LOG_PREFIX + "Start fetching all clients");
        try {
            List<ClientDTO> clients = clientRepository.findAll().stream()
                    .map(clientMapper::toDto)
                    .collect(Collectors.toList());
            logManager.logTransactionInfo(LOG_PREFIX + "Successfully fetched " + clients.size() + " clients");
            return WsResponse.<List<ClientDTO>>builder()
                    .codeAndMessageBasicResponse(new GenericCodeAndMessageResponse("200", "success"))
                    .data(clients)
                    .build();
        } catch (Exception e) {
            logManager.logTransactionError(LOG_PREFIX + "Error fetching clients: ", e);
            return WsResponse.<List<ClientDTO>>builder()
                    .codeAndMessageBasicResponse(new GenericCodeAndMessageResponse("500", "An error has occurred"))
                    .build();
        }
    }

    @Override
    public WsResponse<ClientDTO> findById(Long id) {
        final String LOG_PREFIX = LOG_PREFIX_CLASS + "findById() ";
        logManager.logTransactionInfo(LOG_PREFIX + "Start fetching client with ID: " + id);
        try {
            return clientRepository.findById(id)
                    .map(clientMapper::toDto)
                    .map(dto -> {
                        logManager.logTransactionInfo(LOG_PREFIX + "Successfully fetched client: " + dto.getClientId());
                        return WsResponse.<ClientDTO>builder()
                                .codeAndMessageBasicResponse(new GenericCodeAndMessageResponse("200", "success"))
                                .data(dto)
                                .build();
                    })
                    .orElseGet(() -> {
                        logManager.logTransactionInfo(LOG_PREFIX + "Client not found with ID: " + id);
                        return WsResponse.<ClientDTO>builder()
                                .codeAndMessageBasicResponse(new GenericCodeAndMessageResponse("404", "not found"))
                                .build();
                    });
        } catch (Exception e) {
            logManager.logTransactionError(LOG_PREFIX + "Error fetching client by ID: ", e);
            return WsResponse.<ClientDTO>builder()
                    .codeAndMessageBasicResponse(new GenericCodeAndMessageResponse("500", "An error has occurred"))
                    .build();
        }
    }

    @Override
    public WsResponse<ClientDTO> save(ClientDTO clientDTO) {
        final String LOG_PREFIX = LOG_PREFIX_CLASS + "save() ";
        logManager.logTransactionInfo(LOG_PREFIX + "Start saving client: " + clientDTO.getIdentification());
        try {
            Client client = clientMapper.toEntity(clientDTO);
            client.setPassword(passwordEncoder.encode(clientDTO.getPassword()));
            Client saved = clientRepository.save(client);
            logManager.logTransactionInfo(LOG_PREFIX + "Successfully saved client with ID: " + saved.getClientId());
            return WsResponse.<ClientDTO>builder()
                    .codeAndMessageBasicResponse(new GenericCodeAndMessageResponse("201", "Cliente creado"))
                    .data(clientMapper.toDto(saved))
                    .build();
        } catch (DataIntegrityViolationException dive) {
            Throwable rootCause = dive.getRootCause();
            if (rootCause instanceof SQLIntegrityConstraintViolationException sqlEx && sqlEx.getErrorCode() == 1062) {
                // Esto es un duplicado
                logManager.logTransactionError(LOG_PREFIX + "Duplicate identification: " + clientDTO.getIdentification(), sqlEx);
                return WsResponse.<ClientDTO>builder()
                        .codeAndMessageBasicResponse(new GenericCodeAndMessageResponse("409",
                                "La identificación ya existe: " + clientDTO.getIdentification()))
                        .build();
            }
            // Si es otra violación de constraint
            logManager.logTransactionError(LOG_PREFIX + "Data integrity violation", dive);
            return WsResponse.<ClientDTO>builder()
                    .codeAndMessageBasicResponse(new GenericCodeAndMessageResponse("500", "An error has occurred"))
                    .build();
        } catch (Exception e) {
            logManager.logTransactionError(LOG_PREFIX + "Error saving client: ", e);
            return WsResponse.<ClientDTO>builder()
                    .codeAndMessageBasicResponse(new GenericCodeAndMessageResponse("500", "An error has occurred"))
                    .build();
        }
    }

    @Override
    public WsResponse<ClientDTO> update(Long id, ClientDTO clientDTO) {
        final String LOG_PREFIX = LOG_PREFIX_CLASS + "update() ";
        logManager.logTransactionInfo(LOG_PREFIX + "Start updating client with ID: " + id);
        try {
            Client existing = clientRepository.findById(id)
                    .orElseThrow(() -> CustomException.builder()
                    .message("Cliente no encontrado con ID: " + id)
                    .errorCode("404")
                    .build());

            boolean valid = passwordEncoder.matches(clientDTO.getPassword(), existing.getPassword());
            if (!valid) {
                throw new RuntimeException("Contraseña inválida");
            }
            if (clientDTO.getPassword() != null && !clientDTO.getPassword().isBlank()) {
                clientDTO.setPassword(passwordEncoder.encode(clientDTO.getPassword()));
            }
            
            clientMapper.updateFromDto(clientDTO, existing);

            Client updated = clientRepository.save(existing);
            logManager.logTransactionInfo(LOG_PREFIX + "Successfully updated client with ID: " + updated.getClientId());

            return WsResponse.<ClientDTO>builder()
                    .codeAndMessageBasicResponse(new GenericCodeAndMessageResponse("200", "success"))
                    .data(clientMapper.toDto(updated))
                    .build();
        } catch (CustomException ce) {
            logManager.logTransactionError(LOG_PREFIX + "Custom error: ", ce);
            return WsResponse.<ClientDTO>builder()
                    .codeAndMessageBasicResponse(new GenericCodeAndMessageResponse(ce.getErrorCode(), ce.getMessage()))
                    .build();
        } catch (Exception e) {
            logManager.logTransactionError(LOG_PREFIX + "Error updating client: ", e);
            return WsResponse.<ClientDTO>builder()
                    .codeAndMessageBasicResponse(new GenericCodeAndMessageResponse("500", "An error has occurred"))
                    .build();
        }
    }

    @Override
    public WsResponse<String> delete(Long id) {
        final String LOG_PREFIX = LOG_PREFIX_CLASS + "delete() ";
        logManager.logTransactionInfo(LOG_PREFIX + "Start deleting client with ID: " + id);
        try {
            if (!clientRepository.existsById(id)) {
                logManager.logTransactionInfo(LOG_PREFIX + "Client not found with ID: " + id);
                return WsResponse.<String>builder()
                        .codeAndMessageBasicResponse(new GenericCodeAndMessageResponse("404", "Cliente no encontrado con ID: " + id))
                        .build();
            }
            clientRepository.deleteById(id);
            logManager.logTransactionInfo(LOG_PREFIX + "Successfully deleted client with ID: " + id);
            return WsResponse.<String>builder()
                    .codeAndMessageBasicResponse(new GenericCodeAndMessageResponse("200", "success"))
                    .build();
        } catch (Exception e) {
            logManager.logTransactionError(LOG_PREFIX + "Error deleting client: ", e);
            return WsResponse.<String>builder()
                    .codeAndMessageBasicResponse(new GenericCodeAndMessageResponse("500", "An error has occurred"))
                    .build();
        }
    }
}
