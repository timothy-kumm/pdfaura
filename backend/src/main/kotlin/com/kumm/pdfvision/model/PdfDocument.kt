package com.kumm.pdfvision.model

import io.hypersistence.utils.hibernate.type.json.JsonType
import jakarta.persistence.*
import org.hibernate.annotations.Type
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "pdf_documents")
data class PdfDocument(
    @Id
    val id: UUID = UUID.randomUUID(),

    @Column(nullable = false)
    val filename: String = "",

    @Column(name = "upload_date", nullable = false)
    val uploadDate: LocalDateTime = LocalDateTime.now(),

    @Type(JsonType::class)
    @Column(columnDefinition = "jsonb")
    val content: Map<String, Any> = emptyMap(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User? = null
)
