package com.toyoda.parking.estapar.service

import com.toyoda.parking.estapar.model.ParkingEvent
import com.toyoda.parking.estapar.repository.ParkingEventRepository
import com.toyoda.parking.estapar.repository.SectorRepository
import com.toyoda.parking.estapar.repository.SpotRepository
import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
@Transactional
class ParkingService(
    private val spotRepository: SpotRepository,
    private val sectorRepository: SectorRepository,
    private val eventRepository: ParkingEventRepository,
    private val pricingService: PricingService
) {
    private val log = LoggerFactory.getLogger(javaClass)

    fun handleEntry(licensePlate: String, entryTime: LocalDateTime) {
        val isFull = sectorRepository.findAll().all { sector ->
            spotRepository.countBySectorAndOccupied(sector.sector, true) >= sector.maxCapacity
        }
        check(!isFull) { "Garage is full, entry blocked for $licensePlate" }

        ParkingEvent(
            licensePlate = licensePlate,
            entryTime = entryTime,
            eventDate = entryTime.toLocalDate().toString()
        ).let { eventRepository.save(it) }

        log.info("Entry recorded: $licensePlate at $entryTime")
    }

    fun handleParked(licensePlate: String, lat: Double, lng: Double) {
        val spot = spotRepository.findByLatAndLng(lat, lng)
            .orElseThrow { NoSuchElementException("Spot not found: lat=$lat, lng=$lng") }

        spotRepository.save(spot.copy(occupied = true, currentLicensePlate = licensePlate))

        val event = eventRepository.findTopByLicensePlateAndExitTimeIsNull(licensePlate)
            .orElseThrow { NoSuchElementException("No open entry event found for $licensePlate") }

        eventRepository.save(event.copy(sector = spot.sector))
        log.info("$licensePlate parked at spot ${spot.id} (sector ${spot.sector})")
    }

    fun handleExit(licensePlate: String, exitTime: LocalDateTime) {
        val event = eventRepository.findTopByLicensePlateAndExitTimeIsNull(licensePlate)
            .orElseThrow { NoSuchElementException("No active event found for $licensePlate") }

        val amount = pricingService.calculatePrice(
            sector = event.sector!!,
            entry  = event.entryTime,
            exit   = exitTime
        )

        eventRepository.save(event.copy(exitTime = exitTime, amountCharged = amount))

        spotRepository.findByCurrentLicensePlate(licensePlate).ifPresent { spot ->
            spotRepository.save(spot.copy(occupied = false, currentLicensePlate = null))
        }

        log.info("Exit: $licensePlate — charged: ${"%.2f".format(amount)} BRL")
    }
}