/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.banco.demo.utils;

import java.util.LinkedHashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 *
 * @author hernando.hernandez
 */
@Component
@ConfigurationProperties(prefix = "regex")
@Getter
@Setter
public class RegexProperties {
    private Map<String, String> person = new LinkedHashMap<>();
    private Map<String, String> client = new LinkedHashMap<>();
    private Map<String, String> account = new LinkedHashMap<>();
    private Map<String, String> movement = new LinkedHashMap<>();
}

