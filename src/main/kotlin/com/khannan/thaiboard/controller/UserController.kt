package com.khannan.thaiboard.controller

import com.khannan.thaiboard.dto.UserDto
import com.khannan.thaiboard.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping(path = ["/users"])
class UserController @Autowired constructor(private val userService: UserService) {
    @PostMapping
    fun create(@RequestBody userDto: UserDto): Boolean {
        return userService.create(userDto)
    }

    @PatchMapping("/{userId}")
    fun update(@PathVariable userId: Long, @RequestBody userDto: UserDto): Boolean {
        return userService.update(userId, userDto)
    }

    @DeleteMapping("/{userId}")
    fun delete(@PathVariable userId: Long) {
        userService.delete(userId)
    }

    @GetMapping("/{userId}")
    fun getUserById(@PathVariable userId: Long): UserDto {
        return userService.getUserById(userId)
    }

    @GetMapping
    fun allUsers(): List<UserDto> {
        return userService.getAllUsers()
    }
}