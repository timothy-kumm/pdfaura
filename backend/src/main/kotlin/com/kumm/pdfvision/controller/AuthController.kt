package com.kumm.pdfvision.controller

import com.kumm.pdfvision.dto.UserDto
import com.kumm.pdfvision.repository.UserRepository
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Authentication related endpoints")
class AuthController(
    private val userRepository: UserRepository
) {

    @Operation(summary = "Get current user", description = "Retrieves the currently authenticated user's details")
    @GetMapping("/me")
    fun getCurrentUser(): ResponseEntity<UserDto> {
        val authentication = SecurityContextHolder.getContext().authentication
        val userId = authentication.name // This is the UID from the token

        val user = userRepository.findById(userId).orElseThrow {
            RuntimeException("User not found")
        }

        return ResponseEntity.ok(
            UserDto(
                id = user.id,
                email = user.email,
                displayName = user.displayName,
                photoUrl = user.photoUrl
            )
        )
    }
}
