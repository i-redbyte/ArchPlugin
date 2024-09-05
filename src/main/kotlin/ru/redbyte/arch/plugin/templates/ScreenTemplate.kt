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
            return if (withState) "state = ${params.camelCaseFeatureName}State(\"\")," else ""
        }

        fun generatePreviewActions(withActions: Boolean): String {
            return if (withActions) "eventHandler = {}" else ""
        }

        val importList = mutableListOf(
            "$IMPORT $ANDROIDX_COMPOSE.runtime.$COMPOSABLE",
            "$IMPORT $ANDROIDX_COMPOSE.runtime.collectAsState",
            "$IMPORT $ANDROIDX_COMPOSE.foundation.layout.Column",
            "$IMPORT $ANDROIDX_COMPOSE.foundation.layout.fillMaxSize",
            "$IMPORT $ANDROIDX_COMPOSE.foundation.layout.padding",
            "$IMPORT $ANDROIDX_COMPOSE.ui.Modifier",
            "$IMPORT $ANDROIDX_COMPOSE.ui.unit.dp",
            "$IMPORT $ANDROIDX.$HILT.navigation.compose.hiltViewModel",
            "$IMPORT $PROJECT_THEMED_PREVIEW",
            "$IMPORT $PROJECT_THEME",
            "$IMPORT ${params.packageName}.${params.lowerCaseFeatureName}.R"
        )

        fillImportsByContract(params.contract, params.packageName, importList, true)

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
$TAB${generateStateParam(params.contract.withState)}
$TAB${generateActionsParam(params.contract.withActions)}
) { 
${TAB}Column(
${TAB}${TAB}modifier = Modifier
${TAB}${TAB}${TAB}.fillMaxSize()
${TAB}${TAB}${TAB}.padding(16.dp)
${TAB}){
${TAB}${TAB}TODO("Make your screen")
${TAB}}
}

@ThemedPreview
@$COMPOSABLE
$INTERNAL $FUN Preview${params.camelCaseFeatureName}Content() {
${TAB}OtpTheme {
$TAB$TAB${params.camelCaseFeatureName}Content(
$TAB$TAB$TAB${generatePreviewState(params.contract.withState)}
$TAB$TAB$TAB${generatePreviewActions(params.contract.withActions)}
$TAB$TAB)
$TAB}
}
        """.trimIndent()
    }

}
