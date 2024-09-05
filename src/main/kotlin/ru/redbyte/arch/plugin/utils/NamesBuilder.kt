package ru.redbyte.arch.plugin.utils

import java.util.*

private val snakeRegex = "-[a-zA-Z]".toRegex()
private val camelRegex = "(?<=[a-zA-Z])[A-Z]".toRegex()

fun String.snakeToCamelCase() =
    snakeRegex
        .replace(this) {
            it.value.replace("-", "")
                .uppercase(Locale.getDefault())
        }
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }

fun String.camelToSnakeCase() =
    camelRegex
        .replace(this) {
            "_${it.value}"
        }.replace("-","_")
        .lowercase(Locale.getDefault())

class NamesBuilder {

    fun build(featureName: String): Names {
        val camelCaseName: String = featureName.snakeToCamelCase()

        val lowerCaseName = camelCaseName.lowercase(Locale.getDefault())
        val snakeCaseName = featureName.replace("-", "_")

        return Names(
            camelCaseName = camelCaseName,
            snakeCaseName = snakeCaseName,
            lowerCaseModuleName = lowerCaseName,
            moduleName = featureName
        )
    }

    data class Names(
        val camelCaseName: String,
        val snakeCaseName: String,
        val lowerCaseModuleName: String,
        val moduleName: String
    )
}
