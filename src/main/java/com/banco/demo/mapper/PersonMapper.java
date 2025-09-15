/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.banco.demo.mapper;

import com.banco.demo.dto.PersonDTO;
import com.banco.demo.model.Person;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 *
 * @author hernando.hernandez
 */
@Mapper(componentModel = "spring")
public interface PersonMapper {

    PersonDTO toDto(Person entity);

    Person toEntity(PersonDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(PersonDTO dto, @MappingTarget Person entity);
}
