/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.g2academy.bootcamp.storefront.controller;

import co.g2academy.bootcamp.storefront.entity.Product;
import co.g2academy.bootcamp.storefront.repository.ProductRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Asus
 */
@RestController
@RequestMapping("/api")
public class CategorySearchController {
    @Autowired
    private ProductRepository productRepository;
    
    @GetMapping("/search")
    public ResponseEntity<List<Product>> search(
            @RequestParam String q,
            @RequestParam Integer size,
            @RequestParam Integer page,
            @RequestParam String sort){
    
        Pageable pageable = composePageAble(page, size, sort);
        List<Product> products = productRepository.findByNameContains(q, pageable);
        return ResponseEntity.ok(products);
    }
    
    @GetMapping("/category")
    public ResponseEntity<List<Product>> category(
            @RequestParam String category,
            @RequestParam Integer size,
            @RequestParam Integer page,
            @RequestParam String sort){
    
        Pageable pageable = composePageAble(page, size, sort);
        List<Product> products = productRepository.findByCategory(category, pageable);
        return ResponseEntity.ok(products);
    }
    
    public Pageable composePageAble(Integer page, Integer size, String sort){
//        PRICE_DESC, PRICE _ASC, TITLE
        if ("PRICE_DESC".equals(sort)) {
            Sort sortByPriceDesc = Sort.by(Sort.Direction.DESC, "price");
            return PageRequest.of(page, size, sortByPriceDesc);
        }else if ("PRICE_ASC".equals(sort)) {
            Sort sortByPriceAsc = Sort.by("price");
            return PageRequest.of(page, size, sortByPriceAsc);
        }
        Sort sortByName = Sort.by("name");
        return PageRequest.of(page, size, sortByName);
    }
}
