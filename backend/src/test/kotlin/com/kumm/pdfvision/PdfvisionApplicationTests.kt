package com.kumm.pdfvision

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("test")
class PdfvisionApplicationTests {

    @org.springframework.boot.test.context.TestConfiguration
    class TestConfig {
        @org.springframework.context.annotation.Bean
        @org.springframework.context.annotation.Primary
        fun firebaseApp(): com.google.firebase.FirebaseApp {
            return org.mockito.Mockito.mock(com.google.firebase.FirebaseApp::class.java)
        }
    }

	@Test
	fun contextLoads() {
	}

}
