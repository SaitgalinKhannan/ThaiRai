package com.khannan.thaiboard.service

import com.khannan.thaiboard.dto.UserDto
import com.khannan.thaiboard.mapper.UserMapper
import com.khannan.thaiboard.model.ChangePasswordRequest
import com.khannan.thaiboard.model.User
import com.khannan.thaiboard.repository.UserRepository
import kotlinx.coroutines.runBlocking
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.security.Principal

@Service
class UserService(
    private val userRepository: UserRepository,
    private val userMapper: UserMapper,
    private val passwordEncoder: PasswordEncoder
) : UserDetailsService {
    suspend fun create(userDto: UserDto): UserDto {
        val user = userRepository.create(userMapper.toUser(userDto))
        return userMapper.toUserDto(user)
    }

    suspend fun update(userId: Long, userDto: UserDto): Boolean {
        return userRepository.update(userId, userMapper.toUser(userDto))
    }

    suspend fun getAllUsers(): List<UserDto> {
        return userRepository
            .findAll()
            .map { userMapper.toUserDto(it) }
    }

    suspend fun delete(userId: Long) {
        userRepository.delete(userId)
    }

    suspend fun getUserById(userId: Long): UserDto {
        return userMapper.toUserDto(userRepository.findById(userId))
    }

    @Suppress("Unused")
    suspend fun getUserEmail(email: String): UserDto {
        return userMapper.toUserDto(userRepository.findByEmail(email))
    }

    override fun loadUserByUsername(username: String): UserDetails? = runBlocking {
        return@runBlocking userRepository.findByEmail(username)
    }

    suspend fun changePassword(request: ChangePasswordRequest, connectedUser: Principal) {
        val user = (connectedUser as UsernamePasswordAuthenticationToken).principal as User

        // check if the current password is correct
        check(passwordEncoder.matches(request.currentPassword, user.password)) { "Wrong password" }
        // check if the two new passwords are the same
        check(request.newPassword == request.confirmationPassword) { "Password are not the same" }

        // update the password
        val userWithNewPassword = user.copy(accountPassword = passwordEncoder.encode(request.newPassword))

        // save the new password
        userRepository.create(userWithNewPassword)
    }
}