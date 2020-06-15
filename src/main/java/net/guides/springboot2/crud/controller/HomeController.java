/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.guides.springboot2.crud.controller;

import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import net.guides.springboot2.crud.model.Category;
import net.guides.springboot2.crud.model.Comment;
import net.guides.springboot2.crud.model.Customer;
import net.guides.springboot2.crud.model.Order;
import net.guides.springboot2.crud.model.Product;
import net.guides.springboot2.crud.repository.CategoryRepository;
import net.guides.springboot2.crud.repository.CommentRepository;
import net.guides.springboot2.crud.repository.OrderRepository;
import net.guides.springboot2.crud.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Owner
 */
@RestController
@CrossOrigin(origins = "http://localhost:4200")

@RequestMapping("/home")
public class HomeController {
     @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired 
    private ProductRepository productRepository;
     @GetMapping("/categories")
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }
 
    @GetMapping("/products/{index}/{size}")
    public List<Product> getAllProducts(@PathVariable(value = "index") Integer index,@PathVariable(value = "size") Integer size) {
        Pageable pageable = PageRequest.of(index,size);
        Page<Product> products=productRepository.findAll(pageable);
        return products.getContent();
    }
    @GetMapping("/productsByCat/{catId}")
    public List<Product> getProductsByCat(@PathVariable(value = "catId") Integer catId) {
        return productRepository.FindCat(catId);
    }
    @GetMapping("/products/total/{size}")
    public Integer totalPage(@PathVariable(value = "size") Integer size) {
        Integer a=productRepository.findAll().size();
        if(a%size==0){
            return a/size-1;
        }
        else
         return a/size;
    }
     @GetMapping("/productsByName/{name}")
    public List<Product> getProductsByCat(@PathVariable(value = "name") String name) {
        return productRepository.findByNameIgnoreCaseContaining(name);
    }
    @GetMapping("/productsById/{id}")
    public Optional<Product> getProductsById(@PathVariable(value = "id") Integer id) {
        return productRepository.findById(id);
    }
    @GetMapping("/catDetail/{id}")
    public Optional<Category> catDetail(@PathVariable(value = "id") Integer id) {
        return categoryRepository.findById(id);
    }
     @GetMapping("/feedbacks/{id}")
    public List<Comment> getComments(@PathVariable(value = "id") Integer id) {
        return commentRepository.ListComments(id);
    }
     @GetMapping("/catById/{id}")
    public Optional<Category> getCatById(@PathVariable(value = "id") Integer id) {
        return categoryRepository.findById(id);
    }
}
