/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.guides.springboot2.crud.repository;

import java.util.List;
import net.guides.springboot2.crud.model.Admin;
import net.guides.springboot2.crud.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 *
 * @author Owner
 */
public interface AdminRepository extends JpaRepository<Admin, Integer>{
    @Query("FROM Admin WHERE username = ?1 AND password = ?2")
    List<Admin> Login(String username,String password);
}
