package com.toyoda.parking.estapar.dto.request

import com.fasterxml.jackson.annotation.JsonProperty

data class RevenueRequestDTO(@JsonProperty("date")   val date: String,
                             @JsonProperty("sector") val sector: String)