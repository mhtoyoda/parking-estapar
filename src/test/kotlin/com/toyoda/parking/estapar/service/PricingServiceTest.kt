package com.toyoda.parking.estapar.service

import com.toyoda.parking.estapar.model.Sector
import com.toyoda.parking.estapar.repository.SectorRepository
import com.toyoda.parking.estapar.repository.SpotRepository
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.whenever
import java.time.LocalDateTime
import java.util.Optional

@ExtendWith(MockitoExtension::class)
class PricingServiceTest {

    @Mock private lateinit var spotRepository: SpotRepository
    @Mock private lateinit var sectorRepository: SectorRepository
    @InjectMocks private lateinit var pricingService: PricingService

    private val sectorCode = "A"
    private val sector = Sector(sector = sectorCode, basePrice = 10.0, maxCapacity = 10)
    private val entry = LocalDateTime.of(2024, 1, 15, 10, 0)

    @Test
    fun `calculatePrice returns zero when stay is thirty minutes or less`() {
        val price = pricingService.calculatePrice(sectorCode, entry, entry.plusMinutes(30))

        assertThat(price).isEqualTo(0.0)
    }

    @Test
    fun `calculatePrice throws NoSuchElementException when sector does not exist`() {
        whenever(sectorRepository.findById("X")).thenReturn(Optional.empty())

        assertThatThrownBy { pricingService.calculatePrice("X", entry, entry.plusHours(2)) }
            .isInstanceOf(NoSuchElementException::class.java)
    }

    @Test
    fun `calculatePrice applies ten percent discount when occupancy is below twenty-five percent`() {
        whenever(sectorRepository.findById(sectorCode)).thenReturn(Optional.of(sector))
        whenever(spotRepository.countBySectorAndOccupied(sectorCode, true)).thenReturn(2L) // 20%

        val price = pricingService.calculatePrice(sectorCode, entry, entry.plusHours(1))

        assertThat(price).isEqualTo(9.0) // 10.0 * 0.90 * 1h
    }

    @Test
    fun `calculatePrice applies no change when occupancy is between twenty-five and fifty percent`() {
        whenever(sectorRepository.findById(sectorCode)).thenReturn(Optional.of(sector))
        whenever(spotRepository.countBySectorAndOccupied(sectorCode, true)).thenReturn(4L) // 40%

        val price = pricingService.calculatePrice(sectorCode, entry, entry.plusHours(1))

        assertThat(price).isEqualTo(10.0)
    }

    @Test
    fun `calculatePrice applies ten percent surcharge when occupancy is between fifty and seventy-five percent`() {
        whenever(sectorRepository.findById(sectorCode)).thenReturn(Optional.of(sector))
        whenever(spotRepository.countBySectorAndOccupied(sectorCode, true)).thenReturn(6L) // 60%

        val price = pricingService.calculatePrice(sectorCode, entry, entry.plusHours(1))

        assertThat(price).isEqualTo(11.0) // 10.0 * 1.10 * 1h
    }

    @Test
    fun `calculatePrice applies twenty-five percent surcharge when occupancy is seventy-five percent or above`() {
        whenever(sectorRepository.findById(sectorCode)).thenReturn(Optional.of(sector))
        whenever(spotRepository.countBySectorAndOccupied(sectorCode, true)).thenReturn(8L) // 80%

        val price = pricingService.calculatePrice(sectorCode, entry, entry.plusHours(1))

        assertThat(price).isEqualTo(12.5) // 10.0 * 1.25 * 1h
    }

    @Test
    fun `calculatePrice rounds partial hours up to the next full hour`() {
        whenever(sectorRepository.findById(sectorCode)).thenReturn(Optional.of(sector))
        whenever(spotRepository.countBySectorAndOccupied(sectorCode, true)).thenReturn(4L) // 40%

        // 90 minutes = 1.5h → ceil to 2h
        val price = pricingService.calculatePrice(sectorCode, entry, entry.plusMinutes(90))

        assertThat(price).isEqualTo(20.0) // 10.0 * 2h
    }
}
