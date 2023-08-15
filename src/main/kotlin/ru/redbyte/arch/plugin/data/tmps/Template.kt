package ru.redbyte.arch.plugin.data.tmps

interface Template<T : TemplateParams> {

    fun generate(params: T): String
}

sealed interface TemplateParams

object NoParams : TemplateParams

class ManifestParams(
    val lowerCaseFeatureName: String
) : TemplateParams