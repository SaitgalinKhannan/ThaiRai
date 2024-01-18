package com.khannan.thaiboard.mapper

import com.khannan.thaiboard.dto.PhotoDto
import com.khannan.thaiboard.model.Photo
import org.springframework.stereotype.Component

@Component
class PhotoMapper {
    fun toPhoto(photoDto: PhotoDto): Photo {
        return with(photoDto) {
            Photo(id = id, realEstateId = realEstateId, imageUrl = imageUrl)
        }
    }

    fun toPhotoDto(photo: Photo): PhotoDto {
        return with(photo) {
            PhotoDto(id = id, realEstateId = realEstateId, imageUrl = imageUrl)
        }
    }
}