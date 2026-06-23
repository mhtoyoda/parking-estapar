package com.toyoda.parking.estapar.service

import com.toyoda.parking.estapar.dto.response.RevenueResponseDTO
import com.toyoda.parking.estapar.repository.ParkingEventRepository
import org.springframework.stereotype.Service
import java.time.ZoneOffset
import java.time.ZonedDateTime

@Service
class RevenueService(private val eventRepository: ParkingEventRepository) {

    fun getRevenue(date: String, sector: String): RevenueResponseDTO {
        val total = eventRepository.sumAmountBySectorAndDate(sector, date) ?: 0.0

        return RevenueResponseDTO(
            amount    = total,
            currency  = "BRL",
            timestamp = ZonedDateTime.now(ZoneOffset.UTC).toString()
        )
    }
}