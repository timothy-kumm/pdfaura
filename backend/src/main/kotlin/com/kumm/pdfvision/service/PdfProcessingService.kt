package com.kumm.pdfvision.service

import com.kumm.pdfvision.dto.PdfTextElement
import org.apache.pdfbox.Loader
import org.apache.pdfbox.pdmodel.graphics.color.PDColor
import org.apache.pdfbox.text.PDFTextStripper
import org.apache.pdfbox.text.TextPosition
import org.springframework.stereotype.Service
import java.awt.Color
import java.io.IOException
import kotlin.math.abs

private data class TextPositionWithColor(
    val textPosition: TextPosition,
    val color: String
)

@Service
class PdfProcessingService {

    /**
     * Extracts text elements from a PDF, grouping characters into words or phrases.
     * This version uses a more robust algorithm for word detection.
     */
    fun extractTextElements(pdfBytes: ByteArray): List<PdfTextElement> {
        println("Extracting text elements from PDF")

        return Loader.loadPDF(pdfBytes).use { document ->
            val textElements = mutableListOf<PdfTextElement>()

            for (pageIndex in 0 until document.numberOfPages) {
                val page = document.getPage(pageIndex)
                val pageNumber = pageIndex + 1

                println("Processing page ${'$'}pageNumber")

                // Create custom text stripper to capture raw TextPosition objects.
                val textStripper = CustomTextStripper()
                textStripper.startPage = pageNumber
                textStripper.endPage = pageNumber

                // This call populates the textPositions list in our custom stripper.
                textStripper.getText(document)

                // Group the extracted character positions into logical text elements (words).
                val groupedElements = groupTextPositions(textStripper.textPositions)
                textElements.addAll(groupedElements.map { group ->
                    PdfTextElement(
                        text = group.text,
                        x = group.x,
                        y = group.y, // Note: This 'y' is the baseline from the bottom of the page (PDF coordinates).
                        width = group.width,
                        height = group.height,
                        fontSize = group.fontSize,
                        fontFamily = extractFontFamily(group.fontName),
                        fontWeight = extractFontWeight(group.fontName),
                        fontStyle = extractFontStyle(group.fontName),
                        color = group.color,
                        pageNumber = pageNumber
                    )
                })
            }

            println("Extracted ${'$'}{textElements.size} text elements")
            textElements
        }
    }

    private data class TextGroup(
        val text: String,
        val x: Double,
        val y: Double, // PDF baseline coordinate (from bottom-left origin)
        val width: Double,
        val height: Double,
        val fontSize: Double,
        val fontName: String,
        val color: String
    )

    private fun groupTextPositions(textPositions: List<TextPositionWithColor>): List<TextGroup> {
        if (textPositions.isEmpty()) return emptyList()

        // Sortierung ist kritisch: Wir runden Y leicht, um "wackelnde" Zeilen zu vermeiden
        val sortedPositions = textPositions
            .filter { it.textPosition.unicode != null }
            .sortedWith(Comparator { o1, o2 ->
                val y1 = Math.round(o1.textPosition.y)
                val y2 = Math.round(o2.textPosition.y)
                if (y1 != y2) {
                    y1.compareTo(y2)
                } else {
                    o1.textPosition.x.compareTo(o2.textPosition.x)
                }
            })

        val groups = mutableListOf<TextGroup>()
        var currentWordPositions = mutableListOf<TextPositionWithColor>()
        var lastGroupInfo: Triple<Double, Double, Double>? = null

        for (pos in sortedPositions) {
            // Check: Ist das aktuelle Zeichen selbst ein explizites Leerzeichen?
            // (Manche PDFs kodieren Spaces als echte Zeichen, nicht nur als Abstand)
            if (pos.textPosition.unicode.trim().isEmpty()) {
                if (currentWordPositions.isNotEmpty()) {
                    val group = createWordGroupFromPositions(currentWordPositions, lastGroupInfo)
                    groups.add(group)
                    // Wir merken uns das Ende dieser Gruppe für die nächste Berechnung
                    val spaceW = if (pos.textPosition.widthOfSpace > 0) pos.textPosition.widthOfSpace else pos.textPosition.width
                    lastGroupInfo = Triple(group.x + group.width, group.y, spaceW.toDouble())
                    currentWordPositions = mutableListOf()
                }
                continue // Das Leerzeichen selbst nicht in das Wort aufnehmen
            }

            if (currentWordPositions.isEmpty()) {
                currentWordPositions.add(pos)
            } else {
                val lastPos = currentWordPositions.last().textPosition

                // 1. Zeilenwechsel prüfen
                val onSameLine = abs(pos.textPosition.y - lastPos.y) < (lastPos.height * 0.5f) // Toleranterer Y-Check

                // 2. Eigenschaften prüfen
                val sameFont = pos.textPosition.font == lastPos.font && abs(pos.textPosition.fontSizeInPt - lastPos.fontSizeInPt) < 0.5f
                val sameColor = pos.color == currentWordPositions.last().color

                // 3. Abstände berechnen (Das Herzstück des Fixes)
                val distance = pos.textPosition.x - (lastPos.x + lastPos.width)

                // ROBUSTE Space-Berechnung:
                // Wenn widthOfSpace kaputt ist (<=0), nehmen wir die Breite des letzten Zeichens als Schätzung.
                val validSpaceWidth = if (lastPos.widthOfSpace > 0) lastPos.widthOfSpace else lastPos.width

                // SCHWELLENWERT:
                // Wir trennen, wenn die Lücke > 35% der Leerzeichenbreite ist.
                // Ihr alter Wert (1.2f) war zu hoch, da ein normales Space exakt 1.0f ist.
                val isSubstantialGap = distance > (validSpaceWidth * 0.35f)

                val isNewGroup = !onSameLine || isSubstantialGap || !sameFont || !sameColor

                if (isNewGroup) {
                    val group = createWordGroupFromPositions(currentWordPositions, lastGroupInfo)
                    groups.add(group)
                    lastGroupInfo = Triple(group.x + group.width, group.y, validSpaceWidth.toDouble())
                    currentWordPositions = mutableListOf(pos)
                } else {
                    currentWordPositions.add(pos)
                }
            }
        }

        if (currentWordPositions.isNotEmpty()) {
            groups.add(createWordGroupFromPositions(currentWordPositions, lastGroupInfo))
        }

        println("Grouped ${textPositions.size} character positions into ${groups.size} word groups")
        return groups
    }

