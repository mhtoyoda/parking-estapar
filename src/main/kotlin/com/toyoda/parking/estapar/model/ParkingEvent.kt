package com.toyoda.parking.estapar.model

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import java.time.LocalDateTime

@Entity
data class ParkingEvent(@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
                        val id: Long = 0,
                        val licensePlate: String,
                        var sector: String? = null,
                        val entryTime: LocalDateTime,
                        var exitTime: LocalDateTime? = null,
                        var amountCharged: Double? = null,
                        val eventDate: String)
