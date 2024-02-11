package com.khannan.thaiboard.model

data class Token(
    val id: Int = 0,
    val token: String,
    val tokenType: TokenType,
    val revoked: Boolean,
    val expired: Boolean,
    val userId: Long
)