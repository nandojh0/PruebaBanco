/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.banco.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *
 * @author hernando.hernandez
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovementResponseDTO {
    private String fecha;
    private String cliente;
    private String numeroCuenta;
    private String tipo;
    private Double saldoInicial;
    private Boolean estado;
    private Double movimiento;
    private Double saldoDisponible;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("MovementResponseDTO{");
        sb.append("fecha=").append(fecha);
        sb.append(", cliente=").append(cliente);
        sb.append(", numeroCuenta=<*").append(numeroCuenta).append("*>");
        sb.append(", tipo=").append(tipo);
        sb.append(", saldoInicial=").append(saldoInicial);
        sb.append(", estado=").append(estado);
        sb.append(", movimiento=").append(movimiento);
        sb.append(", saldoDisponible=").append(saldoDisponible);
        sb.append('}');
        return sb.toString();
    }
    
    
}
