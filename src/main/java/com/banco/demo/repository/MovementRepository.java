/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.banco.demo.repository;

import com.banco.demo.model.Account;
import com.banco.demo.model.Movement;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 *
 * @author hernando.hernandez
 */
@Repository
public interface MovementRepository extends JpaRepository<Movement, Long> {

    List<Movement> findTop10ByAccount_AccountNumberOrderByDateDesc(Long accountNumber);

    @Query("""
    SELECT m 
    FROM Movement m
    JOIN m.account a
    JOIN a.client c
    WHERE c.clientId = :clientId
      AND m.date BETWEEN :fechaDesde AND :fechaHasta
    ORDER BY m.date
    """)
    List<Movement> findMovementsByClientAndDateRange(
            @Param("clientId") Long clientId,
            @Param("fechaDesde") LocalDateTime fechaDesde,
            @Param("fechaHasta") LocalDateTime fechaHasta
    );

    List<Movement> findByAccountAndDateBetween(Account account, LocalDateTime startDate, LocalDateTime endDate);

}
