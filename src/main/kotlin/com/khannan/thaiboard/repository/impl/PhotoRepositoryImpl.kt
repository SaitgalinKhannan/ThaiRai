package com.khannan.thaiboard.repository.impl

import com.khannan.thaiboard.dto.PhotoDto
import com.khannan.thaiboard.model.Photo
import com.khannan.thaiboard.repository.PhotoRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Repository
import java.sql.Connection
import java.sql.SQLException
import java.sql.Statement
import javax.sql.DataSource

@Repository
class PhotoRepositoryImpl(db: DataSource) : PhotoRepository {
    private val connection: Connection = db.connection
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO

    init {
        try {
            connection.createStatement().use { statement ->
                statement.executeUpdate(CREATE_PHOTO_TABLE)
            }
        } catch (e: SQLException) {
            println(e.message)
        }
    }

    override suspend fun findById(id: Long): Photo = withContext(dispatcher) {
        connection.prepareStatement(SELECT_PHOTO_BY_ID).use { statement ->
            statement.setLong(1, id)
            val resultSet = statement.executeQuery()

            return@use if (resultSet.next()) {
                with(resultSet) {
                    Photo(
                        id = getLong(1),
                        realEstateId = getLong(2),
                        imageUrl = getString(3)
                    )
                }
            } else {
                throw Exception("Photo with id = $id not found")
            }
        }
    }

    override suspend fun findAllById(id: Long): List<Photo> = withContext(dispatcher) {
        connection.prepareStatement(SELECT_PHOTO_BY_REAL_ESTATE_ID).use { statement ->
            statement.setLong(1, id)
            val resultSet = statement.executeQuery()
            val photos = mutableListOf<Photo>()

            while (resultSet.next()) {
                with(resultSet) {
                    photos.add(
                        Photo(
                            id = getLong(1),
                            realEstateId = getLong(2),
                            imageUrl = getString(3)
                        )
                    )
                }
            }

            return@use photos
        }
    }

    override suspend fun create(photoDto: PhotoDto): Boolean = withContext(dispatcher) {
        connection.prepareStatement(CREATE_PHOTO, Statement.RETURN_GENERATED_KEYS).use { statement ->
            statement.setLong(1, photoDto.realEstateId)
            statement.setString(2, photoDto.imageUrl)

            statement.executeUpdate()
            val generatedKeysResultSet = statement.generatedKeys
            return@use generatedKeysResultSet.first()
        }
    }

    override suspend fun createBatch(photos: List<PhotoDto>): List<PhotoDto> = withContext(dispatcher) {
        connection.autoCommit = false
        return@withContext try {
            connection.prepareStatement(CREATE_PHOTO, Statement.RETURN_GENERATED_KEYS).use { statement ->
                for (photo in photos) {
                    statement.setLong(1, photo.realEstateId)
                    statement.setString(2, photo.imageUrl)
                    statement.addBatch()
                }

                val result = statement.executeBatch()
                val generatedKeysResultSet = statement.generatedKeys
                val generatedKeys = mutableListOf<Long>()

                while (generatedKeysResultSet.next()) {
                    generatedKeys.add(generatedKeysResultSet.getLong(1))
                }

                val photoWithId = photos.zip(generatedKeys).map {
                    it.first.copy(id = it.second)
                }
                connection.commit()
                if (result.all { it > 0 }) {
                    photoWithId
                } else {
                    throw Exception("Photos not saved")
                }
            }
        } catch (e: SQLException) {
            connection.rollback()
            throw e
        } finally {
            connection.autoCommit = true
        }
    }

    override suspend fun update(photoId: Long, photoDto: PhotoDto): Result<Boolean> = withContext(dispatcher) {
        connection.prepareStatement(UPDATE_PHOTO_BY_ID).use { statement ->
            runCatching {
                statement.setLong(1, photoDto.realEstateId)
                statement.setString(2, photoDto.imageUrl)
                statement.setLong(3, photoDto.id)
                val resultSet = statement.executeUpdate()
                return@use Result.success(resultSet > 0)
            }
        }
    }

    override suspend fun delete(photoId: Long): Result<Boolean> = withContext(dispatcher) {
        connection.prepareStatement(DELETE_PHOTO_BY_ID).use { statement ->
            runCatching {
                statement.setLong(1, photoId)
                val resultSet = statement.executeUpdate()
                return@use Result.success(resultSet > 0)
            }
        }
    }

    companion object {
        private const val CREATE_PHOTO_TABLE =
            """               
                CREATE TABLE IF NOT EXISTS photos
                (
                    id             BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
                    real_estate_id BIGINT REFERENCES real_estates (id) NOT NULL,
                    image_url      VARCHAR(255)                        NOT NULL
                )
            """
        private const val SELECT_PHOTO_BY_REAL_ESTATE_ID = "SELECT * FROM photos WHERE real_estate_id = ?"
        private const val SELECT_PHOTO_BY_ID = "SELECT * FROM photos WHERE id = ?"
        private const val CREATE_PHOTO = "INSERT INTO photos (real_estate_id, image_url) VALUES (?, ?)"
        private const val UPDATE_PHOTO_BY_ID = "UPDATE photos SET real_estate_id = ?, image_url = ? WHERE id = ?"
        private const val DELETE_PHOTO_BY_ID = "DELETE FROM photos WHERE id = ?"
    }
}