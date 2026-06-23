package com.toyoda.parking.estapar.controller

import com.toyoda.parking.estapar.dto.request.RevenueRequestDTO
import com.toyoda.parking.estapar.dto.response.RevenueResponseDTO
import com.toyoda.parking.estapar.service.RevenueService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class RevenueController(private val revenueService: RevenueService) {

    @GetMapping("/revenue")
    fun getRevenue(@RequestBody request: RevenueRequestDTO): ResponseEntity<RevenueResponseDTO> =
        ResponseEntity.ok(revenueService.getRevenue(request.date, request.sector))
}