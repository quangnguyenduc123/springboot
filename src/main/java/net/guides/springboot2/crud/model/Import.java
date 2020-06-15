/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.guides.springboot2.crud.model;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import org.springframework.format.annotation.DateTimeFormat;
/**
 *
 * @author Owner
 */
@Entity
@Table(name = "Imports")
public class Import {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    @Column(name = "total", nullable = false)
    private Integer total;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss ")
    @Column(name = "importdate", nullable = false)
    private Date importdate;
    @Column(name = "admin", nullable = false)
    private Integer admin;
    public Import() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    

    public Date getImportdate() {
        return importdate;
    }

    public void setImportdate(Date importdate) {
        this.importdate = importdate;
    }

    public Integer getAdmin() {
        return admin;
    }

    public void setAdmin(Integer admin) {
        this.admin = admin;
    }
     
}
