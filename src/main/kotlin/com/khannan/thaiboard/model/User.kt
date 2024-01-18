package com.khannan.thaiboard.model

class User(
    val id: Long,
    val firstName: String,
    val lastName: String,
    val email: String,
    val phone: String,
    val role: Role,
    val password: String
)