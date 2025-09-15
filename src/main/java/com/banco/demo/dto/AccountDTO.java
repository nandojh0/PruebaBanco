/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.banco.demo.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *
 * @author hernando.hernandez
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AccountDTO {
     private Long accountNumber;
    private String accountType;
    private Double balance;
    private Boolean status;
    private Long clientId; // Mapea client.clientId
    private List<MovementDTO> movements; // Mapea movimientos

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("AccountDTO{");
        sb.append("accountNumber=<*").append(accountNumber).append("*>");
        sb.append(", accountType=").append(accountType);
        sb.append(", balance=").append(balance);
        sb.append(", status=").append(status);
        sb.append(", clientId=").append(clientId);
        sb.append(", movements=").append(movements);
        sb.append('}');
        return sb.toString();
    }
    
    
}