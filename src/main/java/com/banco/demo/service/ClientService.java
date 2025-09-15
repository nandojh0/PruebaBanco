/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */

package com.banco.demo.service;

import com.banco.demo.dto.ClientDTO;
import com.banco.demo.dto.WsResponse;
import java.util.List;

/**
 *
 * @author hernando.hernandez
 */
public interface ClientService {

    WsResponse<List<ClientDTO>> findAll();

    WsResponse<ClientDTO> findById(Long id);

    WsResponse<ClientDTO> save(ClientDTO clientDTO);

    WsResponse<ClientDTO> update(Long id, ClientDTO clientDTO);

    WsResponse<String> delete(Long id);
}
