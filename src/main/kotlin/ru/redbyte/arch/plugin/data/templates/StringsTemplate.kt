package ru.redbyte.arch.plugin.data.templates

class StringsTemplate {
    fun generate(params: StringsParams): String {
        return """
            <resources>
                <string name="${params.lowerCaseFeatureName}_example">Example String for ${params.packageName}</string>
            </resources>
        """.trimIndent()
    }
}