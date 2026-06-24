package com.toyoda.parking.estapar.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class GarageConfigDTO(@JsonProperty("garage") val garage: List<SectorConfigDTO>,
                           @JsonProperty("spots")  val spots: List<SpotConfigDTO>)
