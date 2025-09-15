/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.banco.demo.service;

import com.banco.demo.dto.AccountDTO;
import com.banco.demo.dto.MovementResponseDTO;
import com.banco.demo.dto.WsResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 *
 * @author hernando.hernandez
 */
public interface ReportService {
    public WsResponse<List<MovementResponseDTO>> getReporte(Long clientId, LocalDateTime fechaDesde, LocalDateTime fechaHasta);
    
    public WsResponse<List<AccountDTO>> getAccountReport(Long clientId, LocalDate startDate, LocalDate endDate);
    
    
}
