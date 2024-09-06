plugins {
    id("org.jetbrains.intellij") version "1.17.4"
    id("org.jetbrains.kotlin.jvm") version "1.9.10"
}

group = "ru.redbyte.arch"
version = "1.1.0"

repositories {
    mavenCentral()
    gradlePluginPortal()
}

intellij {
    version.set("2023.1")
    plugins.set(listOf("android", "gradle"))
    type.set("IC")
}

tasks {
    patchPluginXml {
        sinceBuild.set("231")
        untilBuild.set("")
    }
    buildSearchableOptions {
        enabled = false
    }

}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = "17"
    }
}