    private fun createWordGroupFromPositions(
        positions: List<TextPositionWithColor>,
        lastGroupInfo: Triple<Double, Double, Double>? = null
    ): TextGroup {
        // Sortierung nach X ist korrekt
        val sortedPositions = positions.sortedBy { it.textPosition.x }

        // Text zusammenbauen
        val text = sortedPositions.joinToString("") { it.textPosition.unicode }

        val firstPos = sortedPositions.first()
        val lastPos = sortedPositions.last()

        // -----------------------------------------------------------
        // FEHLERQUELLE: Diese "Korrektur"-Logik muss RAUS!
        // -----------------------------------------------------------
        var x = firstPos.textPosition.x.toDouble()
        val y = firstPos.textPosition.y.toDouble() // Baseline

        /* LÖSCHEN SIE DIESEN GANZEN BLOCK:
        if (lastGroupInfo != null) {
            val (lastGroupEndX, lastGroupY, lastSpaceWidth) = lastGroupInfo
            val onSameLine = abs(y - lastGroupY) < 1.0
            val distance = x - lastGroupEndX

            // Das hier verursacht den Ripple-Effekt (Verschieben des Wortes)
            if (onSameLine && distance > 0 && distance < lastSpaceWidth * 1.2) {
                 x = lastGroupEndX + lastSpaceWidth
            }
        }
        */
        // -----------------------------------------------------------

        // Breite exakt berechnen (Bounding Box der sichtbaren Zeichen)
        val width = (lastPos.textPosition.x + lastPos.textPosition.width - firstPos.textPosition.x).toDouble()

        val height = positions.maxOf { it.textPosition.height.toDouble() }
        val fontSize = firstPos.textPosition.fontSizeInPt.toDouble()

        // Font-Handling bleibt gleich...
        val font = firstPos.textPosition.font
        val fontName = when {
            font?.name != null -> font.name
            font?.fontDescriptor?.fontName != null -> font.fontDescriptor.fontName
            else -> "Unknown"
        }
        val color = firstPos.color

        return TextGroup(text, x, y, width, height, fontSize, fontName, color)
    }

    private fun extractFontFamily(fontName: String): String {
        // Clean up PDF subset prefixes (e.g., "DAAAAA+PlayfairDisplay-Italic" -> "PlayfairDisplay-Italic")
        val cleanedName = fontName.replace(Regex("^[A-Z]{6}\\+"), "").ifBlank { "Unknown" }
        
        // Log the cleaning process for debugging
        if (fontName != cleanedName) {
            println("DEBUG: Cleaned font name from '$fontName' to '$cleanedName'")
        }
        
        return cleanedName
    }

