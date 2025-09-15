/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */

package com.banco.demo.mapper;

import com.banco.demo.dto.MovementDTO;
import com.banco.demo.dto.MovementResponseDTO;
import com.banco.demo.model.Movement;
import java.time.format.DateTimeFormatter;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 *
 * @author hernando.hernandez
 */
@Mapper(componentModel = "spring")
public interface MovementMapper {

    // Mapeo explícito del número de cuenta
    @Mapping(source = "account.accountNumber", target = "accountNumber")
    MovementDTO toDto(Movement entity);

    // Mapeo inverso: account se resuelve en el servicio
    @Mapping(target = "movementId", ignore = true)
    @Mapping(target = "account", ignore = true)
    Movement toEntity(MovementDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "movementId", ignore = true)
    @Mapping(target = "account", ignore = true)
    void updateFromDto(MovementDTO dto, @MappingTarget Movement entity);
    
    public static MovementResponseDTO toResponseDTO(Movement movement) {
        return MovementResponseDTO.builder()
                .fecha(movement.getDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                .cliente(movement.getAccount().getClient().getPerson().getName())
                .numeroCuenta(movement.getAccount().getAccountNumber().toString())
                .tipo(movement.getAccount().getAccountType())
                .saldoInicial(movement.getInitialBalance())
                .estado(movement.getAccount().getStatus())
                .movimiento(movement.getValue())
                .saldoDisponible(movement.getBalance())
                .build();
    }
}
