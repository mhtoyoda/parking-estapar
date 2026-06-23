package com.toyoda.parking.estapar.controller

import com.toyoda.parking.estapar.dto.WebhookEventDTO
import com.toyoda.parking.estapar.service.ParkingService
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class WebhookController(private val parkingService: ParkingService) {
    private val log = LoggerFactory.getLogger(javaClass)

    @PostMapping("/webhook")
    fun handleEvent(@RequestBody event: WebhookEventDTO): ResponseEntity<Void> {
        log.info("Event received: ${event.eventType} — ${event.licensePlate}")

        when (event.eventType) {
            "ENTRY"  -> parkingService.handleEntry(event.licensePlate, event.entryTime!!)
            "PARKED" -> parkingService.handleParked(event.licensePlate, event.lat!!, event.lng!!)
            "EXIT"   -> parkingService.handleExit(event.licensePlate, event.exitTime!!)
            else     -> log.warn("Unknown event type: ${event.eventType}")
        }

        return ResponseEntity.ok().build()
    }

}