package com.khannan.thaiboard.repository

import com.khannan.thaiboard.model.User

interface UserRepository {
    suspend fun findByEmail(email: String): User
    suspend fun findById(id: Long): User
    suspend fun create(user: User): User
    suspend fun update(userId: Long, user: User): Boolean
    suspend fun delete(userId: Long): Boolean
    suspend fun findAll(): List<User>
}