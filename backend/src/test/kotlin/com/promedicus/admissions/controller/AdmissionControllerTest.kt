package com.promedicus.admissions.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.promedicus.admissions.TestcontainersConfig
import com.promedicus.admissions.entity.Admission
import com.promedicus.admissions.entity.Category
import com.promedicus.admissions.entity.Sex
import com.promedicus.admissions.repository.AdmissionRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDate

@SpringBootTest
@AutoConfigureMockMvc
class AdmissionControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var admissionRepository: AdmissionRepository

    companion object {
        @DynamicPropertySource
        @JvmStatic
        fun registerProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", TestcontainersConfig.postgres::getJdbcUrl)
            registry.add("spring.datasource.username", TestcontainersConfig.postgres::getUsername)
            registry.add("spring.datasource.password", TestcontainersConfig.postgres::getPassword)
        }
    }

    @BeforeEach
    fun cleanDatabase() {
        admissionRepository.deleteAll()
    }

    private fun savedRegular(
        name: String = "Jane Doe",
        birthday: LocalDate = LocalDate.of(1990, 5, 15),
        sex: Sex = Sex.FEMALE,
        category: Category = Category.INPATIENT,
    ): Admission =
        admissionRepository.save(
            Admission(name = name, birthday = birthday, sex = sex, category = category),
        )

    private fun savedExternal(
        name: String = "John External",
        birthday: LocalDate = LocalDate.of(1985, 3, 10),
        sex: Sex = Sex.MALE,
        category: Category = Category.OUTPATIENT,
        externalSystemId: String = "EXT-001",
    ): Admission =
        admissionRepository.save(
            Admission(
                name = name,
                birthday = birthday,
                sex = sex,
                category = category,
                externalSystemId = externalSystemId,
            ),
        )

    private fun json(obj: Any): String = objectMapper.writeValueAsString(obj)

    @Test
    fun `GET list returns 200 with paginated response`() {
        savedRegular(name = "Alice")
        savedRegular(name = "Bob")

        mockMvc.perform(get("/api/admissions?page=0&size=10"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.totalElements").value(2))
            .andExpect(jsonPath("$.content").isArray)
            .andExpect(jsonPath("$.page").value(0))
            .andExpect(jsonPath("$.size").value(10))
    }

    @Test
    fun `GET list returns empty content when no admissions exist`() {
        mockMvc.perform(get("/api/admissions"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.totalElements").value(0))
            .andExpect(jsonPath("$.content").isEmpty)
    }

    @Test
    fun `POST create returns 201 with saved admission`() {
        val body =
            mapOf(
                "name" to "Jane Doe",
                "birthday" to "1990-05-15",
                "sex" to "FEMALE",
                "category" to "INPATIENT",
            )

        mockMvc.perform(
            post("/api/admissions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(body)),
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.name").value("Jane Doe"))
            .andExpect(jsonPath("$.sex").value("FEMALE"))
            .andExpect(jsonPath("$.category").value("INPATIENT"))
            .andExpect(jsonPath("$.externalSystemId").doesNotExist())

        assertEquals(1, admissionRepository.count())
    }

    @Test
    fun `POST create with future birthday returns 400 with field error`() {
        val body =
            mapOf(
                "name" to "Jane Doe",
                "birthday" to "2099-01-01",
                "sex" to "FEMALE",
                "category" to "INPATIENT",
            )

        mockMvc.perform(
            post("/api/admissions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(body)),
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.errors[0].field").value("birthday"))

        assertEquals(0, admissionRepository.count())
    }

    @Test
    fun `POST create with missing name returns 400`() {
        val body = mapOf("birthday" to "1990-05-15", "sex" to "FEMALE", "category" to "INPATIENT")

        mockMvc.perform(
            post("/api/admissions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(body)),
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.status").value(400))
    }

    @Test
    fun `PUT update returns 200 with updated fields`() {
        val admission = savedRegular()
        val body =
            mapOf(
                "name" to "Updated Name",
                "birthday" to "2000-06-01",
                "sex" to "MALE",
                "category" to "EMERGENCY",
            )

        mockMvc.perform(
            put("/api/admissions/${admission.id}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(body)),
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.name").value("Updated Name"))
            .andExpect(jsonPath("$.sex").value("MALE"))
            .andExpect(jsonPath("$.category").value("EMERGENCY"))
    }

    @Test
    fun `PUT update returns 404 when admission does not exist`() {
        val body =
            mapOf(
                "name" to "X",
                "birthday" to "1990-01-01",
                "sex" to "FEMALE",
                "category" to "NORMAL",
            )

        mockMvc.perform(
            put("/api/admissions/00000000-0000-0000-0000-000000000000")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(body)),
        )
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.status").value(404))
    }

    @Test
    fun `PUT update on external admission returns 409`() {
        val external = savedExternal()
        val body =
            mapOf(
                "name" to "X",
                "birthday" to "1990-01-01",
                "sex" to "FEMALE",
                "category" to "NORMAL",
            )

        mockMvc.perform(
            put("/api/admissions/${external.id}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(body)),
        )
            .andExpect(status().isConflict)
            .andExpect(jsonPath("$.status").value(409))
    }

    @Test
    fun `DELETE returns 204 and removes admission`() {
        val admission = savedRegular()

        mockMvc.perform(delete("/api/admissions/${admission.id}"))
            .andExpect(status().isNoContent)

        assertEquals(0, admissionRepository.count())
    }

    @Test
    fun `DELETE returns 404 when admission does not exist`() {
        mockMvc.perform(delete("/api/admissions/00000000-0000-0000-0000-000000000000"))
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.status").value(404))
    }

    @Test
    fun `POST external returns 201 with externalSystemId set`() {
        val body =
            mapOf(
                "name" to "John External",
                "birthday" to "1985-03-10",
                "sex" to "MALE",
                "category" to "OUTPATIENT",
                "externalSystemId" to "EXT-100",
            )

        mockMvc.perform(
            post("/api/admissions/external")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(body)),
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.externalSystemId").value("EXT-100"))
            .andExpect(jsonPath("$.name").value("John External"))
    }

    @Test
    fun `PUT external updates only name, birthday, sex - category unchanged`() {
        val external =
            savedExternal(
                name = "Original",
                sex = Sex.MALE,
                category = Category.OUTPATIENT,
                externalSystemId = "EXT-200",
            )
        val body =
            mapOf(
                "name" to "Updated External",
                "birthday" to "1995-06-20",
                "sex" to "INTERSEX",
            )

        mockMvc.perform(
            put("/api/admissions/external/${external.id}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(body)),
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.name").value("Updated External"))
            .andExpect(jsonPath("$.sex").value("INTERSEX"))
            .andExpect(jsonPath("$.category").value("OUTPATIENT")) // unchanged
            .andExpect(jsonPath("$.externalSystemId").value("EXT-200")) // unchanged
    }

    @Test
    fun `PUT external on regular admission returns 409`() {
        val regular = savedRegular()
        val body = mapOf("name" to "X", "birthday" to "1990-01-01", "sex" to "FEMALE")

        mockMvc.perform(
            put("/api/admissions/external/${regular.id}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(body)),
        )
            .andExpect(status().isConflict)
            .andExpect(jsonPath("$.status").value(409))
    }
}
