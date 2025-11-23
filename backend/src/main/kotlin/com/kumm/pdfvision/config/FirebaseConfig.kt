package com.kumm.pdfvision.config

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.io.IOException

@Configuration
class FirebaseConfig {

    @Bean
    @Throws(IOException::class)
    fun firebaseApp(): FirebaseApp {
        // In a real environment, you would load the service account from a file
        // or environment variable (GOOGLE_APPLICATION_CREDENTIALS).
        // For now, if no credentials are provided, we'll try to use default application credentials
        // or just initialize without specific credentials if running in a context where they are available implicitly (like GCP).
        // However, local dev often needs a mock or a real key.
        // If the user hasn't provided a key, we might need to rely on the environment being set up.

        if (FirebaseApp.getApps().isEmpty()) {
             // Use Google Application Credentials if available, otherwise this might fail in local dev without setup
             val options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.getApplicationDefault())
                .setProjectId("pdfaura")
                .build()
            return FirebaseApp.initializeApp(options)
        }
        return FirebaseApp.getInstance()
    }
}
