/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.g2academy.bootcamp.storefront.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 *
 * @author Asus
 */
@Entity
@Table(name = "T_CARTITEM")
public class CartItem implements Serializable{
    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    @Column(name =  "id")
    private Integer id;
    
    @Column(name =  "quantity", nullable = false)
    private Integer quantity;
    
    @Column(name =  "price", nullable = false)
    private Integer price;
    
    @ManyToOne
    @JoinColumn(name =  "cart_id", nullable = false)
    private Cart cart;
    
    @ManyToOne
    @JoinColumn(name =  "product_id", nullable = false)
    private Product product;

    public Cart getCart() {
        return cart;
    }

    public void setCart(Cart cart) {
        this.cart = cart;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }
}
