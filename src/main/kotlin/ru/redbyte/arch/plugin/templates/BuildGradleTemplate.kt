package ru.redbyte.arch.plugin.templates

class BuildGradleTemplate : Template<BuildGradleParams> {

    override fun generate(params: BuildGradleParams): String {
        return StringBuilder()
            .addPluginsAlias(params)
            .addNewLine()
            .addGradleApplyFiles(params)
            .addNewLine()
            .addAndroid(params)
            .addNewLine()
            .addNewLine()
            .addDependencies(params)
            .toString()
    }

    private fun StringBuilder.addPluginsAlias(params: BuildGradleParams): StringBuilder {
        if (params.plugins.isEmpty()) return this
        append("plugins {")
        addNewLine()
        params.plugins.forEach { file ->
            append("${params.gradleIndent}alias $file")
            addNewLine()
        }
        append("}")
        return this
    }

    private fun StringBuilder.addGradleApplyFiles(params: BuildGradleParams): StringBuilder {
        if (params.applyFiles.isEmpty()) return this
        append("apply {")
        addNewLine()
        params.applyFiles.forEach { file ->
            append("${params.gradleIndent}from($file)")
            addNewLine()
        }
        append("}")
        return this
    }

    private fun StringBuilder.addAndroid(params: BuildGradleParams): StringBuilder {
        return append("android {")
            .addNewLine()
            .append("${params.gradleIndent} namespace '${params.packageName}.${params.lowerCaseFeatureName}'")
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
