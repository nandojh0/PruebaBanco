/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.banco.demo.dto;

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
public class PersonDTO {
    private Long personId;
    private String name;
    private String gender;
    private Integer age;
    private String identification;
    private String address;
    private String phone;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("PersonDTO{");
        sb.append("name=").append(name);
        sb.append(", gender=").append(gender);
        sb.append(", age=").append(age);
        sb.append(", identification=").append(identification);
        sb.append(", address=").append(address);
        sb.append(", phone=<*").append(phone).append("*>");
        sb.append('}');
        return sb.toString();
    }
    
    
}