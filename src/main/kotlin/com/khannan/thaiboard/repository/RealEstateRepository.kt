package com.khannan.thaiboard.repository

import com.khannan.thaiboard.dto.RealEstateDto
import com.khannan.thaiboard.model.RealEstate
import com.khannan.thaiboard.model.Status
import com.khannan.thaiboard.model.User


interface RealEstateRepository {
    fun findAllByStatus(status: Status): List<RealEstate>
    fun findAllByOwner(user: User): List<RealEstate>
    fun create(realEstateDto: RealEstateDto): RealEstateDto
    fun findAll(): List<RealEstate>
    fun findById(id: Long): RealEstate
    fun update(realEstateDto: RealEstateDto): Boolean
}