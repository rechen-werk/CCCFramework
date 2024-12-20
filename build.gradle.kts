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

    //stuff
    implementation("org.slf4j:slf4j-nop:2.0.9")
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))
    implementation(kotlin("script-runtime"))
}

kotlin {
    jvmToolchain(17)
}

tasks.compileKotlin {
    kotlinOptions {
        javaParameters = true
    }
}

