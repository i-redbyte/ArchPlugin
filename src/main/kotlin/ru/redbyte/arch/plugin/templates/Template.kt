package ru.redbyte.arch.plugin.templates

import ru.redbyte.arch.plugin.generation.models.FeatureContract
import kotlin.properties.Delegates

interface Template<T : TemplateParams> {
    fun generate(params: T): String
}

sealed interface TemplateParams

object NoParams : TemplateParams

abstract class BaseFeatureParams(
    val packageName: String,
    val lowerCaseFeatureName: String,
    val camelCaseFeatureName: String? = null
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
        var packageName: String by Delegates.notNull()
        var lowerCaseFeatureName: String by Delegates.notNull()

        fun build() = ManifestParams(packageName, lowerCaseFeatureName)
    }
}

class BuildGradleParams private constructor(
    packageName: String,
    lowerCaseFeatureName: String
) : BaseFeatureParams(packageName, lowerCaseFeatureName) {

    val gradleIndent = "    "

    val plugins: List<String> = listOf(
        "libs.plugins.android.library",
        "libs.plugins.kotlin.library",
        "libs.plugins.hilt",
        "libs.plugins.kotlin.compose",
    )
    val applyFiles: List<String> = listOf(
        "config.base",
        "config.compose",
        "config.navigation",
        "config.test",
    )
    val dependenciesProjects: List<String> = listOf(
        "implementation project(path: ':_core:data')",
        "implementation project(path: ':_core:domain')",
    )
    val dependenciesLibraries: List<String> = listOf(
        "implementation libs.hilt.android"
    )
    val annotationProcessors: List<String> = listOf(
        "ksp libs.hilt.androidCompiler"
    )

    companion object {
        fun build(init: BuildGradleParamsBuilder.() -> Unit): BuildGradleParams {
            return BuildGradleParamsBuilder().apply(init).build()
        }
    }

    class BuildGradleParamsBuilder {
        var packageName: String by Delegates.notNull()
        var lowerCaseFeatureName: String by Delegates.notNull()

        fun build() = BuildGradleParams(packageName, lowerCaseFeatureName)
    }
}

class ScreenParams private constructor(
    packageName: String,
    lowerCaseFeatureName: String,
    camelCaseFeatureName: String,
    val contract: FeatureContract
) : BaseFeatureParams(packageName, lowerCaseFeatureName, camelCaseFeatureName) {

    companion object {
        fun build(init: ScreenParamsBuilder.() -> Unit): ScreenParams {
            return ScreenParamsBuilder().apply(init).build()
        }
    }

    class ScreenParamsBuilder {
        var packageName: String by Delegates.notNull()
        var lowerCaseFeatureName: String by Delegates.notNull()
        var camelCaseFeatureName: String by Delegates.notNull()
        var snakeCaseFeatureName: String by Delegates.notNull()
        var contract: FeatureContract by Delegates.notNull()
        fun build() = ScreenParams(
            packageName, lowerCaseFeatureName, camelCaseFeatureName,
            contract
        )
    }
}

class StringsParams private constructor(
    packageName: String,
    lowerCaseFeatureName: String
) : BaseFeatureParams(packageName, lowerCaseFeatureName) {
    companion object {
        fun build(init: StringsParamsBuilder.() -> Unit): StringsParams {
            return StringsParamsBuilder().apply(init).build()
        }
    }

    class StringsParamsBuilder {
        var packageName: String by Delegates.notNull()
        var lowerCaseFeatureName: String by Delegates.notNull()

        fun build() = StringsParams(packageName, lowerCaseFeatureName)
    }
}

class ContractParams private constructor(
    packageName: String,
    lowerCaseFeatureName: String,
    camelCaseFeatureName: String,
    val contract: FeatureContract
) : BaseFeatureParams(packageName, lowerCaseFeatureName, camelCaseFeatureName) {

    companion object {
        fun build(init: ContractParamsBuilder.() -> Unit): ContractParams {
            return ContractParamsBuilder().apply(init).build()
        }
    }

    class ContractParamsBuilder {
        var packageName: String by Delegates.notNull()
        var lowerCaseFeatureName: String by Delegates.notNull()
        var camelCaseFeatureName: String by Delegates.notNull()
        var contract: FeatureContract by Delegates.notNull()
        fun build() = ContractParams(
            packageName, lowerCaseFeatureName, camelCaseFeatureName,
            contract
        )
    }
}

class ViewModelParams private constructor(
    packageName: String,
    lowerCaseFeatureName: String,
    camelCaseFeatureName: String,
    val contract: FeatureContract
) : BaseFeatureParams(packageName, lowerCaseFeatureName, camelCaseFeatureName) {

    companion object {
        fun build(init: ViewModelBuilder.() -> Unit): ViewModelParams {
            return ViewModelBuilder().apply(init).build()
        }
    }

    class ViewModelBuilder {
        var packageName: String by Delegates.notNull()
        var lowerCaseFeatureName: String by Delegates.notNull()
        var camelCaseFeatureName: String by Delegates.notNull()
        var contract: FeatureContract by Delegates.notNull()
        fun build() = ViewModelParams(
            packageName, lowerCaseFeatureName, camelCaseFeatureName,
            contract
        )
    }
}


class DIParams private constructor(
    packageName: String,
    lowerCaseFeatureName: String
) : BaseFeatureParams(packageName, lowerCaseFeatureName) {
    companion object {
        fun build(init: DIParamsBuilder.() -> Unit): DIParams {
            return DIParamsBuilder().apply(init).build()
        }
    }

    class DIParamsBuilder {
        var packageName: String by Delegates.notNull()
        var lowerCaseFeatureName: String by Delegates.notNull()

        fun build() = DIParams(packageName, lowerCaseFeatureName)
    }
}
