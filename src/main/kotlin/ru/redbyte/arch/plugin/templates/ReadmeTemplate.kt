package ru.redbyte.arch.plugin.templates

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