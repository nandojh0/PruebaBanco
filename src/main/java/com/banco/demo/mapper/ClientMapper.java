/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.banco.demo.mapper;

import com.banco.demo.dto.ClientDTO;
import com.banco.demo.model.Client;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 *
 * @author hernando.hernandez
 */
@Mapper(componentModel = "spring", uses = {PersonMapper.class})
public interface ClientMapper {

    @Mapping(source = "person", target = ".") // mapea los campos de person directamente
    ClientDTO toDto(Client entity);

    @Mapping(target = "clientId", ignore = true)
    @Mapping(source = ".", target = "person") // construye Person desde los campos del DTO
    Client toEntity(ClientDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "clientId", ignore = true)
    @Mapping(source = ".", target = "person")
    void updateFromDto(ClientDTO dto, @MappingTarget Client entity);
}
