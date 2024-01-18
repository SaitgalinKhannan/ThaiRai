package com.khannan.thaiboard.repository.impl

import com.khannan.thaiboard.dto.RealEstateDto
import com.khannan.thaiboard.exception.RealEstateNotFoundException
import com.khannan.thaiboard.model.RealEstate
import com.khannan.thaiboard.model.Status
import com.khannan.thaiboard.model.User
import com.khannan.thaiboard.repository.RealEstateRepository
import org.springframework.stereotype.Repository
import java.sql.Connection
import java.sql.SQLException
import java.sql.Statement
import java.sql.Timestamp
import java.time.Instant
import javax.sql.DataSource

@Repository
class RealEstateRepositoryImpl(db: DataSource) : RealEstateRepository {
    private val connection: Connection = db.connection

    init {
        try {
            connection.createStatement().use { statement ->
                statement.executeUpdate(CREATE_TABLE_REAL_ESTATES)
            }
        } catch (e: SQLException) {
            println(e.message)
        }
    }

    override fun findById(id: Long): RealEstate {
        connection.prepareStatement(SELECT_REAL_ESTATES_BY_ID).use { statement ->
            statement.setLong(1, id)
            val resultSet = statement.executeQuery()

            if (resultSet.next()) {
                with(resultSet) {
                    return RealEstate(
                        id = getLong(1),
                        ownerId = getLong(2),
                        name = getString(3),
                        price = getFloat(4),
                        status = getString(5),
                        newBuilding = getBoolean(6),
                        type = getString(7),
                        roomCount = getInt(8),
                        area = getFloat(9),
                        description = getString(10),
                        constructionYear = getInt(11),
                        floor = getInt(12),
                        numberOfFloors = getInt(13),
                        addressId = getLong(14),
                        createdAt = getTimestamp(15),
                        updatedAt = getTimestamp(16)
                    )
                }
            } else {
                throw RealEstateNotFoundException("Advertisement with id = $id not found")
            }
        }
    }

    override fun findAllByStatus(status: Status): List<RealEstate> {
        connection.prepareStatement(SELECT_REAL_ESTATES_BY_STATUS).use { statement ->
            statement.setString(1, status.name)
            val resultSet = statement.executeQuery()
            val advertisements = mutableListOf<RealEstate>()

            while (resultSet.next()) {
                with(resultSet) {
                    advertisements.add(
                        RealEstate(
                            id = getLong(1),
                            ownerId = getLong(2),
                            name = getString(3),
                            price = getFloat(4),
                            status = getString(5),
                            newBuilding = getBoolean(6),
                            type = getString(7),
                            roomCount = getInt(8),
                            area = getFloat(9),
                            description = getString(10),
                            constructionYear = getInt(11),
                            floor = getInt(12),
                            numberOfFloors = getInt(13),
                            addressId = getLong(14),
                            createdAt = getTimestamp(15),
                            updatedAt = getTimestamp(16)
                        )
                    )
                }
            }

            return advertisements
        }
    }

    override fun findAllByOwner(user: User): List<RealEstate> {
        connection.prepareStatement(SELECT_REAL_ESTATES_BY_OWNER_ID).use { statement ->
            statement.setLong(1, user.id)
            val resultSet = statement.executeQuery()
            val advertisements = mutableListOf<RealEstate>()

            while (resultSet.next()) {
                with(resultSet) {
                    advertisements.add(
                        RealEstate(
                            id = getLong(1),
                            ownerId = getLong(2),
                            name = getString(3),
                            price = getFloat(4),
                            status = getString(5),
                            newBuilding = getBoolean(6),
                            type = getString(7),
                            roomCount = getInt(8),
                            area = getFloat(9),
                            description = getString(10),
                            constructionYear = getInt(11),
                            floor = getInt(12),
                            numberOfFloors = getInt(13),
                            addressId = getLong(14),
                            createdAt = getTimestamp(15),
                            updatedAt = getTimestamp(16)
                        )
                    )
                }
            }

            return advertisements
        }
    }

