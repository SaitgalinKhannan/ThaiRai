package com.khannan.thaiboard.repository

import com.khannan.thaiboard.dto.AddressDto
import com.khannan.thaiboard.model.Address

interface AddressRepository {
    fun delete(userId: Long): Boolean
    fun update(addressId: Long, addressDto: AddressDto): Boolean
    fun create(addressDto: Address): Address
    fun findById(id: Long): Address
}