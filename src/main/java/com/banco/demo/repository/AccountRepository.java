/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.banco.demo.repository;

import com.banco.demo.model.Account;
import com.banco.demo.model.Client;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author hernando.hernandez
 */
@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    
    List<Account> findByClient(Client client);
    
}
