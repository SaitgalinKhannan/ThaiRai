package com.khannan.thaiboard.repository.impl

import com.khannan.thaiboard.model.Token
import com.khannan.thaiboard.model.TokenType
import com.khannan.thaiboard.repository.TokenRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Repository
import java.sql.Connection
import java.sql.Statement
import javax.sql.DataSource


@Repository
class TokenRepositoryImpl(db: DataSource) : TokenRepository {
    private val connection: Connection = db.connection
    private val dispatcher = Dispatchers.IO

    override suspend fun save(token: Token): Int = withContext(dispatcher) {
        connection.prepareStatement(CREATE_TOKEN, Statement.RETURN_GENERATED_KEYS).use { statement ->
            statement.setString(1, token.token)
            statement.setString(2, token.tokenType.name)
            statement.setBoolean(3, token.revoked)
            statement.setBoolean(4, token.expired)
            statement.setLong(5, token.userId)

            val result = statement.executeUpdate()
            val generatedKeys = statement.generatedKeys
            return@use if (generatedKeys.next() && result > 0) {
                generatedKeys.getInt(1)
            } else {
                throw Exception("Creating token failed, no ID obtained.")
            }
        }
    }

    override suspend fun saveAll(tokens: List<Token>): Boolean = withContext(dispatcher) {
        connection.prepareStatement(CREATE_TOKEN).use { statement ->
            for (token in tokens) {
                statement.setString(1, token.token)
                statement.setString(2, token.tokenType.name)
                statement.setBoolean(3, token.revoked)
                statement.setBoolean(4, token.expired)
                statement.setLong(5, token.userId)
            }

            val result = statement.executeUpdate()
            return@use result > 0
        }
    }

    override suspend fun findByToken(token: String): Token = withContext(dispatcher) {
        connection.prepareStatement(SELECT_TOKEN).use { statement ->
            statement.setString(1, token)
            val resultSet = statement.executeQuery()

            return@use if (resultSet.next()) {
                Token(
                    id = resultSet.getInt(1),
                    token = resultSet.getString(2),
                    tokenType = TokenType.valueOf(resultSet.getString(3)),
                    revoked = resultSet.getBoolean(4),
                    expired = resultSet.getBoolean(5),
                    userId = resultSet.getLong(6)
                )
            } else {
                throw Exception("Token $token not found")
            }
        }
    }

    override suspend fun findAllValidTokenByUser(id: Long): List<Token> = withContext(dispatcher) {
        val tokens = mutableListOf<Token>()
        connection.prepareStatement(SELECT_ALL_TOKENS).use { statement ->
            statement.setLong(1, id)
            val resultSet = statement.executeQuery()

            while (resultSet.next()) {
                Token(
                    id = resultSet.getInt(1),
                    token = resultSet.getString(2),
                    tokenType = TokenType.valueOf(resultSet.getString(3)),
                    revoked = resultSet.getBoolean(4),
                    expired = resultSet.getBoolean(5),
                    userId = resultSet.getLong(6)
                )
            }
            return@use tokens
        }
    }

    companion object {
        @Suppress("Unused")
        private const val CREATE_TOKEN_TABLE = """
            create table tokens
            (
                id        integer generated always as identity
                    primary key,
                token     varchar(255) not null,
                token_type varchar(50)  not null,
                revoked   boolean      not null,
                expired   boolean      not null,
                user_id    bigint       not null
                    references users
            );
        """
        private const val SELECT_ALL_TOKENS = """
            select t.*
            from tokens t
                inner join users us
                    on t.user_id = us.id
            where us.id = ?
            and (t.expired = false or t.revoked = false)
        """
        private const val SELECT_TOKEN = "SELECT * FROM tokens WHERE token = ?"
        private const val CREATE_TOKEN =
            "INSERT INTO tokens (token, token_type, revoked, expired, user_id) VALUES (?, ?, ?, ?, ?)"
    }
}