package com.khannan.thaiboard.model

data class ChangePasswordRequest(
    val currentPassword: String,
    val newPassword: String,
    val confirmationPassword: String
)