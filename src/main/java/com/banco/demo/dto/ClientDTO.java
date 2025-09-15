/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.banco.demo.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
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
@JsonIgnoreProperties(ignoreUnknown = false)
public class ClientDTO extends PersonDTO {
    private Long clientId;
    
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    private Boolean status;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ClientDTO{");
        sb.append("clientId=").append(clientId);
        sb.append(", status=").append(status);
        sb.append('}');
        return sb.toString();
    }
    
    
}
