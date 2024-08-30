package ru.redbyte.arch.plugin.data.templates

class ReadmeTemplate : Template<NoParams> {

    override fun generate(params: NoParams): String {
        return """
## Название фичи

# Описание

Краткое описание

# Документация
        """.trimIndent()
    }
}