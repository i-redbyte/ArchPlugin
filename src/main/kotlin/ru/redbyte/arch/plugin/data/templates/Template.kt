package ru.redbyte.arch.plugin.data.templates

import ru.redbyte.arch.plugin.domain.Feature

interface Template<T : TemplateParams> {
    fun generate(params: T): String
}

sealed interface TemplateParams

object NoParams : TemplateParams

abstract class BaseFeatureParams(
    val packageName: String,
    val lowerCaseFeatureName: String,
    val camelCaseFeatureName: String? = null,
    val snakeCaseFeatureName: String? = null
) : TemplateParams

class ManifestParams private constructor(
    packageName: String,
    lowerCaseFeatureName: String
) : BaseFeatureParams(packageName, lowerCaseFeatureName) {

    companion object {
        fun build(init: ManifestParamsBuilder.() -> Unit): ManifestParams {
            return ManifestParamsBuilder().apply(init).build()
        }
    }

    class ManifestParamsBuilder {
        lateinit var packageName: String
        lateinit var lowerCaseFeatureName: String

        fun build() = ManifestParams(packageName, lowerCaseFeatureName)
    }
}

class BuildGradleParams private constructor(
    val feature: Feature,
    packageName: String,
    lowerCaseFeatureName: String
) : BaseFeatureParams(packageName, lowerCaseFeatureName) {

    val gradleIndent = "    "

    val applyFiles: List<String> = listOf(
        "\"gradle/plugin/android.gradle\"",
        "\"gradle/plugin/kotlin-library.gradle\""
    )
    val dependenciesProjects: List<String> = listOf(
        "implementation project(':common:lokalise')",
        "implementation project(':common:models')"
    )
    val dependenciesLibraries: List<String> = listOf(
        "implementation kotlinLibs.kotlinxCoroutinesAndroid",
        "implementation kotlinLibs.kotlinxCoroutinesRuntimeKtx"
    )
    val annotationProcessors: List<String> = emptyList<String>()

    companion object {
        fun build(init: BuildGradleParamsBuilder.() -> Unit): BuildGradleParams {
            return BuildGradleParamsBuilder().apply(init).build()
        }
    }

    class BuildGradleParamsBuilder {
        lateinit var feature: Feature
        lateinit var packageName: String
        lateinit var lowerCaseFeatureName: String

        fun build() = BuildGradleParams(feature, packageName, lowerCaseFeatureName)
    }
}

class ScreenParams private constructor(
    packageName: String,
    lowerCaseFeatureName: String,
    camelCaseFeatureName: String,
    snakeCaseFeatureName: String,
    val createDi: Boolean,
    val withSetupBackNavigation: Boolean
) : BaseFeatureParams(packageName, lowerCaseFeatureName, camelCaseFeatureName, snakeCaseFeatureName) {

    companion object {
        fun build(init: ScreenParamsBuilder.() -> Unit): ScreenParams {
            return ScreenParamsBuilder().apply(init).build()
        }
    }

    class ScreenParamsBuilder {
        lateinit var packageName: String
        lateinit var lowerCaseFeatureName: String
        lateinit var camelCaseFeatureName: String
        lateinit var snakeCaseFeatureName: String
        var createDi: Boolean = false
        var withSetupBackNavigation: Boolean = false

        fun build() = ScreenParams(
            packageName,
            lowerCaseFeatureName,
            camelCaseFeatureName,
            snakeCaseFeatureName,
            createDi,
            withSetupBackNavigation
        )
    }
}

class EventParams private constructor(
    packageName: String,
    lowerCaseFeatureName: String,
    camelCaseFeatureName: String
) : BaseFeatureParams(packageName, lowerCaseFeatureName, camelCaseFeatureName) {

    companion object {
        fun build(init: EventParamsBuilder.() -> Unit): EventParams {
            return EventParamsBuilder().apply(init).build()
        }
    }

    class EventParamsBuilder {
        lateinit var packageName: String
        lateinit var lowerCaseFeatureName: String
        lateinit var camelCaseFeatureName: String

        fun build() = EventParams(packageName, lowerCaseFeatureName, camelCaseFeatureName)
    }
}

class StateParams private constructor(
    packageName: String,
    lowerCaseFeatureName: String,
    camelCaseFeatureName: String
) : BaseFeatureParams(packageName, lowerCaseFeatureName, camelCaseFeatureName) {

    companion object {
        fun build(init: StateParamsBuilder.() -> Unit): StateParams {
            return StateParamsBuilder().apply(init).build()
        }
    }

    class StateParamsBuilder {
        lateinit var packageName: String
        lateinit var lowerCaseFeatureName: String
        lateinit var camelCaseFeatureName: String

        fun build() = StateParams(packageName, lowerCaseFeatureName, camelCaseFeatureName)
    }
}

class ReducerParams private constructor(
    packageName: String,
    lowerCaseFeatureName: String,
    camelCaseFeatureName: String
) : BaseFeatureParams(packageName, lowerCaseFeatureName, camelCaseFeatureName) {

    companion object {
        fun build(init: ReducerParamsBuilder.() -> Unit): ReducerParams {
            return ReducerParamsBuilder().apply(init).build()
        }
    }

    class ReducerParamsBuilder {
        lateinit var packageName: String
        lateinit var lowerCaseFeatureName: String
        lateinit var camelCaseFeatureName: String

        fun build() = ReducerParams(packageName, lowerCaseFeatureName, camelCaseFeatureName)
    }
}
