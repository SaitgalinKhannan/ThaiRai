package com.khannan.thaiboard.dto

data class AddressDto(
    val id: Long = 0,
    val country: String,
    val region: String,
    val district: String,
    val regionInCity: String,
    val street: String,
    val index: String,
    val houseNumber: String
)
