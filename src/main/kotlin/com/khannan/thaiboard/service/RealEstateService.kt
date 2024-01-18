package com.khannan.thaiboard.service

import com.khannan.thaiboard.dto.PhotoDto
import com.khannan.thaiboard.dto.RealEstateDto
import com.khannan.thaiboard.exception.UserIsNotOwnerException
import com.khannan.thaiboard.mapper.RealEstateMapper
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
import java.util.stream.Collectors

@Service
class RealEstateService(
    private val userRepository: UserRepository,
    private val realEstateRepository: RealEstateRepository,
    private val realEstateMapper: RealEstateMapper,
    private val addressRepository: AddressRepository,
    private val photoRepository: PhotoRepository
) {
    fun create(realEstateDto: RealEstateDto, files: List<MultipartFile>, uploadPath: File): RealEstateDto {
        val address = addressRepository.create(realEstateDto.address)
        val realEstate = realEstateRepository.create(realEstateDto.copy(address = address))
        val photoDtoList = mutableListOf<PhotoDto>()

        for (photo in files) {
            val uuidFileName = UUID.randomUUID().toString()
            val resultFileName = uuidFileName + "." + photo.originalFilename
            val imageUrl = "$uploadPath/$resultFileName"
            photo.transferTo(File(imageUrl))
            photoDtoList.add(
                PhotoDto(
                    id = 0,
                    realEstateId = realEstate.id,
                    imageUrl = imageUrl
                )
            )
        }

        //photoDtoList = photoRepository.createBatch(photoDtoList).toMutableList()

        return realEstate.copy(photos = photoRepository.createBatch(photoDtoList))
    }

    fun realEstateById(realEstateId: Long): RealEstateDto {
        val advertisement = realEstateRepository.findById(realEstateId)
        return realEstateMapper.toRealEstateDto(advertisement)
    }

    fun update(realEstateId: Long, realEstateDto: RealEstateDto): Boolean {
        val owner = userRepository.findById(realEstateDto.owner.id)
        val advertisement = realEstateRepository.findById(realEstateDto.id)

        if (owner.id != advertisement.ownerId) {
            throw UserIsNotOwnerException("Вы пытаетесь изменить чужую запись")
        }

        return realEstateRepository.update(realEstateDto)
    }

    fun allRealEstate(): List<RealEstateDto> {
        return realEstateRepository
            .findAll()
            .stream()
            .map { realEstateMapper.toRealEstateDto(it) }
            .collect(Collectors.toList())
    }

    fun allRealEstatesByActiveStatus(): List<RealEstateDto> {
        return realEstateRepository
            .findAllByStatus(Status.ACTIVE)
            .stream()
            .map { realEstateMapper.toRealEstateDto(it) }
            .collect(Collectors.toList())
    }

    fun allByUser(userId: Long): List<RealEstateDto> {
        val owner = userRepository.findById(userId)
        return realEstateRepository
            .findAllByOwner(owner)
            .stream()
            .map { realEstateMapper.toRealEstateDto(it) }
            .collect(Collectors.toList())
    }

    fun allRealEstateSortedByName(): List<RealEstateDto> {
        return realEstateRepository
            .findAll()
            .stream()
            .sorted(Comparator.comparing(RealEstate::name))
            .map { realEstateMapper.toRealEstateDto(it) }
            .collect(Collectors.toList())
    }

    fun allRealEstateSortedByOwner(): List<RealEstateDto> {
        return realEstateRepository
            .findAll()
            .stream()
            .map { realEstateMapper.toRealEstateDto(it) }
            .sorted(Comparator.comparing { it.owner.id })
            .collect(Collectors.toList())
    }
}