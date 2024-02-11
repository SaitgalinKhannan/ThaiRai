package com.khannan.thaiboard.repository.impl

import com.khannan.thaiboard.dto.AddressDto
import com.khannan.thaiboard.model.Address
import com.khannan.thaiboard.repository.AddressRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Repository
import java.sql.Connection
import java.sql.SQLException
import java.sql.Statement
import javax.sql.DataSource

@Repository
class AddressRepositoryImpl(db: DataSource) : AddressRepository {
    private val connection: Connection = db.connection
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO

    init {
        try {
            connection.createStatement().use { statement ->
                statement.executeUpdate(CREATE_ADDRESS_TABLE)
            }
        } catch (e: SQLException) {
            println(e.message)
        }
    }

    override suspend fun findById(id: Long): Address = withContext(dispatcher) {
        connection.prepareStatement(SELECT_ADDRESS_BY_ID).use { statement ->
            statement.setLong(1, id)
            val resultSet = statement.executeQuery()

            return@use if (resultSet.next()) {
                with(resultSet) {
                    Address(
                        id = getLong(1),
                        country = getString(2),
                        region = getString(3),
                        district = getString(4),
                        regionInCity = getString(5),
                        street = getString(6),
                        index = getString(7),
                        houseNumber = getString(8)
                    )
                }
            } else {
                throw Exception("Address with $id not found")
            }
        }
    }

    override suspend fun create(addressDto: Address): Long = withContext(dispatcher) {
        connection.prepareStatement(CREATE_ADDRESS, Statement.RETURN_GENERATED_KEYS).use { statement ->
            with(addressDto) {
                statement.setString(1, country)
                statement.setString(2, region)
                statement.setString(3, district)
                statement.setString(4, regionInCity)
                statement.setString(5, street)
                statement.setString(6, index)
                statement.setString(7, houseNumber)
            }

            val result = statement.executeUpdate()
            val generatedKeys = statement.generatedKeys
            return@use if (generatedKeys.next() && result > 0) {
                generatedKeys.getLong(1)
            } else {
                throw Exception("Creating address failed, no ID obtained.")
            }
        }
    }

    override suspend fun update(addressId: Long, addressDto: AddressDto): Boolean = withContext(dispatcher) {
        connection.prepareStatement(UPDATE_ADDRESS_BY_ID).use { statement ->
            with(addressDto) {
                statement.setString(1, country)
                statement.setString(2, region)
                statement.setString(3, district)
                statement.setString(4, regionInCity)
                statement.setString(5, street)
                statement.setString(6, index)
                statement.setString(7, houseNumber)
                statement.setLong(8, id)
            }
            val resultSet = statement.executeUpdate()
            return@use resultSet > 0
        }
    }

    override suspend fun delete(userId: Long): Boolean = withContext(dispatcher) {
        connection.prepareStatement(DELETE_ADDRESS_BY_ID).use { statement ->
            statement.setLong(1, userId)
            val resultSet = statement.executeUpdate()
            return@use resultSet > 0
        }
    }

    companion object {
        private const val CREATE_ADDRESS_TABLE =
            """               
                CREATE TABLE IF NOT EXISTS addresses
                (
                    id             BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
                    country        VARCHAR(255) NOT NULL,
                    region         VARCHAR(255) NOT NULL,
                    district       VARCHAR(255) NOT NULL,
                    region_in_city VARCHAR(255) NOT NULL,
                    street         VARCHAR(255),
                    index          VARCHAR(255) NOT NULL,
                    house_number   VARCHAR(10)
                )
            """

        private const val SELECT_ADDRESS_BY_ID = "SELECT * FROM addresses WHERE id = ?"
        private const val CREATE_ADDRESS =
            "INSERT INTO addresses (country, region, district, region_in_city, street, index, house_number) VALUES (?, ?, ?, ?, ?, ?, ?)"
        private const val UPDATE_ADDRESS_BY_ID =
            "UPDATE addresses SET country = ?, region = ?, district = ?, region_in_city = ?, street = ?, index = ?, house_number = ? WHERE id = ?"
        private const val DELETE_ADDRESS_BY_ID = "DELETE FROM addresses WHERE id = ?"
    }
}