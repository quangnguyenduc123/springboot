/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.guides.springboot2.crud.repository;

import java.util.List;
import net.guides.springboot2.crud.model.Customer;
import net.guides.springboot2.crud.model.Order;
import net.guides.springboot2.crud.model.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Owner
 */
@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail,Integer>{
    @Query("FROM OrderDetail WHERE orderid=?1")
    List<OrderDetail> listOrderDetail(Integer orderid);
}
