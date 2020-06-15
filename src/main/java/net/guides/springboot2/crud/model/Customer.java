package net.guides.springboot2.crud.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "Customers")
public class Customer {
                        
	private Integer id;
	private String fullname;
	private String password;
	private String phonenumber;
        private Integer status;
        private String email;
        private String username;
        private String address;
	public Customer() {
		
	}

    public Customer(Integer id, String fullname, String password, String phonenumber, Integer status, String email, String username, String address) {
        this.id = id;
        this.fullname = fullname;
        this.password = password;
        this.phonenumber = phonenumber;
        this.status = status;
        this.email = email;
        this.username = username;
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

   
       
    
        
    
    
    @Column(name = "fullname", nullable = false)
    public String getFullname() {
        return fullname;
    }
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }
    @Column(name = "password", nullable = false)
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    @Column(name = "phonenumber", nullable = false)
    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }
    @Column(name = "status", nullable = false)
    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
    @Column(name = "email", nullable = false)
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
     @Column(name = "username", nullable = false)
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
	
    
	
	
}
