package com.toyoda.parking.estapar.service

import com.toyoda.parking.estapar.repository.SectorRepository
import com.toyoda.parking.estapar.repository.SpotRepository
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import kotlin.math.ceil

@Service
class PricingService(
    private val spotRepository: SpotRepository,
    private val sectorRepository: SectorRepository
) {
    fun calculatePrice(sector: String, entry: LocalDateTime, exit: LocalDateTime): Double {
        val minutes = ChronoUnit.MINUTES.between(entry, exit)
        if (minutes <= 30) return 0.0

        val basePrice = sectorRepository.findById(sector)
            .orElseThrow { NoSuchElementException("Sector not found: $sector") }
            .basePrice

        val dynamicPrice = applyDynamicPricing(sector, basePrice)
        val hours = ceil(minutes / 60.0).toLong()

        return hours * dynamicPrice
    }

    private fun applyDynamicPricing(sector: String, basePrice: Double): Double {
        val maxCapacity = sectorRepository.findById(sector).orElseThrow().maxCapacity
        val occupied = spotRepository.countBySectorAndOccupied(sector, true)
        val occupancy = occupied.toDouble() / maxCapacity

        return when {
            occupancy < 0.25 -> basePrice * 0.90  // under 25% — 10% discount
            occupancy < 0.50 -> basePrice          // under 50% — no change
            occupancy < 0.75 -> basePrice * 1.10   // under 75% — 10% surcharge
            else             -> basePrice * 1.25   // up to 100% — 25% surcharge
        }
    }
}