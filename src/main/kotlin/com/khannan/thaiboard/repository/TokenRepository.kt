package com.khannan.thaiboard.repository

import com.khannan.thaiboard.model.Token

interface TokenRepository {
    suspend fun save(token: Token): Int
    suspend fun saveAll(tokens: List<Token>): Boolean
    suspend fun findByToken(token: String): Token
    suspend fun findAllValidTokenByUser(id: Long): List<Token>
}