package com.toyoda.parking.estapar.model

import jakarta.persistence.Entity
import jakarta.persistence.Id

@Entity
data class Sector(@Id val sector: String,
                  val basePrice: Double,
                  val maxCapacity: Int,
                  val isOpen: Boolean = true)