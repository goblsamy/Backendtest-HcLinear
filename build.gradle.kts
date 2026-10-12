plugins {
    java
    id("org.springframework.boot") version "3.2.0"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "com.hclinear"
version = "0.0.1-SNAPSHOT"
description = "Spring Boot backend coding challenge for HC Linear onboarding."

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

repositories {
	mavenCentral()
}

dependencies {
    val lombokVersion = "1.18.34"
    val mapstructVersion = "1.5.5.Final"
    val postgresqlVersion = "42.7.7"
    val flywayCoreVersion = "9.16.3"
    val bindingVersion = "0.2.0"

    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.mapstruct:mapstruct:${mapstructVersion}")
    implementation("org.projectlombok:lombok:${lombokVersion}")
    implementation("org.flywaydb:flyway-core:${flywayCoreVersion}")

    runtimeOnly("org.postgresql:postgresql:${postgresqlVersion}")


    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    annotationProcessor("org.mapstruct:mapstruct-processor:$mapstructVersion")
    annotationProcessor("org.projectlombok:lombok-mapstruct-binding:$bindingVersion")
    annotationProcessor("org.projectlombok:lombok:$lombokVersion")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
