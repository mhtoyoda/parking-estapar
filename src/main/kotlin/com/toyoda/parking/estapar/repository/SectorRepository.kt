package com.toyoda.parking.estapar.repository

import com.toyoda.parking.estapar.model.Sector
import org.springframework.data.jpa.repository.JpaRepository

interface SectorRepository: JpaRepository<Sector, String>