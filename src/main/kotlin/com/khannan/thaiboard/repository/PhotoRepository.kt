package com.khannan.thaiboard.repository

import com.khannan.thaiboard.dto.PhotoDto
import com.khannan.thaiboard.model.Photo

interface PhotoRepository {
    suspend fun delete(photoId: Long): Result<Boolean>
    suspend fun findById(id: Long): Photo
    suspend fun create(photoDto: PhotoDto): Boolean
    suspend fun update(photoId: Long, photoDto: PhotoDto): Result<Boolean>
    suspend fun findAllById(id: Long): List<Photo>
    suspend fun createBatch(photos: List<PhotoDto>): List<PhotoDto>
}