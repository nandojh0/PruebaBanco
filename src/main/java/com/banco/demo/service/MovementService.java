/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.banco.demo.service;

import com.banco.demo.dto.MovementDTO;
import com.banco.demo.dto.WsResponse;

/**
 *
 * @author hernando.hernandez
 */
public interface MovementService {

    WsResponse<MovementDTO> addMovement(MovementDTO movementDTO);

}
