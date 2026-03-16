package com.promedicus.admissions.service

import com.promedicus.admissions.TestcontainersConfig
import com.promedicus.admissions.dto.*
import com.promedicus.admissions.entity.Admission
import com.promedicus.admissions.entity.Category
import com.promedicus.admissions.entity.Sex
import com.promedicus.admissions.exception.AdmissionNotFoundException
import com.promedicus.admissions.exception.DuplicateExternalSystemIdException
import com.promedicus.admissions.exception.InvalidAdmissionTypeException
import com.promedicus.admissions.repository.AdmissionRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.PageRequest
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import java.time.LocalDate
import java.util.UUID

@SpringBootTest
class AdmissionServiceTest {

    @Autowired
    private lateinit var admissionService: AdmissionService

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

    // ── helpers ──────────────────────────────────────────────────────────────

    private fun savedRegular(
        name: String = "Jane Doe",
        birthday: LocalDate = LocalDate.of(1990, 5, 15),
        sex: Sex = Sex.FEMALE,
        category: Category = Category.INPATIENT
    ): Admission = admissionRepository.save(
        Admission(name = name, birthday = birthday, sex = sex, category = category)
    )

    private fun savedExternal(
        name: String = "John External",
        birthday: LocalDate = LocalDate.of(1985, 3, 10),
        sex: Sex = Sex.MALE,
        category: Category = Category.OUTPATIENT,
        externalSystemId: String = "EXT-001"
    ): Admission = admissionRepository.save(
        Admission(name = name, birthday = birthday, sex = sex, category = category,
            externalSystemId = externalSystemId)
    )

    // ── create ───────────────────────────────────────────────────────────────

    @Test
    fun `create - persists admission and returns mapped response`() {
        val request = AdmissionRequest(
            name = "Jane Doe",
            birthday = LocalDate.of(1990, 5, 15),
            sex = Sex.FEMALE,
            category = Category.INPATIENT
        )

        val response = admissionService.create(request)

        assertNotNull(response.id)
        assertEquals("Jane Doe", response.name)
        assertEquals(Sex.FEMALE, response.sex)
        assertEquals(Category.INPATIENT, response.category)
        assertNull(response.externalSystemId)
        assertEquals(1, admissionRepository.count())
    }

    // ── createExternal ───────────────────────────────────────────────────────

    @Test
    fun `createExternal - persists admission with externalSystemId`() {
        val request = ExternalAdmissionRequest(
            name = "John External",
            birthday = LocalDate.of(1985, 3, 10),
            sex = Sex.MALE,
            category = Category.OUTPATIENT,
            externalSystemId = "EXT-001"
        )

        val response = admissionService.createExternal(request)

        assertEquals("EXT-001", response.externalSystemId)
        assertEquals("John External", response.name)
    }

    @Test
    fun `createExternal - throws DuplicateExternalSystemIdException for duplicate externalSystemId`() {
        savedExternal(externalSystemId = "EXT-DUP")

        val request = ExternalAdmissionRequest(
            name = "Another",
            birthday = LocalDate.of(1990, 1, 1),
            sex = Sex.UNKNOWN,
            category = Category.NORMAL,
            externalSystemId = "EXT-DUP"
        )

        assertThrows<DuplicateExternalSystemIdException> {
            admissionService.createExternal(request)
        }
    }

    // ── update ───────────────────────────────────────────────────────────────

    @Test
    fun `update - modifies all four fields`() {
        val admission = savedRegular()

        val request = AdmissionUpdateRequest(
            name = "Updated Name",
            birthday = LocalDate.of(2000, 1, 1),
            sex = Sex.MALE,
            category = Category.EMERGENCY
        )

        val response = admissionService.update(admission.id!!, request)

        assertEquals("Updated Name", response.name)
        assertEquals(LocalDate.of(2000, 1, 1), response.birthday)
        assertEquals(Sex.MALE, response.sex)
        assertEquals(Category.EMERGENCY, response.category)
    }

    @Test
    fun `update - throws InvalidAdmissionTypeException when targeting an external admission`() {
        val external = savedExternal()

        val request = AdmissionUpdateRequest(
            name = "X", birthday = LocalDate.of(1990, 1, 1),
            sex = Sex.FEMALE, category = Category.NORMAL
        )

        assertThrows<InvalidAdmissionTypeException> {
            admissionService.update(external.id!!, request)
        }
    }

    @Test
    fun `update - throws AdmissionNotFoundException for unknown id`() {
        val request = AdmissionUpdateRequest(
            name = "X", birthday = LocalDate.of(1990, 1, 1),
            sex = Sex.FEMALE, category = Category.NORMAL
        )

        assertThrows<AdmissionNotFoundException> {
            admissionService.update(UUID.randomUUID(), request)
        }
    }

    // ── updateExternal ───────────────────────────────────────────────────────

    @Test
    fun `updateExternal - updates only name, birthday, and sex`() {
        val external = savedExternal(
            name = "Original", sex = Sex.MALE, category = Category.OUTPATIENT,
            externalSystemId = "EXT-002"
        )

        val request = ExternalAdmissionUpdateRequest(
            name = "Updated External",
            birthday = LocalDate.of(1995, 6, 20),
            sex = Sex.INTERSEX
        )

        val response = admissionService.updateExternal(external.id!!, request)

        assertEquals("Updated External", response.name)
        assertEquals(LocalDate.of(1995, 6, 20), response.birthday)
        assertEquals(Sex.INTERSEX, response.sex)
        // category and externalSystemId must remain unchanged
        assertEquals(Category.OUTPATIENT, response.category)
        assertEquals("EXT-002", response.externalSystemId)
    }

    @Test
    fun `updateExternal - throws InvalidAdmissionTypeException when targeting a regular admission`() {
        val regular = savedRegular()

        val request = ExternalAdmissionUpdateRequest(
            name = "X", birthday = LocalDate.of(1990, 1, 1), sex = Sex.FEMALE
        )

        assertThrows<InvalidAdmissionTypeException> {
            admissionService.updateExternal(regular.id!!, request)
        }
    }

    // ── delete ───────────────────────────────────────────────────────────────

    @Test
    fun `delete - removes admission from database`() {
        val admission = savedRegular()

        admissionService.delete(admission.id!!)

        assertEquals(0, admissionRepository.count())
    }

    @Test
    fun `delete - throws AdmissionNotFoundException for unknown id`() {
        assertThrows<AdmissionNotFoundException> {
            admissionService.delete(UUID.randomUUID())
        }
    }

    // ── list ─────────────────────────────────────────────────────────────────

    @Test
    fun `list - returns paginated results with correct metadata`() {
        repeat(5) { i -> savedRegular(name = "Patient $i") }

        val page1 = admissionService.list(PageRequest.of(0, 3))
        assertEquals(5, page1.totalElements)
        assertEquals(2, page1.totalPages)
        assertEquals(3, page1.content.size)
        assertEquals(0, page1.page)

        val page2 = admissionService.list(PageRequest.of(1, 3))
        assertEquals(2, page2.content.size)
        assertEquals(1, page2.page)
    }
}
