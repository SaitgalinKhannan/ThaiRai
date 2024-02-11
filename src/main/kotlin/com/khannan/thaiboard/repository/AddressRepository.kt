package com.khannan.thaiboard.repository

import com.khannan.thaiboard.dto.AddressDto
import com.khannan.thaiboard.model.Address

interface AddressRepository {
    suspend fun delete(userId: Long): Boolean
    suspend fun update(addressId: Long, addressDto: AddressDto): Boolean
    suspend fun create(addressDto: Address): Long
    suspend fun findById(id: Long): Address
}