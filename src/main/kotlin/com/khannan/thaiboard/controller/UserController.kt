package com.khannan.thaiboard.controller

import com.khannan.thaiboard.dto.UserDto
import com.khannan.thaiboard.model.ChangePasswordRequest
import com.khannan.thaiboard.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.security.Principal


@RestController
@RequestMapping(path = ["/users"])
class UserController(private val userService: UserService) {
    @PostMapping
    suspend fun create(@RequestBody userDto: UserDto): UserDto {
        return userService.create(userDto)
    }

    @PatchMapping("/{userId}")
    suspend fun update(@PathVariable userId: Long, @RequestBody userDto: UserDto): Boolean {
        return userService.update(userId, userDto)
    }

    @DeleteMapping("/{userId}")
    suspend fun delete(@PathVariable userId: Long) {
        userService.delete(userId)
    }

    @GetMapping("/{userId}")
    suspend fun getUserById(@PathVariable userId: Long): UserDto {
        return userService.getUserById(userId)
    }

    @GetMapping
    suspend fun allUsers(): List<UserDto> {
        return userService.getAllUsers()
    }

    @PatchMapping
    suspend fun changePassword(
        @RequestBody request: ChangePasswordRequest,
        connectedUser: Principal
    ): ResponseEntity<HttpStatus> {
        userService.changePassword(request, connectedUser)
        return ResponseEntity.ok().build()
    }
}