    override fun create(realEstateDto: RealEstateDto): RealEstateDto {
        connection.prepareStatement(CREATE_REAL_ESTATES, Statement.RETURN_GENERATED_KEYS).use { statement ->
            with(realEstateDto) {
                statement.setLong(1, owner.id)
                statement.setString(2, name)
                statement.setFloat(3, price)
                statement.setString(4, status)
                statement.setBoolean(5, newBuilding)
                statement.setString(6, type)
                statement.setInt(7, roomCount)
                statement.setFloat(8,area)
                statement.setString(9, description)
                statement.setInt(10, constructionYear)
                statement.setInt(11, floor)
                statement.setInt(12, numberOfFloors)
                statement.setLong(13, address.id)
                statement.setTimestamp(14, Timestamp.from(Instant.now()))
                statement.setTimestamp(15, Timestamp.from(Instant.now()))
            }

            val result = statement.executeUpdate()
            val generatedKeys = statement.generatedKeys
            return if (generatedKeys.next() && result > 0) {
                realEstateDto.copy(id = generatedKeys.getLong(1))
            } else {
                throw SQLException("Creating realEstate failed, no ID obtained.")
            }
        }
    }

    override fun update(realEstateDto: RealEstateDto): Boolean {
        connection.prepareStatement(UPDATE_REAL_ESTATES_BY_ID).use { statement ->

            val resultSet = statement.executeUpdate()

            return resultSet > 0
        }
    }

    override fun findAll(): List<RealEstate> {
        connection.createStatement().use { statement ->
            val resultSet = statement.executeQuery(SELECT_REAL_ESTATES)
            val advertisements = mutableListOf<RealEstate>()

            while (resultSet.next()) {
                with(resultSet) {
                    advertisements.add(
                        RealEstate(
                            id = getLong(1),
                            ownerId = getLong(2),
                            name = getString(3),
                            price = getFloat(4),
                            status = getString(5),
                            newBuilding = getBoolean(6),
                            type = getString(7),
                            roomCount = getInt(8),
                            area = getFloat(9),
                            description = getString(10),
                            constructionYear = getInt(11),
                            floor = getInt(12),
                            numberOfFloors = getInt(13),
                            addressId = getLong(14),
                            createdAt = getTimestamp(15),
                            updatedAt = getTimestamp(16)
                        )
                    )
                }
            }

            return advertisements
        }
    }

    companion object {
        private const val CREATE_TABLE_REAL_ESTATES = """
                CREATE TABLE IF NOT EXISTS real_estates
                (
                    id                BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
                    owner_id          BIGINT                                             NOT NULL,
                    name              VARCHAR(255)                                       NOT NULL,
                    price             DECIMAL(30, 2)                                     NOT NULL,
                    status            VARCHAR(20)                                        NOT NULL,
                    new_building      BOOLEAN                                            NOT NULL,
                    type              VARCHAR(50)                                        NOT NULL,
                    room_count        INT                                                NOT NULL,
                    area              DECIMAL(20, 2)                                     NOT NULL,
                    description       TEXT                                               NOT NULL,
                    construction_year INT                                                NOT NULL,
                    floor             INT                                                NOT NULL,
                    number_of_floors  INT                                                NOT NULL,
                    address_id        BIGINT                                             NOT NULL,
                    created_at        TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
                    updated_at        TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
                    FOREIGN KEY (owner_id) REFERENCES users (ID),
                    FOREIGN KEY (address_id) REFERENCES addresses (ID)
                )
            """
        private const val SELECT_REAL_ESTATES_BY_ID = "SELECT * FROM real_estates WHERE id = ?"
        private const val SELECT_REAL_ESTATES_BY_STATUS = "SELECT * FROM real_estates WHERE status = ?"
        private const val SELECT_REAL_ESTATES_BY_OWNER_ID = "SELECT * FROM real_estates WHERE owner_id = ?"
        private const val CREATE_REAL_ESTATES =
            "INSERT INTO real_estates (owner_id, name, price, status, new_building, type, room_count, area, description, construction_year, floor, number_of_floors, address_id, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
        private const val SELECT_REAL_ESTATES = "SELECT * FROM real_estates"
        private const val UPDATE_REAL_ESTATES_BY_ID =
            "UPDATE real_estates SET name = ?, price = ?, status = ?, new_building = ?, type = ?, room_count = ?, area = ?, description = ?, construction_year = ?, floor = ?, number_of_floors = ?, address_id = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?"
    }
}