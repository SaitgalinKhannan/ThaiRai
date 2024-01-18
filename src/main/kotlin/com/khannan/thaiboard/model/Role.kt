package com.khannan.thaiboard.model

/*import org.springframework.security.core.authority.SimpleGrantedAuthority
import java.util.stream.Collectors*/


enum class Role(private val permissions: Set<Permission>) {
    USER(mutableSetOf(Permission.PERMISSION_USER)),
    ADMIN(mutableSetOf(Permission.PERMISSION_ADMIN, Permission.PERMISSION_USER));

    /*val authorities: Set<SimpleGrantedAuthority>
        get() = permissions.stream().map { permission: Permission ->
            SimpleGrantedAuthority(permission.permission)
        }.collect(Collectors.toSet())*/
}