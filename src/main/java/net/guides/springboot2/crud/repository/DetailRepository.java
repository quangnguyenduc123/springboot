/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.guides.springboot2.crud.repository;

import java.util.List;
import net.guides.springboot2.crud.model.Customer;
import net.guides.springboot2.crud.model.Detail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 *
 * @author Owner
 */
public interface DetailRepository extends JpaRepository<Detail, Integer>{
    @Query("FROM Detail WHERE importid = ?1")
    List<Detail> listDetails(Integer importid);
}
