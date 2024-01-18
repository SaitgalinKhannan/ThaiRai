package com.khannan.thaiboard.dto

import java.sql.Timestamp

data class RealEstateDto(
    val id: Long,
    val owner: UserDto,
    val name: String,
    val price: Float,
    val status: String,
    val newBuilding: Boolean,
    val type: String,
    val roomCount: Int,
    val area: Float,
    val description: String,
    val constructionYear: Int,
    val floor: Int,
    val numberOfFloors: Int,
    val address: AddressDto,
    val createdAt: Timestamp,
    val updatedAt: Timestamp,
    val photos: List<PhotoDto>
)