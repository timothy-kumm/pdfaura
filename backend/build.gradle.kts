plugins {
	kotlin("jvm") version "1.9.25"
	kotlin("plugin.spring") version "1.9.25"
	id("org.springframework.boot") version "3.5.3"
	id("io.spring.dependency-management") version "1.1.7"
	kotlin("plugin.jpa") version "1.9.25"
}

group = "com.kumm"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}


dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.flywaydb:flyway-core")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.6.0")
	
	// PDF processing dependencies
	implementation("org.apache.pdfbox:pdfbox:3.0.3")
	implementation("org.apache.pdfbox:pdfbox-tools:3.0.3")
	implementation("org.apache.pdfbox:fontbox:3.0.3")
	
	// Database (H2 for development, replace with production DB in prod)
	runtimeOnly("com.h2database:h2")
	runtimeOnly("org.postgresql:postgresql")

	// JSONB support
	implementation("io.hypersistence:hypersistence-utils-hibernate-63:3.7.0")

	// Firebase Auth
	implementation("com.google.firebase:firebase-admin:9.2.0")
	implementation("org.springframework.boot:spring-boot-starter-security")
	
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}


allOpen {
	annotation("jakarta.persistence.Entity")
	annotation("jakarta.persistence.MappedSuperclass")
	annotation("jakarta.persistence.Embeddable")
}

tasks.withType<Test> {
	useJUnitPlatform()
}

// Configure Gradle for compatibility
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
	kotlinOptions {
		jvmTarget = "21"
		freeCompilerArgs += "-Xjsr305=strict"
	}
}

// Configure compiler options for better compatibility
tasks.withType<JavaCompile> {
	options.encoding = "UTF-8"
	options.isIncremental = true
}
