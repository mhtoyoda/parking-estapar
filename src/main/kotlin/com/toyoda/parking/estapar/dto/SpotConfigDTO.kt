package com.toyoda.parking.estapar.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class SpotConfigDTO(@JsonProperty("id")     val id: Long,
                         @JsonProperty("sector") val sector: String,
                         @JsonProperty("lat")    val lat: Double,
                         @JsonProperty("lng")    val lng: Double)
