package com.khannan.thaiboard.mapper

import com.khannan.thaiboard.dto.AddressDto
import com.khannan.thaiboard.model.Address
import org.springframework.stereotype.Component

@Component
class AddressMapper {
    fun toAddress(addressDto: AddressDto): Address {
        return with(addressDto) {
            Address(
                id = id,
                country = country,
                region = region,
                district = district,
                regionInCity = regionInCity,
                street = street,
                index = index,
                houseNumber = houseNumber
            )
        }
    }

    fun toAddressDto(address: Address): AddressDto {
        return with(address) {
            AddressDto(
                id = id,
                country = country,
                region = region,
                district = district,
                regionInCity = regionInCity,
                street = street,
                index = index,
                houseNumber = houseNumber
            )
        }
    }
}