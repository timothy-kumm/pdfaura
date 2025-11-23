package com.kumm.pdfvision.security

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseToken
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException

@Component
class FirebaseTokenFilter(
    private val userRepository: com.kumm.pdfvision.repository.UserRepository
) : OncePerRequestFilter() {

    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val authHeader = request.getHeader("Authorization")

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            val token = authHeader.substring(7)
            try {
                // In a real scenario with configured Firebase Admin, this verifies the token
                // Ensure GOOGLE_APPLICATION_CREDENTIALS is set or FirebaseApp is initialized correctly
                val decodedToken: FirebaseToken = FirebaseAuth.getInstance().verifyIdToken(token)
                val uid = decodedToken.uid

                // Check if user exists, if not create and save
                if (!userRepository.existsById(uid)) {
                    val email = decodedToken.email ?: ""
                    val name = decodedToken.name
                    val picture = decodedToken.picture

                    val newUser = com.kumm.pdfvision.model.User(
                        id = uid,
                        email = email,
                        displayName = name,
                        photoUrl = picture
                    )
                    userRepository.save(newUser)
                    logger.info("Registered new user: $uid ($email)")
                }

                val authentication = UsernamePasswordAuthenticationToken(
                    uid, null, listOf(SimpleGrantedAuthority("ROLE_USER"))
                )
                authentication.details = WebAuthenticationDetailsSource().buildDetails(request)
                SecurityContextHolder.getContext().authentication = authentication

            } catch (e: Exception) {
                // Token verification failed
                logger.error("Firebase token verification failed", e)
                response.addHeader("X-Auth-Error", e.message)
            }
        }

        filterChain.doFilter(request, response)
    }
}
