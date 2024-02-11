package com.khannan.thaiboard.dto

import com.khannan.thaiboard.model.TokenType

data class TokenDto(
    val id: Int,
    val token: String,
    val tokenType: TokenType,
    val revoked: Boolean,
    val expired: Boolean,
    val userDto: UserDto
)