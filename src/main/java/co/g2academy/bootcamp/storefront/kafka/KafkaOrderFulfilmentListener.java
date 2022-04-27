/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.g2academy.bootcamp.storefront.kafka;


import co.g2academy.bootcamp.storefront.entity.Cart;
import co.g2academy.bootcamp.storefront.entity.CartItem;
import co.g2academy.bootcamp.storefront.model.OrderItemModel;
import co.g2academy.bootcamp.storefront.model.OrderModel;
import co.g2academy.bootcamp.storefront.model.OrderStatus;
import co.g2academy.bootcamp.storefront.repository.CartItemRepository;
import co.g2academy.bootcamp.storefront.repository.CartRepository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

/**
 *
 * @author Asus
 */
@Component
public class KafkaOrderFulfilmentListener {
    @Autowired
    private CartRepository cartRepository;
    
    @Autowired CartItemRepository cartItemRepository;
    
    @KafkaListener(topics = "order-fulfillment-delivered", groupId = "foo", containerFactory = "kafkaListenerFactory")
    public void listenerOrderFulfillmentTopic(@Payload OrderStatus orderStatus){
        System.out.println("update processed to delivered (Status)");;
                
    }
    
    public Cart convertOrderModelToOrder(OrderStatus orderStatus){
        System.out.println(orderStatus.getCartId());
        System.out.println(orderStatus.getClass());
        System.out.println(orderStatus.getOrderId());
        System.out.println(orderStatus.getStatus());
        Cart cart = new Cart();
        cart.setCartItem(null);
        
        cart.setId(orderStatus.getCartId());
        cart.setPerson(null);
        cart.setStatus(orderStatus.getStatus());
        cart.setTransactionDate(null);
        List<CartItem> cartItems = new ArrayList<>();
        
        return cart;
    }
}
