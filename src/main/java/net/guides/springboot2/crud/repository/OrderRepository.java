/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.guides.springboot2.crud.repository;

import java.util.List;
import net.guides.springboot2.crud.model.Customer;
import net.guides.springboot2.crud.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Owner
 */
@Repository
public interface OrderRepository  extends JpaRepository<Order, Integer>{
    @Query("FROM Order WHERE customerid = ?1 AND processed=0 ")
    List<Order> orders(Integer customerid);
    @Query("FROM Order WHERE customerid = ?1 AND processed=4")
    List<Order> finishedOrders(Integer customerid);
     @Query("FROM Order WHERE customerid = ?1 AND processed>0 AND processed<4")
    List<Order> processingOrders(Integer customerid);
    List<Order> findByProcessedOrderByOrderdateDesc(int processed);
    List<Order> findByProcessedBetweenOrderByOrderdateDesc(int a,int b);
    @Query("FROM Order WHERE Month(orderdate)= Month(getdate())AND Year(orderdate)=Year(getdate())  AND processed=4")
    List<Order> ordersInMonth();
}

