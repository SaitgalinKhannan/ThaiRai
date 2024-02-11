package com.khannan.thaiboard.service

import com.khannan.thaiboard.dto.PhotoDto
import com.khannan.thaiboard.dto.RealEstateDto
import com.khannan.thaiboard.mapper.PhotoMapper
import com.khannan.thaiboard.mapper.RealEstateMapper
import com.khannan.thaiboard.model.Address
import com.khannan.thaiboard.model.RealEstate
import com.khannan.thaiboard.model.Status
import com.khannan.thaiboard.repository.AddressRepository
import com.khannan.thaiboard.repository.PhotoRepository
import com.khannan.thaiboard.repository.RealEstateRepository
import com.khannan.thaiboard.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.util.*

@Service
class RealEstateService(
    private val userRepository: UserRepository,
    private val realEstateRepository: RealEstateRepository,
    private val realEstateMapper: RealEstateMapper,
    private val addressRepository: AddressRepository,
    private val photoRepository: PhotoRepository,
    private val photoMapper: PhotoMapper
) {
    suspend fun create(
        realEstate: RealEstate,
        address: Address,
        files: List<MultipartFile>,
        uploadPath: File
    ): RealEstateDto {
        val addressId = addressRepository.create(address)
        val newRealEstate = realEstateRepository.create(realEstate.copy(addressId = addressId))
        val photoDtoList = mutableListOf<PhotoDto>()

        for (photo in files) {
            val uuidFileName = UUID.randomUUID().toString()
            val resultFileName = uuidFileName + "." + photo.originalFilename
            val imageUrl = "$uploadPath/$resultFileName"
            photo.transferTo(File(imageUrl))
            photoDtoList.add(
                PhotoDto(
                    id = 0,
                    realEstateId = newRealEstate.id,
                    imageUrl = imageUrl
                )
            )
        }

        return realEstateMapper.toRealEstateDto(newRealEstate).copy(photos = photoRepository.createBatch(photoDtoList))
    }

    suspend fun realEstateById(realEstateId: Long): RealEstateDto {
        val realEstate = realEstateRepository.findById(realEstateId)
        val photos = photoMapper.toPhotoDtoList(photoRepository.findAllById(realEstateId))
        return realEstateMapper.toRealEstateDto(realEstate).copy(photos = photos)
    }

    suspend fun update(realEstateId: Long, realEstate: RealEstate): Boolean {
        val user = userRepository.findById(realEstate.ownerId)
        val realEstateFromDB = realEstateRepository.findById(realEstateId)

        return if (user.id != realEstateFromDB.ownerId) {
            throw Exception("Вы пытаетесь изменить чужую запись")
        } else {
            realEstateRepository.update(realEstate)
        }
    }

    suspend fun allRealEstates(limit: Int?, offset: Int?): List<RealEstateDto> {
        return realEstateRepository
            .findAll(limit, offset)
            .map { realEstateMapper.toRealEstateDto(it) }
    }

    suspend fun allRealEstatesByStatus(status: Status): List<RealEstateDto> {
        return realEstateRepository
            .findAllByStatus(status)
            .map { realEstateMapper.toRealEstateDto(it) }

    }

    suspend fun allByUser(userId: Long): List<RealEstateDto> {
        val user = userRepository.findById(userId)
        return realEstateRepository
            .findAllByOwner(user)
            .map { realEstateMapper.toRealEstateDto(it) }
    }

    suspend fun allRealEstateSortedByName(limit: Int?, offset: Int?): List<RealEstateDto> {
        return realEstateRepository
            .findAll(limit, offset)
            .sortedWith(Comparator.comparing(RealEstate::name))
            .map { realEstateMapper.toRealEstateDto(it) }

    }

    suspend fun allRealEstateSortedByOwner(limit: Int?, offset: Int?): List<RealEstateDto> {
        return realEstateRepository
            .findAll(limit, offset)
            .map { realEstateMapper.toRealEstateDto(it) }
            .sortedWith(Comparator.comparing { realEstate -> realEstate.owner.id })
    }
}