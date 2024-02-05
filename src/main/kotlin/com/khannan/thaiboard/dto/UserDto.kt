package com.khannan.thaiboard.dto

import com.khannan.thaiboard.model.Role

class UserDto(
    val id: Long = 0,
    val firstName: String,
    val lastName: String,
    val email: String,
    val phone: String,
    val role: Role,
    val password: String
) : Comparable<UserDto> {
    override fun compareTo(other: UserDto): Int {
        return id.compareTo(other.id)
    }
}