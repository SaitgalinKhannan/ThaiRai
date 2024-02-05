package com.khannan.thaiboard.service

import com.khannan.thaiboard.dto.UserDto
import com.khannan.thaiboard.mapper.UserMapper
import com.khannan.thaiboard.repository.UserRepository
import org.springframework.stereotype.Service
import java.util.stream.Collectors

@Service
class UserService(private val userRepository: UserRepository, private val userMapper: UserMapper) {
    fun create(userDto: UserDto): Boolean {
        return userRepository.create(userMapper.toUser(userDto))
    }

    fun update(userId: Long, userDto: UserDto): Boolean {
        return userRepository.update(userId, userMapper.toUser(userDto))
    }

    fun getAllUsers(): List<UserDto> {
        return userRepository.findAll()
            .stream()
            .map { userMapper.toUserDto(it) }
            .collect(Collectors.toList())
    }

    fun delete(userId: Long) {
        userRepository.delete(userId)
    }

    fun getUserById(userId: Long): UserDto {
        return userMapper.toUserDto(userRepository.findById(userId))
    }

    @Suppress("Unused")
    fun getUserEmail(email: String): UserDto {
        return userMapper.toUserDto(userRepository.findByEmail(email))
    }
}