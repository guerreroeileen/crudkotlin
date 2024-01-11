package com.crudkotlin.crudkotlin.controller

import com.crudkotlin.crudkotlin.model.Person
import com.crudkotlin.crudkotlin.repository.PersonRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/person")
class PersonController(private val personRepository: PersonRepository) {


    @GetMapping("/")
    fun getAllPerson(): ResponseEntity<List<Person>> {
        return ResponseEntity.ok(personRepository.findAll())
    }

    @PostMapping("/")
    fun createPerson(@RequestBody persona: Person): ResponseEntity<Person> {
        return ResponseEntity.status(HttpStatus.CREATED).body(personRepository.save(persona))
    }

    @GetMapping("/{id}")
    fun getPersonById(@PathVariable id: Long): ResponseEntity<Person> {
        val person = personRepository.findById(id)
        return person.map { ResponseEntity.ok(it) }.orElse(ResponseEntity.notFound().build())
    }

    @PutMapping("/{id}")
    fun updatePersona(@PathVariable id: Long, @RequestBody person: Person): ResponseEntity<Person> {
        if (!personRepository.existsById(id)) {
            return ResponseEntity.notFound().build()
        }
        val updatedPerson = personRepository.save(person.copy(id = id))
        return ResponseEntity.ok(updatedPerson)
    }

    @DeleteMapping("/{id}")
    fun deletePerson(@PathVariable id: Long): ResponseEntity<Void> {
        personRepository.deleteById(id)
        return ResponseEntity.noContent().build()
    }

}