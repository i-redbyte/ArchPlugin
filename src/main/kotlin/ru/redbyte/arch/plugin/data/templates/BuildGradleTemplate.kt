package ru.redbyte.arch.plugin.data.templates

import ru.redbyte.arch.plugin.data.utils.camelToSnakeCase


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
            .append("resourcePrefix 't_${params.feature.params.featureName.camelToSnakeCase()}_'")
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
