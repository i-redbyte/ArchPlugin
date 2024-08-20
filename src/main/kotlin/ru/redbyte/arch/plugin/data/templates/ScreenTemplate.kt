package ru.redbyte.arch.plugin.data.templates

import ru.redbyte.arch.plugin.data.utils.*

class ScreenTemplate : Template<ScreenParams> {
    override fun generate(params: ScreenParams): String {
        val importList = mutableListOf(
            "$IMPORT $ANDROIDX_COMPOSE.runtime.$COMPOSABLE",
            "$IMPORT $ANDROIDX_COMPOSE.foundation.layout.Column",
            "$IMPORT $ANDROIDX_COMPOSE.foundation.layout.fillMaxSize",
            "$IMPORT $ANDROIDX_COMPOSE.foundation.layout.padding",
            "$IMPORT $ANDROIDX_COMPOSE.ui.Modifier",
            "$IMPORT $ANDROIDX_COMPOSE.ui.unit.dp",
            "$IMPORT ${params.packageName}.${params.lowerCaseFeatureName}.R"
        )
        return """
$PACKAGE ${params.packageName}.${params.lowerCaseFeatureName}.presentation

${importList.sortedImports().joinToString("\n")}

@$COMPOSABLE
$INTERNAL $FUN ${params.camelCaseFeatureName}Screen() { 
    ${params.camelCaseFeatureName}Content()
}
            
@$COMPOSABLE
$INTERNAL $FUN ${params.camelCaseFeatureName}Content() { 
    Column(
        modifier = Modifier
             .fillMaxSize()
              padding(16.dp)
    ){
       TODO("Make your screen")
    }
}
        """.trimIndent()
    }
}
