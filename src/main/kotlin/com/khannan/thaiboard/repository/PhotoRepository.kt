package com.khannan.thaiboard.repository

import com.khannan.thaiboard.dto.PhotoDto
import com.khannan.thaiboard.model.Photo

interface PhotoRepository {
    fun delete(photoId: Long): Boolean
    fun findById(id: Long): Photo
    fun create(photoDto: PhotoDto): Boolean
    fun update(photoId: Long, photoDto: PhotoDto): Boolean
    fun findAllById(id: Long): List<Photo>
    fun createBatch(photos: List<PhotoDto>): List<PhotoDto>
}