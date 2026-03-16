package com.promedicus.admissions.service

import com.promedicus.admissions.dto.*
import com.promedicus.admissions.entity.Admission
import com.promedicus.admissions.exception.AdmissionNotFoundException
import com.promedicus.admissions.exception.InvalidAdmissionTypeException
import com.promedicus.admissions.repository.AdmissionRepository
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
@Transactional
class AdmissionServiceImpl(
    private val admissionRepository: AdmissionRepository
) : AdmissionService {

    @Transactional(readOnly = true)
    override fun list(pageable: Pageable): PagedResponse<AdmissionResponse> {
        val page = admissionRepository.findAll(pageable)
        return PagedResponse(
            content = page.content.map { AdmissionResponse.from(it) },
            totalElements = page.totalElements,
            totalPages = page.totalPages,
            page = page.number,
            size = page.size
        )
    }

    override fun create(request: AdmissionRequest): AdmissionResponse {
        val admission = Admission(
            name = request.name,
            birthday = request.birthday,
            sex = request.sex,
            category = request.category
        )
        return AdmissionResponse.from(admissionRepository.save(admission))
    }

    override fun update(id: UUID, request: AdmissionUpdateRequest): AdmissionResponse {
        val admission = admissionRepository.findById(id)
            .orElseThrow { AdmissionNotFoundException(id) }

        if (admission.externalSystemId != null) {
            throw InvalidAdmissionTypeException(
                "Cannot update external admission (id=$id) via regular update endpoint"
            )
        }

        admission.name = request.name
        admission.birthday = request.birthday
        admission.sex = request.sex
        admission.category = request.category

        return AdmissionResponse.from(admissionRepository.save(admission))
    }

    override fun delete(id: UUID) {
        if (!admissionRepository.existsById(id)) {
            throw AdmissionNotFoundException(id)
        }
        admissionRepository.deleteById(id)
    }

    override fun createExternal(request: ExternalAdmissionRequest): AdmissionResponse {
        val admission = Admission(
            name = request.name,
            birthday = request.birthday,
            sex = request.sex,
            category = request.category,
            externalSystemId = request.externalSystemId
        )
        return AdmissionResponse.from(admissionRepository.save(admission))
    }

    override fun updateExternal(id: UUID, request: ExternalAdmissionUpdateRequest): AdmissionResponse {
        val admission = admissionRepository.findById(id)
            .orElseThrow { AdmissionNotFoundException(id) }

        if (admission.externalSystemId == null) {
            throw InvalidAdmissionTypeException(
                "Cannot update regular admission (id=$id) via external update endpoint"
            )
        }

        admission.name = request.name
        admission.birthday = request.birthday
        admission.sex = request.sex

        return AdmissionResponse.from(admissionRepository.save(admission))
    }
}
