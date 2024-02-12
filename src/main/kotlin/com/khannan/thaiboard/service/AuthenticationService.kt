package com.khannan.thaiboard.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.khannan.thaiboard.model.*
import com.khannan.thaiboard.repository.TokenRepository
import com.khannan.thaiboard.repository.UserRepository
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthenticationService(
    private val userRepository: UserRepository,
    private val tokenRepository: TokenRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtService: JwtService,
    private val authenticationManager: AuthenticationManager
) {
    suspend fun register(request: RegisterRequest): AuthenticationResponse {
        val user = User(
            firstName = request.firstname,
            lastName = request.lastname,
            email = request.email,
            accountPassword = passwordEncoder.encode(request.password),
            phone = request.phone,
            role = request.role
        )
        val savedUser = userRepository.create(user)
        val jwtToken = jwtService.generateToken(user)
        val refreshToken = jwtService.generateRefreshToken(user)
        saveUserToken(savedUser.id, jwtToken)

        return AuthenticationResponse(jwtToken, refreshToken)
    }

    suspend fun authenticate(request: AuthenticationRequest): AuthenticationResponse {
        authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(
                request.email, request.password
            )
        )
        val user = userRepository.findByEmail(request.email)
        val jwtToken = jwtService.generateToken(user)
        val refreshToken = jwtService.generateRefreshToken(user)
        revokeAllUserTokens(user)
        saveUserToken(user.id, jwtToken)

        return AuthenticationResponse(jwtToken, refreshToken)
    }

    private suspend fun saveUserToken(userId: Long, jwtToken: String): Int {
        val token = Token(
            id = 0,
            token = jwtToken,
            tokenType = TokenType.BEARER,
            revoked = false,
            expired = false,
            userId = userId
        )
        return tokenRepository.save(token)
    }

    private suspend fun revokeAllUserTokens(user: User): Boolean {
        val validUserTokens = tokenRepository.findAllValidTokenByUser(user.id)
        val userTokensList = validUserTokens.toMutableList()
        if (userTokensList.isEmpty())
            return false

        for (token in userTokensList) {
            val newToken = token.copy(expired = true, revoked = true)
            userTokensList.remove(token)
            userTokensList.add(newToken)
        }

        return tokenRepository.saveAll(userTokensList)
    }

    suspend fun refreshToken(
        request: HttpServletRequest, response: HttpServletResponse
    ) {
        val authHeader = request.getHeader(HttpHeaders.AUTHORIZATION)
        val userEmail: String?
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return
        }
        val refreshToken = authHeader.substring(7)
        userEmail = jwtService.extractUsername(refreshToken)
        if (userEmail != null) {
            val user = userRepository.findByEmail(userEmail)
            if (jwtService.isTokenValid(refreshToken, user)) {
                val accessToken = jwtService.generateToken(user)
                revokeAllUserTokens(user)
                saveUserToken(user.id, accessToken)
                val authResponse = AuthenticationResponse(accessToken, refreshToken)
                ObjectMapper().writeValue(response.outputStream, authResponse)
            }
        }
    }
}