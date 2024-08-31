package ru.redbyte.arch.plugin.utils

private val snakeRegex = "-[a-zA-Z]".toRegex()
private val camelRegex = "(?<=[a-zA-Z])[A-Z]".toRegex()

fun String.snakeToCamelCase() =
    snakeRegex
        .replace(this) {
            it.value.replace("-", "")
                .toUpperCase()
        }
        .capitalize()

fun String.camelToSnakeCase() =
    camelRegex
        .replace(this) {
            "_${it.value}"
        }.replace("-","_")
        .toLowerCase()

class NamesBuilder {

    fun build(featureName: String): Names {
        val camelCaseName: String = featureName.snakeToCamelCase()

        val lowerCaseName = camelCaseName.toLowerCase()
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
