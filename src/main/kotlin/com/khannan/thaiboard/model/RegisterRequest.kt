package com.khannan.thaiboard.model

data class RegisterRequest(
    val firstname: String,
    val lastname: String,
    val email: String,
    val password: String,
    val phone: String,
    val role: Role
)