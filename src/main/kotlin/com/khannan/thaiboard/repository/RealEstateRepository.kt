package com.khannan.thaiboard.repository

import com.khannan.thaiboard.model.RealEstate
import com.khannan.thaiboard.model.Status
import com.khannan.thaiboard.model.User


interface RealEstateRepository {
    suspend fun findAllByStatus(status: Status): List<RealEstate>
    suspend fun findAllByOwner(user: User): List<RealEstate>
    suspend fun create(realEstate: RealEstate): RealEstate
    suspend fun findAll(limit: Int?, offset: Int?): List<RealEstate>
    suspend fun findById(id: Long): RealEstate
    suspend fun update(realEstate: RealEstate): Boolean
}