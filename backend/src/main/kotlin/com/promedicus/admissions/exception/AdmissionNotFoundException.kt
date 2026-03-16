package com.promedicus.admissions.exception

import java.util.UUID

class AdmissionNotFoundException(id: UUID) : RuntimeException("Admission not found with id: $id")
