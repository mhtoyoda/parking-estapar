package com.toyoda.parking.estapar.repository

import com.toyoda.parking.estapar.model.ParkingEvent
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.Optional

interface ParkingEventRepository : JpaRepository<ParkingEvent, Long> {

    fun findTopByLicensePlateAndExitTimeIsNull(licensePlate: String): Optional<ParkingEvent>

    @Query("""
        SELECT SUM(e.amountCharged) FROM ParkingEvent e
        WHERE e.sector = :sector
          AND e.eventDate = :date
          AND e.exitTime IS NOT NULL
    """)
    fun sumAmountBySectorAndDate(
        @Param("sector") sector: String,
        @Param("date") date: String
    ): Double?
}