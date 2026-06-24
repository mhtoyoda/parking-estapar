package com.toyoda.parking.estapar.controller

import com.toyoda.parking.estapar.dto.response.RevenueResponseDTO
import com.toyoda.parking.estapar.service.RevenueService
import org.junit.jupiter.api.Test
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(RevenueController::class)
class RevenueControllerTest {

    @Autowired private lateinit var mockMvc: MockMvc

    @MockitoBean private lateinit var revenueService: RevenueService

    @Test
    fun `getRevenue returns HTTP 200 with the revenue response body`() {
        val json = """{"date":"2024-01-15","sector":"A"}"""
        val response = RevenueResponseDTO(
            amount = 150.0,
            currency = "BRL",
            timestamp = "2024-01-15T10:00:00Z"
        )
        whenever(revenueService.getRevenue("2024-01-15", "A")).thenReturn(response)

        mockMvc.perform(
            get("/revenue")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.amount").value(150.0))
            .andExpect(jsonPath("$.currency").value("BRL"))
            .andExpect(jsonPath("$.timestamp").value("2024-01-15T10:00:00Z"))
    }
}
