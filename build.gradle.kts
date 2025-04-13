plugins {
    kotlin("jvm") version "1.8.0"
}

group = "eu.rechenwerk"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    //engine
    implementation("org.reflections:reflections:0.10.2")
    implementation("org.slf4j:slf4j-nop:2.0.9")
    implementation(kotlin("script-runtime"))
    implementation("org.springframework:spring-web:6.2.1")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.13.3")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.18.2")
    implementation("org.reactivestreams:reactive-streams:1.0.4")
}

kotlin {
    jvmToolchain(17)
}

tasks.compileKotlin {
    kotlinOptions {
        javaParameters = true
        jvmTarget = "17"
    }
}

