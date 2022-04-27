/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.g2academy.bootcamp.storefront.controller;

import co.g2academy.bootcamp.storefront.entity.Person;
import co.g2academy.bootcamp.storefront.entity.Product;
import co.g2academy.bootcamp.storefront.repository.PersonRepository;
import co.g2academy.bootcamp.storefront.repository.ProductCachedRepository;
import co.g2academy.bootcamp.storefront.repository.ProductRepository;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Asus
 */
@RestController
@RequestMapping("/api")
public class ProductController {
    
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private PersonRepository personRepository;
    @Autowired
    private ProductCachedRepository productCachedRepository;
    @GetMapping("/product/")
    public ResponseEntity<List<Product>>getProducts(Principal principal){
        Person person = personRepository.findByName(principal.getName());
        List<Product> products = productCachedRepository.findByPerson(person);
        for(Product product : products){
            product.setPerson(null);
        }
        return ResponseEntity.ok(products);
    }
    
    @GetMapping("/product/{id}")
    public ResponseEntity<Product> getProductByid(@PathVariable("id") Integer id, Principal principal){
        Person person = personRepository.findByName(principal.getName());
        Product product =  productCachedRepository.findByIdAndPerson(id, person);
        product.setPerson(null);
        return ResponseEntity.ok(product);
    }
    
    @PostMapping("/product")
    public ResponseEntity<String> save(@RequestBody Product product, Principal principal){
        Person person = personRepository.findByName(principal.getName());
        product.setPerson(person);
        productRepository.save(product);
        return ResponseEntity.ok("ok");
    }
    
    @PutMapping("/product/{id}")
    public ResponseEntity<String> update(@RequestBody Product product, @PathVariable Integer id, Principal principal){
        Person person = personRepository.findByName(principal.getName());
        Product productToUpdate = productRepository.findByIdAndPerson(id, person);
        
        if (productToUpdate != null) {
            productToUpdate.setCategory(product.getCategory());
            productToUpdate.setName(product.getName());
            productToUpdate.setStock(product.getStock());
            productToUpdate.setPrice(product.getPrice());
            productRepository.save(productToUpdate);
            return ResponseEntity.ok("ok");
        }
        return ResponseEntity.ok("product not found");
    }
    
    @DeleteMapping("/product/{id}")
    public ResponseEntity<String> delete(@PathVariable Integer id, Principal principal){
        Person person = personRepository.findByName(principal.getName());
        Product productToDelete = productRepository.findByIdAndPerson(id, person);
        
        if(productToDelete != null){
            productRepository.deleteById(id);
            return ResponseEntity.ok("ok");
        }
        return ResponseEntity.ok("product not found");
    }
    
    @DeleteMapping("/product/")
    public ResponseEntity<String> deletedAll(Principal principal){
        Person person = personRepository.findByName(principal.getName());
        List<Product> products =  productRepository.findByPerson(person);
        for(Product product : products){
            productRepository.delete(product);
        }
        return ResponseEntity.ok("ok");
    }
       
   
}
