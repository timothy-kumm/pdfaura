package com.kumm.pdfvision.repository

import com.kumm.pdfvision.model.PdfDocument
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface PdfDocumentRepository : JpaRepository<PdfDocument, UUID> {
    fun findByUserId(userId: String): List<PdfDocument>
}
