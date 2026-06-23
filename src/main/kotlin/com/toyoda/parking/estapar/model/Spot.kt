package com.toyoda.parking.estapar.model

import jakarta.persistence.Entity
import jakarta.persistence.Id

@Entity
data class Spot(@Id val id: Long,
                val sector: String,
                val lat: Double,
                val lng: Double,
                val occupied: Boolean = false,
                val currentLicensePlate: String? = null)