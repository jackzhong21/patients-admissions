package com.promedicus.admissions.exception

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

data class FieldErrorDetail(val field: String, val message: String)

data class ErrorResponse(
    val message: String,
    val errors: List<FieldErrorDetail> = emptyList(),
    val status: Int
)

@RestControllerAdvice
class GlobalExceptionHandler {

    private val log = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    @ExceptionHandler(AdmissionNotFoundException::class)
    fun handleNotFound(ex: AdmissionNotFoundException): ResponseEntity<ErrorResponse> {
        log.warn("Admission not found: {}", ex.message)
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ErrorResponse(message = ex.message ?: "Not found", status = 404))
    }

    @ExceptionHandler(InvalidAdmissionTypeException::class)
    fun handleInvalidType(ex: InvalidAdmissionTypeException): ResponseEntity<ErrorResponse> {
        log.warn("Invalid admission type: {}", ex.message)
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(ErrorResponse(message = ex.message ?: "Invalid admission type", status = 409))
    }

    @ExceptionHandler(DuplicateExternalSystemIdException::class)
    fun handleDuplicate(ex: DuplicateExternalSystemIdException): ResponseEntity<ErrorResponse> {
        log.warn("Duplicate external system ID: {}", ex.message)
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(ErrorResponse(message = ex.message ?: "Duplicate external system ID", status = 409))
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleUnreadable(ex: HttpMessageNotReadableException): ResponseEntity<ErrorResponse> {
        log.warn("Malformed or unreadable request body: {}", ex.message)
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ErrorResponse(message = "Malformed or missing request body", status = 400))
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidation(ex: MethodArgumentNotValidException): ResponseEntity<ErrorResponse> {
        val errors = ex.bindingResult.allErrors.map { error ->
            val field = (error as? FieldError)?.field ?: "unknown"
            val message = error.defaultMessage ?: "Invalid value"
            FieldErrorDetail(field = field, message = message)
        }
        log.warn("Validation failed: {}", errors)
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ErrorResponse(message = "Validation failed", errors = errors, status = 400))
    }

    @ExceptionHandler(Exception::class)
    fun handleGeneric(ex: Exception): ResponseEntity<ErrorResponse> {
        log.error("Unexpected error", ex)
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ErrorResponse(message = "Internal server error", status = 500))
    }
}
