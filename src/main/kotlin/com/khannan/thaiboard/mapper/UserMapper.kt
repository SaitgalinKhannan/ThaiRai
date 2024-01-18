package com.khannan.thaiboard.mapper

import com.khannan.thaiboard.dto.UserDto
import com.khannan.thaiboard.model.User
import org.springframework.stereotype.Component

@Component
class UserMapper {
    fun toUser(userDto: UserDto): User {
        return with(userDto) {
            User(
                id = id,
                firstName = firstName,
                lastName = lastName,
                email = email,
                phone = phone,
                role = role,
                password = password
            )
        }
    }

    fun toUserDto(user: User): UserDto {
        return with(user) {
            UserDto(
                id = id,
                firstName = firstName,
                lastName = lastName,
                email = email,
                phone = phone,
                role = role,
                password = password,
            )
        }
    }
}