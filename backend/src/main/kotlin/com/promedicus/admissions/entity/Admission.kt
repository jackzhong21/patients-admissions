package com.promedicus.admissions.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.Instant
import java.time.LocalDate
import java.util.UUID

@Entity
@Table(name = "admissions")
@EntityListeners(AuditingEntityListener::class)
data class Admission(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,
    @Column(nullable = false)
    var name: String,
    @Column(nullable = false)
    var birthday: LocalDate,
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var sex: Sex,
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var category: Category,
    @Column(name = "date_of_admission", nullable = false, updatable = false)
    val dateOfAdmission: Instant = Instant.now(),
    @Column(name = "external_system_id", updatable = false)
    val externalSystemId: String? = null,
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: Instant = Instant.now(),
    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    var updatedAt: Instant = Instant.now(),
)
