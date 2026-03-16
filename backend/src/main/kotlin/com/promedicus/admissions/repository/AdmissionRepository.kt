package com.promedicus.admissions.repository

import com.promedicus.admissions.entity.Admission
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface AdmissionRepository : JpaRepository<Admission, UUID> {
    fun existsByExternalSystemId(externalSystemId: String): Boolean
}
