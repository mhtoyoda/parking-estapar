package com.toyoda.parking.estapar.service

import com.toyoda.parking.estapar.dto.GarageConfigDTO
import com.toyoda.parking.estapar.model.Sector
import com.toyoda.parking.estapar.model.Spot
import com.toyoda.parking.estapar.repository.SectorRepository
import com.toyoda.parking.estapar.repository.SpotRepository
import jakarta.annotation.PostConstruct
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class GarageInitService(
    private val restTemplate: RestTemplate,
    private val sectorRepository: SectorRepository,
    private val spotRepository: SpotRepository,
    @Value("\${garage.simulator-url}") private val simulatorUrl: String
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @PostConstruct
    fun init() {
        log.info("Buscando configuração da garagem...")
        val config = restTemplate.getForObject("$simulatorUrl/garage", GarageConfigDTO::class.java)
            ?: throw IllegalStateException("Resposta vazia do simulador")

        config.garage.map { s ->
            Sector(sector = s.sector, basePrice = s.basePrice, maxCapacity = s.maxCapacity)
        }.let { sectorRepository.saveAll(it) }

        config.spots.map { sp ->
            Spot(id = sp.id, sector = sp.sector, lat = sp.lat, lng = sp.lng)
        }.let { spotRepository.saveAll(it) }

        log.info("Garagem inicializada: ${config.garage.size} setores, ${config.spots.size} vagas")
    }
}