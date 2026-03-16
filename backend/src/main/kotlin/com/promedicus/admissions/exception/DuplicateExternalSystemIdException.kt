package com.promedicus.admissions.exception

class DuplicateExternalSystemIdException(externalSystemId: String) :
    RuntimeException("Admission with external system ID '$externalSystemId' already exists")
