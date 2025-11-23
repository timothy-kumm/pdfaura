package com.kumm.pdfvision.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Response for PDF upload operation")
data class PdfUploadResponse(
    @Schema(description = "Unique identifier for the uploaded PDF", example = "abc123")
    val id: String,
    
    @Schema(description = "Original filename of the uploaded PDF", example = "document.pdf")
    val filename: String,
    
    @Schema(description = "Size of the uploaded file in bytes", example = "1048576")
    val fileSize: Long,
    
    @Schema(description = "Status of the upload operation", example = "SUCCESS")
    val status: String
)