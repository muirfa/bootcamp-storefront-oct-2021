/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.g2academy.bootcamp.storefront.controller;

import co.g2academy.bootcamp.storefront.entity.Cart;
import co.g2academy.bootcamp.storefront.entity.CartItem;
import co.g2academy.bootcamp.storefront.entity.Person;
import co.g2academy.bootcamp.storefront.entity.Product;
import co.g2academy.bootcamp.storefront.kafka.KafkaListenerCallback;
import co.g2academy.bootcamp.storefront.kafka.KafkaOrderFullfilmentTopicConfig;
import co.g2academy.bootcamp.storefront.model.AddToCart;
import co.g2academy.bootcamp.storefront.model.OrderItemModel;
import co.g2academy.bootcamp.storefront.model.OrderModel;
import co.g2academy.bootcamp.storefront.repository.CartItemRepository;
import co.g2academy.bootcamp.storefront.repository.CartRepository;
import co.g2academy.bootcamp.storefront.repository.PersonRepository;
import co.g2academy.bootcamp.storefront.repository.ProductRepository;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Asus
 */
@RestController
@RequestMapping("/api")
public class CartAndCheckOutController {

    @Autowired
    private PersonRepository personRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private CartItemRepository cartItemRepository;
    @Autowired
    private KafkaTemplate<String, OrderModel> kafkaTemplate;

    private KafkaListenerCallback callback = new KafkaListenerCallback();

    @PostMapping("/add-to-cart")
    public ResponseEntity<String> addToCart(@RequestBody AddToCart addToCart, Principal pricipal) {
        Person person = personRepository.findByName(pricipal.getName());
        Optional<Product> productOptional = productRepository.findById(addToCart.getProductId());

//        stock check
        Iterable<Product> products = productRepository.findAll();
        for (Product product : products) {
            if (product.getId() == addToCart.getProductId()) {
                if (addToCart.getQuantity() > product.getStock()) {
                    return ResponseEntity.ok("product out of stock");
                }
            }
        }

        if (productOptional.isPresent()) {
            Product product = productOptional.get();
            Cart cart = cartRepository.findByStatusAndPerson("ACTIVE", person);
            if (cart == null) {
                cart = new Cart();
                cart.setPerson(person);
                cart.setStatus("ACTIVE");
                cart.setTransactionDate(new Date());
                cartRepository.save(cart);
                cart = cartRepository.findByStatusAndPerson("ACTIVE", person);
            }

//            avoid double cart item with same product, if same sent to add cart then sum a quantity
            boolean isProductExist = false;
            if (cart.getCartItem() != null) {
                for (CartItem cartItem : cart.getCartItem()) {
                    if (cartItem.getProduct().getId().equals(addToCart.getProductId())) {
                        cartItem.setQuantity(cartItem.getQuantity() + addToCart.getQuantity());
                        cartItemRepository.save(cartItem);
                        isProductExist = true;
                        break;

                    }
                }
            }
            if (!isProductExist) {
                CartItem cartItem = new CartItem();
                cartItem.setCart(cart);
                cartItem.setPrice(product.getPrice());
                cartItem.setQuantity(addToCart.getQuantity());
                cartItem.setProduct(product);
                cartItemRepository.save(cartItem);
            }
            return ResponseEntity.ok("ok");
        }
        return ResponseEntity.ok("Product not found");
    }

    @PostMapping("/checkout")
    public ResponseEntity<String> checkout(Principal principal) {
        Person person = personRepository.findByName(principal.getName());
        Cart cart = cartRepository.findByStatusAndPerson("ACTIVE", person);
        if (cart != null) {
            cart.setStatus("PROCESSED");
            cartRepository.save(cart);

            //send to Kafka
            sendMessageToKafka(convertCartToOrder(cart));
        }
        return ResponseEntity.ok("ok");
    }

    public void sendMessageToKafka(OrderModel order) {
        ListenableFuture<SendResult<String, OrderModel>> future
                = kafkaTemplate.send(KafkaOrderFullfilmentTopicConfig.ORDER_FULFILLMENT_KAFKA_TOPIC,order);
        future.addCallback(callback);
    }

    public OrderModel convertCartToOrder(Cart cart) {
        OrderModel orderModel = new OrderModel();
        orderModel.setCartId(cart.getId());
        orderModel.setOrderDate(cart.getTransactionDate());
        orderModel.setPersonId(cart.getPerson().getId());
        orderModel.setStatus(cart.getStatus());
        List<OrderItemModel> orderItems = new ArrayList<>();
        Integer totalPrice = 0;
        for (CartItem cartItem : cart.getCartItem()) {
            OrderItemModel orderItem = new OrderItemModel();
            orderItem.setPrice(cartItem.getPrice());
            orderItem.setProductId(cartItem.getProduct().getId());
            orderItem.setProductName(cartItem.getProduct().getName());
            orderItem.setQuantity(cartItem.getQuantity());
            totalPrice = totalPrice + (cartItem.getPrice() * cartItem.getQuantity());
            orderItems.add(orderItem);
            
//            decrement stock
            Iterable<CartItem> cartItemsTable = cartItemRepository.findAll();
            for (CartItem cartItemTable : cartItemsTable) {
                if (cartItemTable.getCart() == cart) {
                    Iterable<Product> products =  productRepository.findAll();
                    for (Product product : products) {
                        if (product == cartItemTable.getProduct()) {
                            product.setStock(product.getStock() - cartItem.getQuantity());
                            productRepository.save(product);
                        }
                    }
                }
            }
        }
        orderModel.setTotalPrice(totalPrice);
        orderModel.setOrderItems(orderItems);
        return orderModel;
    }

    @GetMapping("/cart")
    public ResponseEntity<Cart> getShoppingCart(Principal principal) {
        Person person = personRepository.findByName(principal.getName());
        Cart cart = cartRepository.findByStatusAndPerson("ACTIVE", person);
        if (cart != null) {
            cart.setPerson(null);
            for (CartItem cartItem : cart.getCartItem()) {
                cartItem.setCart(null);
                cartItem.getProduct().setPerson(null);
            }
        }
        return ResponseEntity.ok(cart);
    }
}
