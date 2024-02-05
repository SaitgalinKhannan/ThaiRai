package com.khannan.thaiboard.mapper

import com.khannan.thaiboard.dto.RealEstateDto
import com.khannan.thaiboard.model.RealEstate
import com.khannan.thaiboard.repository.AddressRepository
import com.khannan.thaiboard.repository.PhotoRepository
import com.khannan.thaiboard.repository.UserRepository
import org.springframework.stereotype.Component

@Component
class RealEstateMapper(
    private val addressRepository: AddressRepository,
    private val photoRepository: PhotoRepository,
    private val addressMapper: AddressMapper,
    private val photoMapper: PhotoMapper,
    private val userRepository: UserRepository,
    private val userMapper: UserMapper
) {
    fun toRealEstate(realEstateDto: RealEstateDto): RealEstate {
        return with(realEstateDto) {
            RealEstate(
                id = id,
                ownerId = owner.id,
                name = name,
                price = price,
                status = status,
                newBuilding = newBuilding,
                type = type,
                roomCount = roomCount,
                area = area,
                description = description,
                constructionYear = constructionYear,
                floor = floor,
                numberOfFloors = numberOfFloors,
                addressId = address.id,
                createdAt = createdAt,
                updatedAt = updatedAt
            )
        }
    }

    fun toRealEstateDto(realEstate: RealEstate): RealEstateDto {
        return with(realEstate) {
            RealEstateDto(
                id = id,
                owner = userMapper.toUserDto(userRepository.findById(ownerId)),
                name = name,
                price = price,
                status = status,
                newBuilding = newBuilding,
                type = type,
                roomCount = roomCount,
                area = area,
                description = description,
                constructionYear = constructionYear,
                floor = floor,
                numberOfFloors = numberOfFloors,
                address = addressMapper.toAddressDto(addressRepository.findById(addressId)),
                createdAt = createdAt,
                updatedAt = updatedAt,
                photos = photoMapper.toPhotoDtoList(photoRepository.findAllById(id))
            )
        }
    }
}