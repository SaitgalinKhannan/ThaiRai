package com.khannan.thaiboard.controller

import com.khannan.thaiboard.dto.RealEstateDto
import com.khannan.thaiboard.service.RealEstateService
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.io.File


@RestController
@RequestMapping(path = ["/realestates"])
class RealEstateController(private val realEstateService: RealEstateService) {
    @Value("\${upload.path}")
    private val uploadPath: String = ""

    @PostMapping("/create")
    fun create(
        @RequestPart realEstateDto: RealEstateDto,
        @RequestParam("files") files: List<MultipartFile>
    ): RealEstateDto {
        val uploadFolder = File(uploadPath)

        return realEstateService.create(realEstateDto, files, uploadFolder)
    }

    @GetMapping("/{realEstateId}")
    fun realEstateById(@PathVariable realEstateId: Long): RealEstateDto {
        return realEstateService.realEstateById(realEstateId)
    }

    @GetMapping
    fun all(): List<RealEstateDto> {
        return realEstateService.allRealEstate()
    }

    @GetMapping("/sort/name")
    fun allSortedByName(): List<RealEstateDto> {
        return realEstateService.allRealEstateSortedByName()
    }

    @GetMapping("/sort/owner")
    fun allSortedByOwner(): List<RealEstateDto> {
        return realEstateService.allRealEstateSortedByOwner()
    }

    @GetMapping("/active")
    fun allByStatusActive(): List<RealEstateDto> {
        return realEstateService.allRealEstatesByActiveStatus()
    }

    @GetMapping("/user/{userId}")
    fun getAllByUser(@PathVariable userId: Long): List<RealEstateDto> {
        return realEstateService.allByUser(userId)
    }

    @PatchMapping("/{realEstateId}")
    fun update(
        @PathVariable realEstateId: Long,
        @RequestBody advertisementDto: RealEstateDto
    ): Boolean {
        return realEstateService.update(realEstateId, advertisementDto)
    }
}