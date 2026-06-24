package com.toyoda.parking.estapar.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class SectorConfigDTO(@JsonProperty("sector")      val sector: String,
                           @JsonProperty("base_price")   val basePrice: Double,
                           @JsonProperty("max_capacity") val maxCapacity: Int)
