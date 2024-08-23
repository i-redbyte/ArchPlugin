plugins {
    id("org.jetbrains.intellij") version "1.3.1"
    id("org.jetbrains.kotlin.jvm") version "1.6.10"
}

group = "ru.redbyte.arch"
version = "1.0.0"

repositories {
    mavenCentral()
    gradlePluginPortal()
}

intellij {
    version.set("193.6911.18")
    plugins.set(listOf("android", "gradle"))
    type.set("IC")
}

tasks {
    patchPluginXml {
        sinceBuild.set("193")
        untilBuild.set("")
    }
    buildSearchableOptions {
        enabled = false
    }

}
