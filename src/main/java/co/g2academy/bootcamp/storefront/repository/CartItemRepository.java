/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.g2academy.bootcamp.storefront.repository;

import co.g2academy.bootcamp.storefront.entity.CartItem;
import org.springframework.data.repository.CrudRepository;

/**
 *
 * @author Asus
 */
public interface CartItemRepository extends CrudRepository<CartItem, Integer>{
    
}
