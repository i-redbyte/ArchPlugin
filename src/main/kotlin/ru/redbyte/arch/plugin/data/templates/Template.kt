package ru.redbyte.arch.plugin.data.templates

import ru.redbyte.arch.plugin.domain.Feature

interface Template<T : TemplateParams> {

    fun generate(params: T): String
}

sealed interface TemplateParams

object NoParams : TemplateParams

class ManifestParams(
    val lowerCaseFeatureName: String
) : TemplateParams

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

class ScreenParams(
    val lowerCaseFeatureName: String,
    val camelCaseFeatureName: String,
    val snakeCaseFeatureName: String,
    val createDi: Boolean,
    val withSetupBackNavigation: Boolean //TODO: extract to setup window
) : TemplateParams

class EventParams(
    val lowerCaseFeatureName: String,
    val camelCaseFeatureName: String
) : TemplateParams

class StateParams(
    val lowerCaseFeatureName: String,
    val camelCaseFeatureName: String
) : TemplateParams

class ReducerParams(
    val lowerCaseFeatureName: String,
    val camelCaseFeatureName: String
) : TemplateParams