package com.promedicus.admissions.dto

import com.promedicus.admissions.entity.Admission
import com.promedicus.admissions.entity.Category
import com.promedicus.admissions.entity.Sex
import java.time.Instant
import java.time.LocalDate
import java.util.UUID

data class AdmissionResponse(
    val id: UUID,
    val name: String,
    val birthday: LocalDate,
    val sex: Sex,
    val category: Category,
    val dateOfAdmission: Instant,
    val externalSystemId: String?,
    val createdAt: Instant,
    val updatedAt: Instant
) {
    companion object {
        fun from(admission: Admission) = AdmissionResponse(
            id = admission.id!!,
            name = admission.name,
            birthday = admission.birthday,
            sex = admission.sex,
            category = admission.category,
            dateOfAdmission = admission.dateOfAdmission,
            externalSystemId = admission.externalSystemId,
            createdAt = admission.createdAt,
            updatedAt = admission.updatedAt
        )
    }
}
