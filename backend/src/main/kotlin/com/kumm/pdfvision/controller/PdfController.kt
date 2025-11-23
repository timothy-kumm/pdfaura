package com.kumm.pdfvision.controller

import com.kumm.pdfvision.dto.PdfTextElement
import com.kumm.pdfvision.dto.PdfUploadResponse
import com.kumm.pdfvision.service.PdfProcessingService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.util.*

@RestController
@RequestMapping("/api/pdf")
@Tag(name = "PDF Operations", description = "API endpoints for PDF processing")
class PdfController(
    private val pdfProcessingService: PdfProcessingService
) {

    @Operation(
        summary = "Upload a PDF file",
        description = "Upload a PDF file for processing. The file will be validated and stored."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "PDF uploaded successfully",
                content = [Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = Schema(implementation = PdfUploadResponse::class)
                )]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Invalid file format or request",
                content = [Content()]
            ),
            ApiResponse(
                responseCode = "500",
                description = "Internal server error",
                content = [Content()]
            )
        ]
    )
    @PostMapping("/upload", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun uploadPdf(
        @Parameter(description = "PDF file to upload", required = true)
        @RequestParam("file") file: MultipartFile
    ): ResponseEntity<PdfUploadResponse> {
        // Sample implementation - replace with actual logic
        val response = PdfUploadResponse(
            id = "pdf_${System.currentTimeMillis()}",
            filename = file.originalFilename ?: "unknown.pdf",
            fileSize = file.size,
            status = "SUCCESS"
        )
        return ResponseEntity.ok(response)
    }

    @Operation(
        summary = "Get PDF information",
        description = "Retrieve information about a previously uploaded PDF"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "PDF information retrieved successfully",
                content = [Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = Schema(implementation = PdfUploadResponse::class)
                )]
            ),
            ApiResponse(
                responseCode = "404",
                description = "PDF not found",
                content = [Content()]
            )
        ]
    )
    @GetMapping("/{id}")
    fun getPdfInfo(
        @Parameter(description = "PDF identifier", required = true)
        @PathVariable id: String
    ): ResponseEntity<PdfUploadResponse> {
        // Sample implementation - replace with actual logic
        val response = PdfUploadResponse(
            id = id,
            filename = "sample.pdf",
            fileSize = 1024000,
            status = "PROCESSED"
        )
        return ResponseEntity.ok(response)
    }

    @Operation(
        summary = "Extract text elements from PDF",
        description = "Extract all text elements from a PDF file including their positions, fonts, and styling information"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Text elements extracted successfully",
                content = [Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = Schema(implementation = Array<PdfTextElement>::class)
                )]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Invalid PDF file",
                content = [Content()]
            ),
            ApiResponse(
                responseCode = "500",
                description = "Internal server error during text extraction",
                content = [Content()]
            )
        ]
    )
    @PostMapping("/extract-text", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun extractTextFromPdf(
        @Parameter(description = "PDF file to extract text from", required = true)
        @RequestParam("file") file: MultipartFile
    ): ResponseEntity<List<PdfTextElement>> {
        return try {
            // Validate file type
            if (file.contentType != MediaType.APPLICATION_PDF_VALUE) {
                return ResponseEntity.badRequest().build()
            }
            
            // Extract text elements
            val textElements = pdfProcessingService.extractTextElements(file.bytes)
            
            ResponseEntity.ok(textElements)
            
        } catch (e: Exception) {
            println("Error extracting text from PDF: ${e.message}")
            e.printStackTrace()
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        }
    }
}