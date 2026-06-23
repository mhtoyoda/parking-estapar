package com.toyoda.parking.estapar.dto

data class GarageConfigDTO(val garage: List<SectorConfigDTO>,
                           val spots: List<SpotConfigDTO>)
