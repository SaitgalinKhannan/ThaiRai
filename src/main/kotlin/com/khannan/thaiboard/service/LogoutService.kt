package com.khannan.thaiboard.service

import com.khannan.thaiboard.repository.TokenRepository
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import kotlinx.coroutines.runBlocking
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.logout.LogoutHandler
import org.springframework.stereotype.Service

@Service
class LogoutService(
    private val tokenRepository: TokenRepository
) : LogoutHandler {
    override fun logout(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication
    ) {
        runBlocking {
            val authHeader = request.getHeader("Authorization")
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return@runBlocking
            }
            val jwt = authHeader.substring(7)
            val storedToken = tokenRepository.findByToken(jwt)


            val newToken = storedToken.copy(expired = true, revoked = true)
            tokenRepository.save(newToken)
            SecurityContextHolder.clearContext()
        }
    }
}