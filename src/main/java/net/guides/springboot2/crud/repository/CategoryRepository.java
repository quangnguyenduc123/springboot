/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.guides.springboot2.crud.repository;

import java.util.List;
import javax.transaction.Transactional;
import net.guides.springboot2.crud.model.Cart;
import net.guides.springboot2.crud.model.Category;
import net.guides.springboot2.crud.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Owner
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer>{
    @Query("FROM Category WHERE status=0")
    List<Category> listCategory();
    @Transactional
    @Modifying
    @Query("update Product  set status= 0 WHERE categoryid = ?1")
    void reSellCategory(Integer categoryId);
}
