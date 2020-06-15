/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.guides.springboot2.crud.repository;

import net.guides.springboot2.crud.model.Import;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Owner
 */
public interface ImportRepository extends JpaRepository<Import, Integer>{
    
}
