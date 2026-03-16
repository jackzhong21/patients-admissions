package com.promedicus.admissions.service

import com.promedicus.admissions.dto.*
import org.springframework.data.domain.Pageable
import java.util.UUID

interface AdmissionService {
    fun list(pageable: Pageable): PagedResponse<AdmissionResponse>
    fun create(request: AdmissionRequest): AdmissionResponse
    fun update(id: UUID, request: AdmissionUpdateRequest): AdmissionResponse
    fun delete(id: UUID)
    fun createExternal(request: ExternalAdmissionRequest): AdmissionResponse
    fun updateExternal(id: UUID, request: ExternalAdmissionUpdateRequest): AdmissionResponse
}
