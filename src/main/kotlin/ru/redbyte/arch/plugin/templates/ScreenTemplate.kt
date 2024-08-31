package ru.redbyte.arch.plugin.templates

import ru.redbyte.arch.plugin.utils.*

class ScreenTemplate : Template<ScreenParams> {

    override fun generate(params: ScreenParams): String {
        fun generateStateParam(withState: Boolean): String {
            return if (withState) "state: ${params.camelCaseFeatureName}State," else ""
        }

        fun generateActionsParam(withActions: Boolean): String {
            return if (withActions) "eventHandler: (${params.camelCaseFeatureName}Actions) -> Unit" else ""
        }

        fun generatePreviewState(withState: Boolean): String {
            return if (withState) "state = ${params.camelCaseFeatureName}State()," else ""
        }

        fun generatePreviewActions(withActions: Boolean): String {
            return if (withActions) "eventHandler = {}" else ""
        }

        val importList = mutableListOf(
            "$IMPORT $ANDROIDX_COMPOSE.runtime.$COMPOSABLE",
            "$IMPORT $ANDROIDX_COMPOSE.foundation.layout.Column",
            "$IMPORT $ANDROIDX_COMPOSE.foundation.layout.fillMaxSize",
            "$IMPORT $ANDROIDX_COMPOSE.foundation.layout.padding",
            "$IMPORT $ANDROIDX_COMPOSE.ui.Modifier",
            "$IMPORT $ANDROIDX_COMPOSE.ui.tooling.preview.Preview",
            "$IMPORT $ANDROIDX_COMPOSE.ui.unit.dp",
            "$IMPORT ${params.packageName}.${params.lowerCaseFeatureName}.R"
        )

        if (!params.withState) importList.add("$IMPORT ${params.packageName}.presentation.base.ViewState")
        if (!params.withActions) importList.add("$IMPORT ${params.packageName}.presentation.base.ViewEvent")
        if (!params.withEffect) importList.add("$IMPORT ${params.packageName}.presentation.base.ViewEffect")

        return """
$PACKAGE ${params.packageName}.${params.lowerCaseFeatureName}.presentation

${importList.sortedImports().joinToString("\n")}

@$COMPOSABLE
$INTERNAL $FUN ${params.camelCaseFeatureName}Screen(viewModel: ${params.camelCaseFeatureName}ViewModel = hiltViewModel()) { 
    val state by viewModel.viewStateFlow.collectAsState()
    val eventHandler = viewModel::sendEvent
    ${params.camelCaseFeatureName}Content(state, eventHandler)
}
            
@$COMPOSABLE
$INTERNAL $FUN ${params.camelCaseFeatureName}Content(
    ${generateStateParam(params.withState)}
    ${generateActionsParam(params.withActions)}
) { 
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ){
       TODO("Make your screen")
    }
}

@ThemedPreview
@$COMPOSABLE
$INTERNAL $FUN Preview${params.camelCaseFeatureName}Content() {
    OtpTheme {
        ${params.camelCaseFeatureName}Content(
            ${generatePreviewState(params.withState)}
            ${generatePreviewActions(params.withActions)}
        )
    }
}
        """.trimIndent()
    }

}
