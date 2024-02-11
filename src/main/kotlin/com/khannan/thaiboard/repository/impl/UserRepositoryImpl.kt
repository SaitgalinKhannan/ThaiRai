package com.khannan.thaiboard.repository.impl

import com.khannan.thaiboard.model.Role
import com.khannan.thaiboard.model.User
import com.khannan.thaiboard.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Repository
import java.sql.Connection
import java.sql.SQLException
import java.sql.Statement
import javax.sql.DataSource

@Repository
class UserRepositoryImpl(db: DataSource) : UserRepository {
    private val connection: Connection = db.connection
    private val dispatcher = Dispatchers.IO

    init {
        try {
            connection.createStatement().use { statement ->
                statement.executeUpdate(CREATE_TABLE_USERS)
            }
        } catch (e: SQLException) {
            println(e.message)
        }
    }

    override suspend fun findByEmail(email: String): User = withContext(dispatcher) {
        connection.prepareStatement(SELECT_USER_BY_EMAIL).use { statement ->
            statement.setString(1, email)
            val resultSet = statement.executeQuery()

            if (resultSet.next()) {
                with(resultSet) {
                    return@use User(
                        id = getLong(1),
                        firstName = getString(2),
                        lastName = getString(3),
                        email = getString(4),
                        phone = getString(5),
                        role = Role.valueOf(getString(6)),
                        accountPassword = getString(7)
                    )
                }
            } else {
                throw Exception("User with email = $email not found")
            }
        }
    }

    override suspend fun findById(id: Long): User = withContext(dispatcher) {
        connection.prepareStatement(SELECT_USER_BY_ID).use { statement ->
            statement.setLong(1, id)
            val resultSet = statement.executeQuery()

            if (resultSet.next()) {
                with(resultSet) {
                    return@use User(
                        id = getLong(1),
                        firstName = getString(2),
                        lastName = getString(3),
                        email = getString(4),
                        phone = getString(5),
                        role = Role.valueOf(getString(6)),
                        accountPassword = getString(7)
                    )
                }
            } else {
                throw Exception("User with $id not found")
            }
        }
    }

    override suspend fun findAll(): List<User> = withContext(dispatcher) {
        connection.createStatement().use { statement ->
            val resultSet = statement.executeQuery(SELECT_ALL_USERS)
            val users = mutableListOf<User>()

            while (resultSet.next()) {
                with(resultSet) {
                    users.add(
                        User(
                            id = getLong(1),
                            firstName = getString(2),
                            lastName = getString(3),
                            email = getString(4),
                            phone = getString(5),
                            role = Role.valueOf(getString(6)),
                            accountPassword = getString(7)
                        )
                    )
                }
            }

            return@use users
        }
    }

    override suspend fun create(user: User): User = withContext(dispatcher) {
        connection.prepareStatement(CREATE_USER, Statement.RETURN_GENERATED_KEYS).use { statement ->
            statement.setString(1, user.firstName)
            statement.setString(2, user.lastName)
            statement.setString(3, user.email)
            statement.setString(4, user.phone)
            statement.setString(5, user.role.name)
            statement.setString(6, user.accountPassword)

            val result = statement.executeUpdate()
            val generatedKeys = statement.generatedKeys
            return@use if (generatedKeys.next() && result > 0) {
                user.copy(id = generatedKeys.getLong(1))
            } else {
                throw Exception("Creating user failed.")
            }
        }
    }

    override suspend fun update(userId: Long, user: User): Boolean = withContext(dispatcher) {
        connection.prepareStatement(UPDATE_USER_BY_ID).use { statement ->
            statement.setString(1, user.firstName)
            statement.setString(2, user.lastName)
            statement.setString(3, user.email)
            statement.setString(4, user.phone)
            statement.setString(5, user.role.name)
            statement.setString(6, user.accountPassword)
            statement.setLong(7, user.id)
            val resultSet = statement.executeUpdate()
            return@use resultSet > 0
        }
    }

    override suspend fun delete(userId: Long): Boolean = withContext(dispatcher) {
        connection.prepareStatement(DELETE_USER_BY_ID).use { statement ->
            statement.setLong(1, userId)
            val resultSet = statement.executeUpdate()
            return@use resultSet > 0
        }
    }

    companion object {
        private const val CREATE_TABLE_USERS =
            """               
                CREATE TABLE IF NOT EXISTS users
                (
                    id         BIGINT PRIMARY KEY GENERATED BY DEFAULT AS IDENTITY,
                    first_name VARCHAR(255)        NOT NULL,
                    last_name  VARCHAR(255)        NOT NULL,
                    email      VARCHAR(255) UNIQUE NOT NULL,
                    phone      VARCHAR(30),
                    role       VARCHAR(20)         NOT NULL,
                    password   VARCHAR(255)        NOT NULL
                )
            """
        private const val SELECT_ALL_USERS = "SELECT * FROM users"
        private const val SELECT_USER_BY_ID = "SELECT * FROM users WHERE id = ?"
        private const val SELECT_USER_BY_EMAIL = "SELECT * FROM users WHERE email = ?"
        private const val CREATE_USER =
            "INSERT INTO users (first_name, last_name, email, phone, role, password) VALUES (?, ?, ?, ?, ?, ?)"
        private const val UPDATE_USER_BY_ID =
            "UPDATE users SET first_name = ?, last_name = ?, email = ?, phone = ?, role = ?, password = ? WHERE id = ?"
        private const val DELETE_USER_BY_ID = "DELETE FROM users WHERE id = ?"
    }
}