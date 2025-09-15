/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.banco.demo.mapper;

import com.banco.demo.dto.AccountDTO;
import com.banco.demo.model.Account;
import org.mapstruct.BeanMapping;


/**
 *
 * @author hernando.hernandez
 */
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;


@Mapper(componentModel = "spring", uses = {MovementMapper.class})
public interface AccountMapper {

    // Mapeo explícito de relaciones
    @Mapping(source = "client.clientId", target = "clientId")
    @Mapping(source = "movements", target = "movements")
    AccountDTO toDto(Account entity);

    // Mapeo inverso: client y movements se resuelven en el servicio
    @Mapping(target = "accountNumber", ignore = true)
    @Mapping(target = "client", ignore = true)
    @Mapping(target = "movements", ignore = true)
    Account toEntity(AccountDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "accountNumber", ignore = true)
    @Mapping(target = "client", ignore = true)
    @Mapping(target = "movements", ignore = true)
    void updateFromDto(AccountDTO dto, @MappingTarget Account entity);
}


