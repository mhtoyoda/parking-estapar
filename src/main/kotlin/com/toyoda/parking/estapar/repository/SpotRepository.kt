package com.toyoda.parking.estapar.repository

import com.toyoda.parking.estapar.model.Spot
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface SpotRepository : JpaRepository<Spot, Long> {
    fun countBySectorAndOccupied(sector: String, occupied: Boolean): Long
    fun findByLatAndLng(lat: Double, lng: Double): Optional<Spot>
    fun findByCurrentLicensePlate(licensePlate: String): Optional<Spot>
}