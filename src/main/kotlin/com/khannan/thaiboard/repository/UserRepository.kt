package com.khannan.thaiboard.repository

import com.khannan.thaiboard.model.User

interface UserRepository {
    fun findByEmail(email: String): User
    fun findById(id: Long): User
    fun create(user: User): Boolean
    fun update(userId: Long, user: User): Boolean
    fun delete(userId: Long): Boolean
    fun findAll(): List<User>
}