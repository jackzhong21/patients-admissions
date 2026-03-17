package com.promedicus.admissions.service

import com.promedicus.admissions.dto.AdmissionRequest
import com.promedicus.admissions.dto.AdmissionResponse
import com.promedicus.admissions.dto.AdmissionUpdateRequest
import com.promedicus.admissions.dto.ExternalAdmissionRequest
import com.promedicus.admissions.dto.ExternalAdmissionUpdateRequest
import com.promedicus.admissions.dto.PagedResponse
import org.springframework.data.domain.Pageable
import java.util.UUID

interface AdmissionService {
    fun list(pageable: Pageable): PagedResponse<AdmissionResponse>

    fun create(request: AdmissionRequest): AdmissionResponse

    fun update(
        id: UUID,
        request: AdmissionUpdateRequest,
    ): AdmissionResponse

    fun delete(id: UUID)

    fun createExternal(request: ExternalAdmissionRequest): AdmissionResponse

    fun updateExternal(
        id: UUID,
        request: ExternalAdmissionUpdateRequest,
    ): AdmissionResponse
}
