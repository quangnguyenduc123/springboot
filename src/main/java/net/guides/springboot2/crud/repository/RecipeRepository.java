/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.guides.springboot2.crud.repository;

import java.util.List;
import net.guides.springboot2.crud.model.Order;
import net.guides.springboot2.crud.model.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 *
 * @author Owner
 */
public interface RecipeRepository extends JpaRepository<Recipe, Integer> {
        List<Recipe> findByProduct(int productid);
        @Query("FROM Recipe WHERE ingredient = ?1 AND product = ?2")
        List<Recipe> findByIngredientAndProduct(int ingredient, int product);
                
       
}
