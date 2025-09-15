/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.banco.demo.service;

/**
 *
 * @author hernando.hernandez
 */
import com.banco.demo.dto.AccountDTO;

import com.banco.demo.dto.WsResponse;


public interface AccountService {

    WsResponse<AccountDTO> findById(Long accountNumber);

    WsResponse<AccountDTO> save(AccountDTO accountDTO);

    WsResponse<AccountDTO> update(Long accountNumber, AccountDTO accountDTO);

    WsResponse<String> delete(Long accountNumber);
}


