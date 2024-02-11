package com.khannan.thaiboard.model

enum class Role(private val permissions: Set<Permission>) {
    USER(mutableSetOf(Permission.PERMISSION_USER)),
    ADMIN(mutableSetOf(Permission.PERMISSION_ADMIN, Permission.PERMISSION_USER));
}