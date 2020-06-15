/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.guides.springboot2.crud.repository;

import java.util.List;
import net.guides.springboot2.crud.model.Comment;
import net.guides.springboot2.crud.model.Customer;
import net.guides.springboot2.crud.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 *
 * @author Owner
 */
public interface CommentRepository extends JpaRepository<Comment, Integer>{
   @Query("FROM Comment WHERE productid = ?1 ")
    List<Comment> ListComments(Integer id);
}
