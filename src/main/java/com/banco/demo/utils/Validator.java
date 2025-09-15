/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.banco.demo.utils;

import com.banco.demo.dto.AccountDTO;
import com.banco.demo.dto.ClientDTO;
import com.banco.demo.dto.MovementDTO;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author hernando.hernandez
 */
@Service
public class Validator {

    @Autowired
    private LogManager logManager;

    @Autowired
    private RegexProperties regexProperties;

    /**
     * Valida cualquier DTO usando el mapa de regex. Convierte automáticamente
     * los tipos a String para aplicar regex.
     * @param request
     * @param regexMap
     * @param optionalFields
     * @return 
     */
    private String findInvalidFields(Object request, Map<String, String> regexMap, Set<String> optionalFields) {
        List<String> invalidFields = new ArrayList<>();

        for (Map.Entry<String, String> entry : regexMap.entrySet()) {
            String fieldName = entry.getKey();
            String regex = entry.getValue();

            try {
                // Detecta getter (getXxx o isXxx para boolean)
                Method getter;
                try {
                    getter = request.getClass().getMethod("get" + capitalize(fieldName));
                } catch (NoSuchMethodException e) {
                    getter = request.getClass().getMethod("is" + capitalize(fieldName));
                }

                // Invoca getter y convierte valor a String
                Object valueObj = getter.invoke(request);
                String value = valueObj != null ? valueObj.toString() : null;

                // Validación
                if (value == null || value.isEmpty()) {
                    if (!optionalFields.contains(fieldName)) {
                        invalidFields.add(fieldName); // obligatorio vacío
                    }
                } else if (!Pattern.matches(regex, value)) {
                    invalidFields.add(fieldName); // presente pero inválido
                }

            } catch (Exception e) {
                logManager.logTransactionError(
                        "Error validating field '" + fieldName + "' in request: " + request.getClass().getSimpleName(), e
                );
                invalidFields.add(fieldName);
            }
        }

        return String.join(", ", invalidFields);
    }

    private String capitalize(String str) {
        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }

    // ======== VALIDADORES POR DTO ========
    public String findInvalidFieldsClient(ClientDTO request) {
        // Campos opcionales heredados de PersonDTO
        Set<String> optionalFields = Set.of("address", "phone");

        // Validar campos de PersonDTO
        String invalidPersonFields = findInvalidFields(request, regexProperties.getPerson(), optionalFields);
        
        Set<String> optionalFields2 = Set.of("clientId");
        // Validar campos específicos de ClientDTO (todos obligatorios)
        String invalidClientFields = findInvalidFields(request, regexProperties.getClient(), optionalFields2);

        // Combina resultados
        String combined = Stream.of(invalidPersonFields, invalidClientFields)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.joining(", "));

        return combined;
    }
    
    public String findInvalidFieldsAccount(AccountDTO request) {
    // Todos los campos obligatorios
    Set<String> optionalFields = Set.of("accountNumber");
    return findInvalidFields(request, regexProperties.getAccount(), optionalFields);
}


    public String findInvalidFieldsMovement(MovementDTO request) {
        Set<String> optionalFields = Set.of("movementId","date","movementType","balance");
        return findInvalidFields(request, regexProperties.getMovement(), optionalFields);
    }
}
