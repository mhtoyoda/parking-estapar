package com.toyoda.parking.estapar.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class SectorConfigDTO(val sector: String,
                           val basePrice: Double,
                           @JsonProperty("max_capacity") val maxCapacity: Int)
