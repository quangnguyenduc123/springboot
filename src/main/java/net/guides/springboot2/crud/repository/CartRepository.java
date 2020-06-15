/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.guides.springboot2.crud.repository;

import java.util.List;
import javax.transaction.Transactional;
import net.guides.springboot2.crud.model.Cart;
import net.guides.springboot2.crud.model.Order;
import net.guides.springboot2.crud.model.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

/**
 *
 * @author Owner
 */
public interface CartRepository extends JpaRepository<Cart, Integer> {

    @Query("FROM Cart WHERE orderid=?1")
    List<Cart> listCarts(Integer orderid);

    @Query("FROM Cart WHERE orderid=?1 AND productid=?2")
    List<Cart> detailCarts(Integer orderid, Integer productid);

    @Modifying
    @Query("update Cart  set quantity= ?3 WHERE orderid=?1 AND productid=?2")
    void setCartInfoById(Integer orderid, Integer productid, Integer quantity);
     @Query("FROM Cart WHERE productid=?1")
    List<Cart> cartByProduct( Integer productid);
    @Transactional
    @Modifying
    @Query("delete from Cart Where productid=?1 And orderid=?2")
    void deleteCartBYPro(Integer productid,Integer orderid);
    List<Cart> findByProductid(Integer id);
}
