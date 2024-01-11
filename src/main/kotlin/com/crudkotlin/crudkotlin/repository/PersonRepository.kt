package com.crudkotlin.crudkotlin.repository

import com.crudkotlin.crudkotlin.model.Person
import org.springframework.data.jpa.repository.JpaRepository

interface PersonRepository: JpaRepository<Person, Long>