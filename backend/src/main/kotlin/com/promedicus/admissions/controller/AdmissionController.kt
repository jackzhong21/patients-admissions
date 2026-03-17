package com.promedicus.admissions.controller

import com.promedicus.admissions.dto.AdmissionRequest
import com.promedicus.admissions.dto.AdmissionResponse
import com.promedicus.admissions.dto.AdmissionUpdateRequest
import com.promedicus.admissions.dto.ExternalAdmissionRequest
import com.promedicus.admissions.dto.ExternalAdmissionUpdateRequest
import com.promedicus.admissions.dto.PagedResponse
import com.promedicus.admissions.service.AdmissionService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/admissions")
@Tag(name = "Admissions", description = "Patient admission management")
class AdmissionController(private val admissionService: AdmissionService) {
    @GetMapping
    @Operation(summary = "List admissions with pagination")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "Paginated list of admissions"),
    )
    fun list(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
    ): ResponseEntity<PagedResponse<AdmissionResponse>> {
        val pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "dateOfAdmission"))
        return ResponseEntity.ok(admissionService.list(pageable))
    }

    @PostMapping
    @Operation(summary = "Create a regular admission")
    @ApiResponses(
        ApiResponse(responseCode = "201", description = "Admission created"),
        ApiResponse(responseCode = "400", description = "Validation error"),
    )
    fun create(
        @Valid @RequestBody request: AdmissionRequest,
    ): ResponseEntity<AdmissionResponse> {
        return ResponseEntity.status(HttpStatus.CREATED).body(admissionService.create(request))
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a regular admission")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "Admission updated"),
        ApiResponse(responseCode = "400", description = "Validation error"),
        ApiResponse(responseCode = "404", description = "Admission not found"),
        ApiResponse(responseCode = "409", description = "Admission type mismatch"),
    )
    fun update(
        @PathVariable id: UUID,
        @Valid @RequestBody request: AdmissionUpdateRequest,
    ): ResponseEntity<AdmissionResponse> {
        return ResponseEntity.ok(admissionService.update(id, request))
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an admission")
    @ApiResponses(
        ApiResponse(responseCode = "204", description = "Admission deleted"),
        ApiResponse(responseCode = "404", description = "Admission not found"),
    )
    fun delete(
        @PathVariable id: UUID,
    ): ResponseEntity<Void> {
        admissionService.delete(id)
        return ResponseEntity.noContent().build()
    }

    @PostMapping("/external")
    @Operation(summary = "Create an external admission")
    @ApiResponses(
        ApiResponse(responseCode = "201", description = "External admission created"),
        ApiResponse(responseCode = "400", description = "Validation error"),
    )
    fun createExternal(
        @Valid @RequestBody request: ExternalAdmissionRequest,
    ): ResponseEntity<AdmissionResponse> {
        return ResponseEntity.status(HttpStatus.CREATED).body(admissionService.createExternal(request))
    }

    @PutMapping("/external/{id}")
    @Operation(summary = "Update an external admission (name, birthday, sex only)")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "External admission updated"),
        ApiResponse(responseCode = "400", description = "Validation error"),
        ApiResponse(responseCode = "404", description = "Admission not found"),
        ApiResponse(responseCode = "409", description = "Admission type mismatch"),
    )
    fun updateExternal(
        @PathVariable id: UUID,
        @Valid @RequestBody request: ExternalAdmissionUpdateRequest,
    ): ResponseEntity<AdmissionResponse> {
        return ResponseEntity.ok(admissionService.updateExternal(id, request))
    }
}
