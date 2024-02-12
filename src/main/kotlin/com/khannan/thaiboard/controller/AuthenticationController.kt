package com.khannan.thaiboard.controller

import com.khannan.thaiboard.model.AuthenticationRequest
import com.khannan.thaiboard.model.AuthenticationResponse
import com.khannan.thaiboard.model.RegisterRequest
import com.khannan.thaiboard.service.AuthenticationService
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(path = ["/api/v1/auth"])
class AuthenticationController(
    private val authenticationService: AuthenticationService
) {
    @PostMapping("/register")
    suspend fun register(
        @RequestBody request: RegisterRequest
    ): ResponseEntity<AuthenticationResponse> {
        return ResponseEntity.ok(authenticationService.register(request))
    }

    @PostMapping("/authenticate")
    suspend fun authenticate(
        @RequestBody request: AuthenticationRequest
    ): ResponseEntity<AuthenticationResponse> {
        return ResponseEntity.ok(authenticationService.authenticate(request))
    }

    @PostMapping("/refresh-token")
    suspend fun refreshToken(
        request: HttpServletRequest, response: HttpServletResponse
    ) {
        return authenticationService.refreshToken(request, response)
    }
}