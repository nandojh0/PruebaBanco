/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package com.banco.demo.service.impl;

import com.banco.demo.dto.ClientDTO;
import com.banco.demo.mapper.ClientMapper;
import com.banco.demo.model.Client;
import com.banco.demo.repository.ClientRepository;
import com.banco.demo.utils.LogManager;
import com.banco.demo.dto.WsResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;



/**
 *
 * @author hernando.hernandez
 */


@ExtendWith(MockitoExtension.class)
public class ClientServiceImplTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private ClientMapper clientMapper;

    @Mock
    private LogManager logManager;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private ClientServiceImpl clientService;

    private Client client;
    private ClientDTO clientDTO;

    @BeforeEach
    void setUp() {
        client = new Client();
        client.setClientId(1L);
        client.setPassword("encodedPass");

        clientDTO = new ClientDTO();
        clientDTO.setClientId(1L);
        clientDTO.setPassword("rawPass");
    }

    // ========== findAll ==========

    @Test
    void testFindAll_success() {
        when(clientRepository.findAll()).thenReturn(List.of(client));
        when(clientMapper.toDto(client)).thenReturn(clientDTO);

        WsResponse<List<ClientDTO>> response = clientService.findAll();

        assertEquals("200", response.getCodeAndMessageBasicResponse().getCode());
        assertEquals(1, response.getData().size());
    }

    @Test
    void testFindAll_exception() {
        when(clientRepository.findAll()).thenThrow(new RuntimeException("DB error"));

        WsResponse<List<ClientDTO>> response = clientService.findAll();

        assertEquals("500", response.getCodeAndMessageBasicResponse().getCode());
        assertNull(response.getData());
    }

    // ========== findById ==========

    @Test
    void testFindById_success() {
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(clientMapper.toDto(client)).thenReturn(clientDTO);

        WsResponse<ClientDTO> response = clientService.findById(1L);

        assertEquals("200", response.getCodeAndMessageBasicResponse().getCode());
        assertEquals(1L, response.getData().getClientId());
    }

    @Test
    void testFindById_notFound() {
        when(clientRepository.findById(1L)).thenReturn(Optional.empty());

        WsResponse<ClientDTO> response = clientService.findById(1L);

        assertEquals("404", response.getCodeAndMessageBasicResponse().getCode());
        assertNull(response.getData());
    }

    @Test
    void testFindById_exception() {
        when(clientRepository.findById(1L)).thenThrow(new RuntimeException("DB error"));

        WsResponse<ClientDTO> response = clientService.findById(1L);

        assertEquals("500", response.getCodeAndMessageBasicResponse().getCode());
    }

    // ========== save ==========

    @Test
    void testSave_success() {
        when(clientMapper.toEntity(clientDTO)).thenReturn(client);
        when(passwordEncoder.encode("rawPass")).thenReturn("encodedPass");
        when(clientRepository.save(any(Client.class))).thenReturn(client);
        when(clientMapper.toDto(client)).thenReturn(clientDTO);

        WsResponse<ClientDTO> response = clientService.save(clientDTO);

        assertEquals("201", response.getCodeAndMessageBasicResponse().getCode());
        assertEquals(1L, response.getData().getClientId());
    }

    @Test
    void testSave_duplicateIdentification() {
        when(clientMapper.toEntity(clientDTO)).thenReturn(client);
        when(passwordEncoder.encode("rawPass")).thenReturn("encodedPass");

        SQLIntegrityConstraintViolationException sqlEx =
                new SQLIntegrityConstraintViolationException("Duplicate", "23000", 1062);
        DataIntegrityViolationException dive =
                new DataIntegrityViolationException("Duplicate", sqlEx);

        when(clientRepository.save(any(Client.class))).thenThrow(dive);

        WsResponse<ClientDTO> response = clientService.save(clientDTO);

        assertEquals("409", response.getCodeAndMessageBasicResponse().getCode());
    }

    @Test
    void testSave_otherConstraintViolation() {
        DataIntegrityViolationException dive =
                new DataIntegrityViolationException("Constraint error");
        when(clientMapper.toEntity(clientDTO)).thenReturn(client);
        when(passwordEncoder.encode("rawPass")).thenReturn("encodedPass");
        when(clientRepository.save(any(Client.class))).thenThrow(dive);

        WsResponse<ClientDTO> response = clientService.save(clientDTO);

        assertEquals("500", response.getCodeAndMessageBasicResponse().getCode());
    }

    @Test
    void testSave_exception() {
        when(clientMapper.toEntity(clientDTO)).thenThrow(new RuntimeException("Mapper error"));

        WsResponse<ClientDTO> response = clientService.save(clientDTO);

        assertEquals("500", response.getCodeAndMessageBasicResponse().getCode());
    }

    // ========== update ==========

    @Test
    void testUpdate_success() {
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(passwordEncoder.matches("rawPass", "encodedPass")).thenReturn(true);
        when(passwordEncoder.encode("rawPass")).thenReturn("encodedPass");
        when(clientRepository.save(client)).thenReturn(client);
        when(clientMapper.toDto(client)).thenReturn(clientDTO);

        WsResponse<ClientDTO> response = clientService.update(1L, clientDTO);

        assertEquals("200", response.getCodeAndMessageBasicResponse().getCode());
    }

    @Test
    void testUpdate_notFound() {
        when(clientRepository.findById(1L)).thenReturn(Optional.empty());

        WsResponse<ClientDTO> response = clientService.update(1L, clientDTO);

        assertEquals("404", response.getCodeAndMessageBasicResponse().getCode());
    }

    @Test
    void testUpdate_invalidPassword() {
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(passwordEncoder.matches("rawPass", "encodedPass")).thenReturn(false);

        WsResponse<ClientDTO> response = clientService.update(1L, clientDTO);

        assertEquals("500", response.getCodeAndMessageBasicResponse().getCode());
    }

    @Test
    void testUpdate_exception() {
        when(clientRepository.findById(1L)).thenThrow(new RuntimeException("DB error"));

        WsResponse<ClientDTO> response = clientService.update(1L, clientDTO);

        assertEquals("500", response.getCodeAndMessageBasicResponse().getCode());
    }

    // ========== delete ==========

    @Test
    void testDelete_success() {
        when(clientRepository.existsById(1L)).thenReturn(true);

        WsResponse<String> response = clientService.delete(1L);

        assertEquals("200", response.getCodeAndMessageBasicResponse().getCode());
        verify(clientRepository).deleteById(1L);
    }

    @Test
    void testDelete_notFound() {
        when(clientRepository.existsById(1L)).thenReturn(false);

        WsResponse<String> response = clientService.delete(1L);

        assertEquals("404", response.getCodeAndMessageBasicResponse().getCode());
    }

    @Test
    void testDelete_exception() {
        when(clientRepository.existsById(1L)).thenThrow(new RuntimeException("DB error"));

        WsResponse<String> response = clientService.delete(1L);

        assertEquals("500", response.getCodeAndMessageBasicResponse().getCode());
    }
}

