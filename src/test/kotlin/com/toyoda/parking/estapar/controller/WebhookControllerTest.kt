package com.toyoda.parking.estapar.controller

import com.toyoda.parking.estapar.service.ParkingService
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(WebhookController::class)
class WebhookControllerTest {

    @Autowired private lateinit var mockMvc: MockMvc

    @MockitoBean private lateinit var parkingService: ParkingService

    @Test
    fun `ENTRY event delegates to handleEntry and returns HTTP 200`() {
        val json = """{"event_type":"ENTRY","license_plate":"ABC-1234","entry_time":"2024-01-15T10:00:00"}"""

        mockMvc.perform(
            post("/webhook")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
        ).andExpect(status().isOk)

        verify(parkingService).handleEntry(eq("ABC-1234"), any())
    }

    @Test
    fun `PARKED event delegates to handleParked and returns HTTP 200`() {
        val json = """{"event_type":"PARKED","license_plate":"ABC-1234","lat":-23.5,"lng":-46.6}"""

        mockMvc.perform(
            post("/webhook")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
        ).andExpect(status().isOk)

        verify(parkingService).handleParked("ABC-1234", -23.5, -46.6)
    }

    @Test
    fun `EXIT event delegates to handleExit and returns HTTP 200`() {
        val json = """{"event_type":"EXIT","license_plate":"ABC-1234","exit_time":"2024-01-15T12:00:00"}"""

        mockMvc.perform(
            post("/webhook")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
        ).andExpect(status().isOk)

        verify(parkingService).handleExit(eq("ABC-1234"), any())
    }

    @Test
    fun `unknown event type returns HTTP 200 without invoking any service method`() {
        val json = """{"event_type":"UNKNOWN","license_plate":"ABC-1234"}"""

        mockMvc.perform(
            post("/webhook")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
        ).andExpect(status().isOk)

        verify(parkingService, never()).handleEntry(any(), any())
        verify(parkingService, never()).handleParked(any(), any(), any())
        verify(parkingService, never()).handleExit(any(), any())
    }
}
