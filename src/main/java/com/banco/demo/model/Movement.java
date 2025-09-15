/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.banco.demo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *
 * @author hernando.hernandez
 */
// -------------------------
// Entity: Movement
// -------------------------
@Entity
@Table(name = "movements")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Movement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "movement_id")
    private Long movementId;

    @Column(nullable = false)
    private LocalDateTime date;

    @Column(name = "movement_type", nullable = false)
    private String movementType;

    @Column(nullable = false)
    private Double value;

    @Column(name = "initial_balance", nullable = false)
    private Double initialBalance;

    @Column(nullable = false)
    private Double balance;

    @ManyToOne
    @JoinColumn(name = "account_number", referencedColumnName = "account_number")
    private Account account;
}
