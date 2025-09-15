package com.banco.demo.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author oscar.morales
 */
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GenericCodeAndMessageResponse {
    
    /*
     * Documentación Seguridad: Las variables de esta clase no se usan de forma
     * explícita pero se deben mantener ya que conforman el modelo de la
     * respuesta generica de los servicios. Las variables se utilizan en los
     * métodos en los getters y setters provistos por la librería Lombok.
     */

    @JsonProperty("code")
    private String code;

    @JsonProperty("message")
    private String message;

    public GenericCodeAndMessageResponse(String code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String toString() {
        return "(" + this.code + ") " + this.message;
    }
    
    public String toJson() {
        return "{\"code\": \"" + this.code + "\",\"message\": \"" + this.message +"\"}";
    }

}