    private fun extractFontWeight(fontName: String): String {
        val lowerFontName = fontName.lowercase()
        return when {
            lowerFontName.contains("bold") -> "bold"
            lowerFontName.contains("black") || lowerFontName.contains("heavy") -> "900"
            lowerFontName.contains("light") -> "300"
            lowerFontName.contains("medium") -> "500"
            lowerFontName.contains("thin") -> "100"
            lowerFontName.contains("ultra") && lowerFontName.contains("light") -> "200"
            lowerFontName.contains("semi") && lowerFontName.contains("bold") -> "600"
            lowerFontName.contains("extra") && lowerFontName.contains("bold") -> "800"
            else -> "normal"
        }
    }

    private fun extractFontStyle(fontName: String): String {
        val lowerFontName = fontName.lowercase()
        return when {
            lowerFontName.contains("italic") || lowerFontName.contains("oblique") -> "italic"
            else -> "normal"
        }
    }
}

private class CustomTextStripper : PDFTextStripper() {
    val textPositions: MutableList<TextPositionWithColor> = mutableListOf()
    private var currentTextColor: String = "#000000"

    init {
        suppressDuplicateOverlappingText = false
    }

    @Throws(IOException::class)
    override fun writeString(text: String, textPositions: List<TextPosition>) {
        // This method is called after processTextPosition, so the color should already be captured.
        // We no longer need to get the color here.
        super.writeString(text, textPositions)
    }

    override fun processTextPosition(text: TextPosition) {
        val nonStrokingColor = graphicsState.nonStrokingColor
        val strokingColor = graphicsState.strokingColor
        val textRenderingMode = graphicsState.textState.renderingMode
        
        // Choose color based on text rendering mode
        val colorToUse = when (textRenderingMode.intValue()) {
            0, 2, 4, 6 -> nonStrokingColor // Fill modes
            1, 3, 5, 7 -> strokingColor    // Stroke modes
            else -> nonStrokingColor       // Default to fill
        }
        
        val hexColor = colorToHex(colorToUse)
        
        // Debug logging for a few characters to understand the issue
        if (text.unicode.isNotBlank()) {
            println("DEBUG: '${text.unicode}' - GraphicsState: $hexColor, ContentStream: $currentTextColor, ColorToUse: $colorToUse")
        }
        
        // Use content stream color if graphics state gives black but content stream has a color
        val finalColor = if (hexColor == "#000000" && currentTextColor != "#000000") {
            currentTextColor
        } else {
            hexColor
        }
        
        this.textPositions.add(TextPositionWithColor(text, finalColor))
        super.processTextPosition(text)
    }

    private fun colorToHex(pdColor: PDColor?): String {
        if (pdColor == null) {
            return "#000000"
        }

        try {
            // Handle different color spaces more explicitly
            val colorSpace = pdColor.colorSpace
            val components = pdColor.components
            
            when (colorSpace?.name) {
                "DeviceRGB" -> {
                    if (components != null && components.size >= 3) {
                        val r = (components[0] * 255).toInt().coerceIn(0, 255)
                        val g = (components[1] * 255).toInt().coerceIn(0, 255)
                        val b = (components[2] * 255).toInt().coerceIn(0, 255)
                        return String.format("#%02x%02x%02x", r, g, b)
                    }
                }
                "DeviceGray" -> {
                    if (components != null && components.size >= 1) {
                        val gray = (components[0] * 255).toInt().coerceIn(0, 255)
                        return String.format("#%02x%02x%02x", gray, gray, gray)
                    }
                }
                "DeviceCMYK" -> {
                    if (components != null && components.size >= 4) {
                        val c = components[0]
                        val m = components[1]
                        val y = components[2]
                        val k = components[3]
                        
                        // Convert CMYK to RGB
                        val r = ((1 - c) * (1 - k) * 255).toInt().coerceIn(0, 255)
                        val g = ((1 - m) * (1 - k) * 255).toInt().coerceIn(0, 255)
                        val b = ((1 - y) * (1 - k) * 255).toInt().coerceIn(0, 255)
                        
                        return String.format("#%02x%02x%02x", r, g, b)
                    }
                }
            }
            
            // Fallback to PDFBox's toRGB() method for other color spaces
            val rgb = pdColor.toRGB()
            val awtColor = Color(rgb)
            return String.format("#%06x", awtColor.rgb and 0xFFFFFF)
        } catch (e: IOException) {
            // If toRGB() fails, it's usually due to a complex or malformed color space.
            // Log the error so you know what's happening.
            println("WARN: Could not convert color space ${pdColor.colorSpace?.name}, defaulting to black. Error: ${e.message}")
            return "#000000" // Default to black
        }
    }
}
