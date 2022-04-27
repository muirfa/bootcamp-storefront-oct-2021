/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.g2academy.bootcamp.storefront.controller;

import co.g2academy.bootcamp.storefront.entity.Person;
import co.g2academy.bootcamp.storefront.repository.PersonRepository;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Asus
 */
@RestController
@RequestMapping("/api")
public class PersonController {
    @Autowired
    private PersonRepository personRepository;
    
    @PutMapping("/person/{id}")
    public ResponseEntity<String> update(@RequestBody Person person, @PathVariable Integer id){
        Optional<Person> personToUpdateOptional =  personRepository.findById(id);
        if (personToUpdateOptional.isPresent()) {
            Person personToUpdate = personToUpdateOptional.get();
            personToUpdate.setPassword(person.getPassword());
            personRepository.save(personToUpdate);
            return ResponseEntity.ok("ok");
        }
        return ResponseEntity.ok("user not found");
    }
    
    @DeleteMapping("/person/{id}")
    public ResponseEntity<String> delete(@PathVariable Integer id){
        Optional<Person> personToDeleteOptional = personRepository.findById(id);
        if (personToDeleteOptional.isPresent()) {
            personRepository.deleteById(id);
            return ResponseEntity.ok("ok");
        }
        return ResponseEntity.ok("user not found");
    }
    
    @DeleteMapping("/person/")
    public ResponseEntity<String> deleteAll(){
        personRepository.deleteAll();
        return ResponseEntity.ok("ok");
    }
    
}
