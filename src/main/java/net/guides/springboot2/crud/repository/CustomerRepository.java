package net.guides.springboot2.crud.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import net.guides.springboot2.crud.model.Customer;
import org.springframework.data.jpa.repository.Query;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer>{
     @Query("FROM Customer WHERE username = ?1 AND password = ?2")
    List<Customer> Login(String username,String password);
    @Query("FROM Customer WHERE username = ?1 ")
    List<Customer> Loginfb(String username);
    @Query(value="Select u.Id,u.fullname,u.Email,u.phonenumber,"
            + "u.username,Case when o.amount IS NULL then 0 else o.amount End  "
            + "from Customers u LEFT JOIN(SELECT customerid ,"
            + " SUM(CASE WHEN processed =4 THEN total ELSE 0 END)as amount "
            + "from orders Where Month(orderdate)= Month(getdate())AND Year(orderdate)=Year(getdate()) "
            + " GROUP BY orders.customerid ) o ON o.customerid=u.Id ORDER BY o.amount DESC ",nativeQuery=true)
    List<Object>listCustomers();
}
