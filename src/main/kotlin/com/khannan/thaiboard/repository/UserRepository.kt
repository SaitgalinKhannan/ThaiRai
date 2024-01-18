package com.khannan.thaiboard.repository

import com.khannan.thaiboard.dto.UserDto
import com.khannan.thaiboard.model.User

interface UserRepository {
    fun findByEmail(email: String): User
    fun findById(id: Long): User
    fun create(userDto: UserDto): Boolean
    fun update(userId: Long, userDto: UserDto): Boolean
    fun delete(userId: Long): Boolean
    fun findAll(): List<User>
}