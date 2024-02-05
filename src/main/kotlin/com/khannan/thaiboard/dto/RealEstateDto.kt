package com.khannan.thaiboard.dto

import com.khannan.thaiboard.model.Status
import java.sql.Timestamp

data class RealEstateDto(
    val id: Long  = 0,
    val owner: UserDto,
    val name: String,
    val price: Float,
    val status: Status,
    val newBuilding: Boolean,
    val type: String,
    val roomCount: Int,
    val area: Float,
    val description: String,
    val constructionYear: Int,
    val floor: Int,
    val numberOfFloors: Int,
    val address: AddressDto,
    val createdAt: Timestamp = Timestamp(System.currentTimeMillis()),
    val updatedAt: Timestamp = Timestamp(System.currentTimeMillis()),
    val photos: List<PhotoDto> = listOf()
)