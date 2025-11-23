package com.kumm.pdfvision.dto

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Represents a text element extracted from or to be added to a PDF")
data class PdfTextElement(
    @JsonProperty("text")
    @Schema(description = "The text content", example = "Hello World")
    val text: String = "",
    
    @JsonProperty("x")
    @Schema(description = "X coordinate in PDF units", example = "100.5")
    val x: Double = 0.0,
    
    @JsonProperty("y")
    @Schema(description = "Y coordinate in PDF units (baseline)", example = "750.2")
    val y: Double = 0.0,
    
    @JsonProperty("width")
    @Schema(description = "Width of the text element in PDF units", example = "85.4")
    val width: Double = 0.0,
    
    @JsonProperty("height")
    @Schema(description = "Height of the text element in PDF units", example = "12.0")
    val height: Double = 0.0,
    
    @JsonProperty("fontSize")
    @Schema(description = "Font size in PDF units", example = "12.0")
    val fontSize: Double = 12.0,
    
    @JsonProperty("fontFamily")
    @Schema(description = "Font family name", example = "Arial, sans-serif")
    val fontFamily: String = "Arial",
    
    @JsonProperty("fontWeight")
    @Schema(description = "Font weight", example = "bold")
    val fontWeight: String = "normal",
    
    @JsonProperty("fontStyle")
    @Schema(description = "Font style", example = "italic")
    val fontStyle: String = "normal",
    
    @JsonProperty("color")
    @Schema(description = "Text color in hex format (e.g., #000000 for black)", example = "#000000")
    val color: String = "#000000",

    @JsonProperty("pageNumber")
    @Schema(description = "Page number (1-based)", example = "1")
    val pageNumber: Int = 1
)