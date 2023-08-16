package ru.redbyte.arch.plugin.data.tmp

import ru.redbyte.arch.plugin.data.camelToSnakeCase
import ru.redbyte.arch.plugin.domain.Feature

class BuildGradleTemplate : Template<BuildGradleParams> {

    override fun generate(params: BuildGradleParams): String {
        return StringBuilder()
            .addGradleApplyFiles(params)
            .addNewLine()
            .addAndroid(params)
            .addNewLine()
            .addNewLine()
            .addDependencies(params)
            .toString()
    }

    private fun StringBuilder.addNewLine(): StringBuilder = append("\n")

    private fun StringBuilder.addGradleApplyFiles(params: BuildGradleParams): StringBuilder {
        if (params.applyFiles.isEmpty()) return this
        params.applyFiles.forEach { file ->
            append("apply from: rootProject.file($file)")
            addNewLine()
        }
        return this
    }
    private fun StringBuilder.addAndroid(params: BuildGradleParams): StringBuilder {
        return append("android {")
            .addNewLine()
            .append(params.gradleIndent)
            .append("resourcePrefix 't_${params.feature.featureName.camelToSnakeCase()}_'")
            .addNewLine()
            .append("}")
    }

    private fun StringBuilder.addDependencies(params: BuildGradleParams): StringBuilder {
        if (params.dependenciesProjects.isEmpty() &&
            params.dependenciesLibraries.isEmpty() &&
            params.annotationProcessors.isEmpty()
        ) return this

        return append("dependencies {")
            .addBlock(params.gradleIndent, params.dependenciesProjects)
            .addNewLine()
            .addBlock(params.gradleIndent, params.dependenciesLibraries)
            .addBlock(params.gradleIndent, params.annotationProcessors)
            .addNewLine()
            .append("}")
    }

    private fun StringBuilder.addBlock(prefix: String, block: List<String>): StringBuilder {
        block.forEach { text ->
            addNewLine()
            append(prefix)
            append(text)
        }
        return this
    }
}

class BuildGradleParams(val feature: Feature) : TemplateParams {

    val gradleIndent = "    "

    private val defaultApplyFiles: MutableList<String> = mutableListOf(
        "\"gradle/plugin/android.gradle\"",
        "\"gradle/plugin/kotlin-library.gradle\""
    )
    private val defaultProjects: MutableList<String> = mutableListOf(
        "implementation project(':common:lokalise')",
        "implementation project(':common:models')"
    )

    private val defaultLibraries: MutableList<String> = mutableListOf(
        "implementation kotlinLibs.kotlinxCoroutinesAndroid",
        "implementation kotlinLibs.kotlinxCoroutinesRuntimeKtx"
    )

    private val defaultAnnotationProcessors: MutableList<String> = mutableListOf()

    val applyFiles: List<String> = defaultApplyFiles
    val dependenciesProjects: List<String> = defaultProjects
    val dependenciesLibraries: List<String> = defaultLibraries
    val annotationProcessors: List<String> = defaultAnnotationProcessors

}
