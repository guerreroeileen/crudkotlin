package com.crudkotlin.crudkotlin.controller

import com.crudkotlin.crudkotlin.model.Person
import com.crudkotlin.crudkotlin.repository.PersonRepository
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.transaction.annotation.Transactional


@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class PersonControllerTest {
    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var personRepository: PersonRepository

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @BeforeEach
    fun setup() {
        personRepository.deleteAll()
    }

    @Test
    fun `GET all persons should return empty list`() {
        mockMvc.perform(get("/api/person/"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.size()").value(0))
    }

    @Test
    fun `POST create person should return created person`() {
        val person = Person(
            id = 1,
            name = "John",
            lastName = "Doe",
            age = 30,
            email = "john@example.com",
            address = "Calle 123"
        )

        mockMvc.perform(
            post("/api/person/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(person))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.name").value("John"))
    }

    @Test
    fun `GET person by ID should return person`() {
        val saved = personRepository.save(
            Person(1, "Ana", "Gomez", 25, "ana@example.com", "Calle 45")
        )

        mockMvc.perform(get("/api/person/${saved.id}"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.name").value("Ana"))
            .andExpect(jsonPath("$.email").value("ana@example.com"))
    }

    @Test
    fun `GET person by ID should return 404 when not found`() {
        mockMvc.perform(get("/api/person/999"))
            .andExpect(status().isNotFound)
    }

    @Test
    fun `PUT update person should modify and return person`() {
        val saved = personRepository.save(
            Person(1, "Luis", "Ramirez", 40, "luis@example.com", "Calle 12")
        )

        val updated = saved.copy(
            name = "Luis Updated",
            age = 41
        )

        mockMvc.perform(
            put("/api/person/${saved.id}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updated))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.name").value("Luis Updated"))
            .andExpect(jsonPath("$.age").value(41))
    }

    @Test
    fun `PUT update should return 404 when person does not exist`() {
        val person = Person(
            id = 1,
            name = "No existe",
            lastName = "Nadie",
            age = 99,
            email = "x@example.com",
            address = "Nowhere"
        )

        mockMvc.perform(
            put("/api/person/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(person))
        )
            .andExpect(status().isNotFound)
    }

    @Test
    fun `DELETE person should remove person and return 204`() {
        val saved = personRepository.save(
            Person(1, "Maria", "Lopez", 28, "maria@example.com", "Calle 90")
        )

        mockMvc.perform(delete("/api/person/${saved.id}"))
            .andExpect(status().isNoContent)

        assert(!personRepository.existsById(saved.id!!))
    }

}