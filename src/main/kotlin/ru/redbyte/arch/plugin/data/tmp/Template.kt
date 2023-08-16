package ru.redbyte.arch.plugin.data.tmp

interface Template<T : TemplateParams> {

    fun generate(params: T): String
}

sealed interface TemplateParams

object NoParams : TemplateParams

class ManifestParams(
    val lowerCaseFeatureName: String
) : TemplateParams