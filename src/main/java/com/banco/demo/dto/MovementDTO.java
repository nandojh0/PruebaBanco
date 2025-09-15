/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.banco.demo.dto;

import java.time.LocalDate;
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
public class MovementDTO {
    private Long movementId;
    private LocalDate date;
    private String movementType;
    private Double value;
    private Double initialBalance;
    private Double balance;
    private Long accountNumber;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("MovementDTO{");
        sb.append("movementId=").append(movementId);
        sb.append(", date=").append(date);
        sb.append(", movementType=").append(movementType);
        sb.append(", value=").append(value);
        sb.append(", initialBalance=").append(initialBalance);
        sb.append(", balance=").append(balance);
        sb.append(", accountNumber=").append(accountNumber);
        sb.append('}');
        return sb.toString();
    }
    
    
}
