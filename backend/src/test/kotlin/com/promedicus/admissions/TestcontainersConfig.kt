package com.promedicus.admissions

import org.testcontainers.containers.PostgreSQLContainer

/**
 * Singleton PostgreSQL container shared across all integration tests.
 * Binds to host port 11111. Testcontainers' Ryuk reaper removes the
 * container automatically when the JVM exits.
 */
object TestcontainersConfig {

    val postgres: PostgreSQLContainer<*> = object : PostgreSQLContainer<Nothing>("postgres:16-alpine") {
        init {
            withDatabaseName("admissions")
            withUsername("admissions")
            withPassword("admissions")
            addFixedExposedPort(11111, 5432)
        }
    }.also { it.start() }
}
