package com.khannan.thaiboard.model

import java.sql.Timestamp

data class RealEstate(
    val id: Long,
    val ownerId: Long,
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
    val addressId: Long,
    val createdAt: Timestamp,
    val updatedAt: Timestamp
)