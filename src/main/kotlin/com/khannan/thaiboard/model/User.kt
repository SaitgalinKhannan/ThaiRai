package com.khannan.thaiboard.model

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

data class User(
    val id: Long = 0,
    val firstName: String,
    val lastName: String,
    val email: String,
    val phone: String,
    val role: Role,
    val accountPassword: String
) : UserDetails {
    override fun getAuthorities(): Collection<GrantedAuthority> {
        return listOf(SimpleGrantedAuthority(role.name))
    }

    override fun getPassword(): String {
        return accountPassword
    }

    override fun getUsername(): String {
        return email
    }

    override fun isAccountNonExpired(): Boolean {
        return true
    }

    override fun isAccountNonLocked(): Boolean {
        return true
    }

    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    override fun isEnabled(): Boolean {
        return true
    }
}