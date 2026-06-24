package com.toyoda.parking.estapar.service

import com.toyoda.parking.estapar.repository.ParkingEventRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.whenever

@ExtendWith(MockitoExtension::class)
class RevenueServiceTest {

    @Mock private lateinit var eventRepository: ParkingEventRepository
    @InjectMocks private lateinit var revenueService: RevenueService

    @Test
    fun `getRevenue returns DTO with summed amount for the given sector and date`() {
        whenever(eventRepository.sumAmountBySectorAndDate("A", "2024-01-15")).thenReturn(150.0)

        val result = revenueService.getRevenue("2024-01-15", "A")

        assertThat(result.amount).isEqualTo(150.0)
        assertThat(result.currency).isEqualTo("BRL")
        assertThat(result.timestamp).isNotBlank()
    }

    @Test
    fun `getRevenue returns zero amount when repository returns null`() {
        whenever(eventRepository.sumAmountBySectorAndDate("A", "2024-01-15")).thenReturn(null)

        val result = revenueService.getRevenue("2024-01-15", "A")

        assertThat(result.amount).isEqualTo(0.0)
        assertThat(result.currency).isEqualTo("BRL")
    }
}
