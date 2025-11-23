package com.kumm.pdfvision.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.info.Contact
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenApiConfig {

    @Bean
    fun customOpenAPI(): OpenAPI {
        return OpenAPI()
            .info(
                Info()
                    .title("PDF Aura API")
                    .version("1.0.0")
                    .description("API for PDF processing and management")
                    .contact(
                        Contact()
                            .name("PDFAura Team")
                            .email("support@pdfaura.com")
                    )
            )
    }
}