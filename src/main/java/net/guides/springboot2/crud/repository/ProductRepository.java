/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.guides.springboot2.crud.repository;

import java.util.List;
import javax.transaction.Transactional;
import net.guides.springboot2.crud.model.Customer;
import net.guides.springboot2.crud.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

/**
 *
 * @author Owner
 */
public interface ProductRepository extends JpaRepository<Product, Integer>{
    @Query("FROM Product WHERE categoryid = ?1 ")
    List<Product> FindCat(Integer categoryId);
    Page<Product> findAll(Pageable pageable);
    Page<Product>findByStatus(Integer status,Pageable pageable);
    List<Product> findByNameIgnoreCaseContaining(String name);
    Product findByName(String name);
    @Query("FROM Product WHERE status = 0 ")
    List<Product> listProducts();
    
}
