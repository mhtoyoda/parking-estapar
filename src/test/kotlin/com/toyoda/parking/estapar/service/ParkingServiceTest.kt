package com.toyoda.parking.estapar.service

import com.toyoda.parking.estapar.model.ParkingEvent
import com.toyoda.parking.estapar.model.Sector
import com.toyoda.parking.estapar.model.Spot
import com.toyoda.parking.estapar.repository.ParkingEventRepository
import com.toyoda.parking.estapar.repository.SectorRepository
import com.toyoda.parking.estapar.repository.SpotRepository
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.time.LocalDateTime
import java.util.Optional

@ExtendWith(MockitoExtension::class)
class ParkingServiceTest {

    @Mock private lateinit var spotRepository: SpotRepository
    @Mock private lateinit var sectorRepository: SectorRepository
    @Mock private lateinit var eventRepository: ParkingEventRepository
    @Mock private lateinit var pricingService: PricingService
    @InjectMocks private lateinit var parkingService: ParkingService

    private val licensePlate = "ABC-1234"
    private val entryTime = LocalDateTime.of(2024, 1, 15, 10, 0)
    private val exitTime = LocalDateTime.of(2024, 1, 15, 12, 0)

    private val sector = Sector(sector = "A", basePrice = 10.0, maxCapacity = 10)
    private val spot = Spot(id = 1L, sector = "A", lat = -23.5, lng = -46.6)
    private val openEvent = ParkingEvent(
        licensePlate = licensePlate,
        sector = "A",
        entryTime = entryTime,
        eventDate = entryTime.toLocalDate().toString()
    )

    // --- handleEntry ---

    @Test
    fun `handleEntry throws IllegalStateException when all sectors are at full capacity`() {
        whenever(sectorRepository.findAll()).thenReturn(mutableListOf(sector))
        whenever(spotRepository.countBySectorAndOccupied("A", true)).thenReturn(10L)

        assertThatThrownBy { parkingService.handleEntry(licensePlate, entryTime) }
            .isInstanceOf(IllegalStateException::class.java)
            .hasMessageContaining("Garage is full")

        verify(eventRepository, never()).save(any())
    }

    @Test
    fun `handleEntry saves a new ParkingEvent when garage has available capacity`() {
        whenever(sectorRepository.findAll()).thenReturn(mutableListOf(sector))
        whenever(spotRepository.countBySectorAndOccupied("A", true)).thenReturn(5L)

        parkingService.handleEntry(licensePlate, entryTime)

        verify(eventRepository).save(any())
    }

    // --- handleParked ---

    @Test
    fun `handleParked throws NoSuchElementException when spot coordinates are not found`() {
        whenever(spotRepository.findByLatAndLng(-23.5, -46.6)).thenReturn(Optional.empty())

        assertThatThrownBy { parkingService.handleParked(licensePlate, -23.5, -46.6) }
            .isInstanceOf(NoSuchElementException::class.java)
            .hasMessageContaining("Spot not found")
    }

    @Test
    fun `handleParked throws NoSuchElementException when vehicle has no open entry event`() {
        whenever(spotRepository.findByLatAndLng(-23.5, -46.6)).thenReturn(Optional.of(spot))
        whenever(eventRepository.findTopByLicensePlateAndExitTimeIsNull(licensePlate))
            .thenReturn(Optional.empty())

        assertThatThrownBy { parkingService.handleParked(licensePlate, -23.5, -46.6) }
            .isInstanceOf(NoSuchElementException::class.java)
            .hasMessageContaining("No open entry event found")
    }

    @Test
    fun `handleParked marks spot as occupied and links the event to the sector`() {
        whenever(spotRepository.findByLatAndLng(-23.5, -46.6)).thenReturn(Optional.of(spot))
        whenever(eventRepository.findTopByLicensePlateAndExitTimeIsNull(licensePlate))
            .thenReturn(Optional.of(openEvent))

        parkingService.handleParked(licensePlate, -23.5, -46.6)

        verify(spotRepository).save(any())
        verify(eventRepository).save(any())
    }

    // --- handleExit ---

    @Test
    fun `handleExit throws NoSuchElementException when no active event exists for vehicle`() {
        whenever(eventRepository.findTopByLicensePlateAndExitTimeIsNull(licensePlate))
            .thenReturn(Optional.empty())

        assertThatThrownBy { parkingService.handleExit(licensePlate, exitTime) }
            .isInstanceOf(NoSuchElementException::class.java)
            .hasMessageContaining("No active event found")
    }

    @Test
    fun `handleExit calculates charge closes the event and frees the occupied spot`() {
        whenever(eventRepository.findTopByLicensePlateAndExitTimeIsNull(licensePlate))
            .thenReturn(Optional.of(openEvent))
        whenever(pricingService.calculatePrice("A", entryTime, exitTime)).thenReturn(20.0)
        whenever(spotRepository.findByCurrentLicensePlate(licensePlate))
            .thenReturn(Optional.of(spot))

        parkingService.handleExit(licensePlate, exitTime)

        verify(eventRepository).save(any())
        verify(spotRepository).save(any())
    }
}
