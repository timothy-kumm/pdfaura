package com.kumm.pdfvision.model

import jakarta.persistence.*

@Entity
@Table(name = "users")
data class User(
    @Id
    @Column(length = 128)
    val id: String = "", // Firebase UID

    @Column(nullable = false)
    val email: String = "",

    @Column(name = "display_name")
    val displayName: String? = null,

    @Column(name = "photo_url")
    val photoUrl: String? = null
)
