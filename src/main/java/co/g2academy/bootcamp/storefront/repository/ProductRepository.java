/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.g2academy.bootcamp.storefront.repository;

import co.g2academy.bootcamp.storefront.entity.Person;
import co.g2academy.bootcamp.storefront.entity.Product;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Asus
 */
@Repository
public interface ProductRepository extends PagingAndSortingRepository<Product, Integer>{
    public List<Product> findByPerson(Person person);
    public Product findByIdAndPerson(Integer id, Person person);
    List<Product> findByNameContains(String name, Pageable pageable);
    List<Product> findByCategory(String category, Pageable pageable);
}
