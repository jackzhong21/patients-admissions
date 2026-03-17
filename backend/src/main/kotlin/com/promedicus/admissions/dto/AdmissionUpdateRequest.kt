package com.promedicus.admissions.dto

import com.promedicus.admissions.entity.Category
import com.promedicus.admissions.entity.Sex
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.PastOrPresent
import java.time.LocalDate

data class AdmissionUpdateRequest(
    @field:NotBlank(message = "Name is required")
    val name: String,
    @field:NotNull(message = "Birthday is required")
    @field:PastOrPresent(message = "Birthday cannot be in the future")
    val birthday: LocalDate,
    @field:NotNull(message = "Sex is required")
    val sex: Sex,
    @field:NotNull(message = "Category is required")
    val category: Category,
)
