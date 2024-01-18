package com.khannan.thaiboard.model

data class Address(
    val id: Long,
    val country: String,
    val region: String,
    val district: String,
    val regionInCity: String,
    val street: String,
    val index: String,
    val houseNumber: String
)
