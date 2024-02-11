package com.khannan.thaiboard.controller

import com.khannan.thaiboard.dto.RealEstateDto
import com.khannan.thaiboard.mapper.AddressMapper
import com.khannan.thaiboard.mapper.RealEstateMapper
import com.khannan.thaiboard.model.Status
import com.khannan.thaiboard.service.RealEstateService
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.io.File


@RestController
@RequestMapping(path = ["/realestates"])
class RealEstateController(
    private val realEstateService: RealEstateService,
    private val realEstateMapper: RealEstateMapper,
    private val addressMapper: AddressMapper
) {
    @Value("\${upload.path}")
    private val uploadPath: String = ""

    @PostMapping("/create")
    suspend fun create(
        @RequestPart realEstateDto: RealEstateDto,
        @RequestParam("files") files: List<MultipartFile>
    ): RealEstateDto {
        val uploadFolder = File(uploadPath)
        if (!uploadFolder.exists() && uploadFolder.mkdir()) {
            println("$uploadPath folder created")
        }
        val realEstate = realEstateMapper.toRealEstate(realEstateDto)
        val address = addressMapper.toAddress(realEstateDto.address)
        return realEstateService.create(realEstate, address, files, uploadFolder)
    }

    @GetMapping("/{realEstateId}")
    suspend  fun realEstateById(@PathVariable realEstateId: Long): RealEstateDto {
        return realEstateService.realEstateById(realEstateId)
    }

    @GetMapping
    suspend fun all(
        @RequestParam(required = false) limit: Int?,
        @RequestParam(required = false) offset: Int?
    ): List<RealEstateDto> {
        return realEstateService.allRealEstates(limit, offset)
    }

    @GetMapping("/sort/name")
    suspend fun allSortedByName(
        @RequestParam(required = false) limit: Int?,
        @RequestParam(required = false) offset: Int?
    ): List<RealEstateDto> {
        return realEstateService.allRealEstateSortedByName(limit, offset)
    }

    @GetMapping("/sort/owner")
    suspend fun allSortedByOwner(
        @RequestParam(required = false) limit: Int?,
        @RequestParam(required = false) offset: Int?
    ): List<RealEstateDto> {
        return realEstateService.allRealEstateSortedByOwner(limit, offset)
    }

    @GetMapping("/rent")
    suspend fun allByStatusRent(): List<RealEstateDto> {
        return realEstateService.allRealEstatesByStatus(Status.RENT)
    }

    @GetMapping("/sale")
    suspend fun allByStatusSale(): List<RealEstateDto> {
        return realEstateService.allRealEstatesByStatus(Status.SALE)
    }

    @GetMapping("/user/{userId}")
    suspend fun getAllByUser(@PathVariable userId: Long): List<RealEstateDto> {
        return realEstateService.allByUser(userId)
    }

    @PatchMapping("/{realEstateId}")
    suspend fun update(
        @PathVariable realEstateId: Long,
        @RequestBody realEstateDto: RealEstateDto
    ): Boolean {
        val realEstate = realEstateMapper.toRealEstate(realEstateDto)
        return realEstateService.update(realEstateId, realEstate)
    }
}