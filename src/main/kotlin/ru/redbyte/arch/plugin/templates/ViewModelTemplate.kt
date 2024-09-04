package ru.redbyte.arch.plugin.templates

import ru.redbyte.arch.plugin.utils.*

class ViewModelTemplate : Template<ViewModelParams> {
    override fun generate(params: ViewModelParams): String {
        val importList = mutableListOf<String>()
        fillImportsByContract(params.contract, params.packageName, importList, true)
        importList.addAll(
            listOf(
                "$IMPORT javax.inject.Inject",
                "$IMPORT dagger.hilt.android.lifecycle.HiltViewModel",
                "$IMPORT com.noxx.navigator.navigator.Navigator",
                "$IMPORT androidx.lifecycle.viewModelScope",
                "$IMPORT $PROJECT_BASE_VIEW_MODEL",
            )
        )
        val viewState = if (params.contract.withState) {
            """            
${params.camelCaseFeatureName}State {
${TAB}${TAB}return ${params.camelCaseFeatureName}State(
${TAB}${TAB}${TAB}name = ""
${TAB}${TAB})
${TAB}}
            """.trimIndent()
        } else {
            "object : ViewState {}"
        }
        val viewActions = if (params.contract.withActions) "${params.camelCaseFeatureName}Actions" else "ViewEvent"
        return """
$PACKAGE ${params.packageName}.${params.lowerCaseFeatureName}.presentation        

${importList.sortedImports().joinToString("\n")}   

@HiltViewModel
$INTERNAL $CLASS ${params.camelCaseFeatureName}ViewModel @Inject constructor(
${TAB}private val navigator: Navigator
) : BaseViewModel<${makeViewModelContent(params)}>(),
${TAB}Navigator by navigator { 

${TAB}override fun setInitialState(): $viewState 
    
${TAB}override suspend fun handleEvents(event: $viewActions) {
${TAB}${TAB}when (event) {
${TAB}${TAB}${TAB}else -> TODO("Add your events")
${TAB}${TAB}}
${TAB}}
    
}
        """.trimIndent()
    }

    private fun makeViewModelContent(params: ViewModelParams): String {
        val state = if (params.contract.withState) "${params.camelCaseFeatureName}State" else "ViewState"
        val actions = if (params.contract.withActions) "${params.camelCaseFeatureName}Actions" else "ViewEvent"
        val effect = if (params.contract.withEffect) "${params.camelCaseFeatureName}Effect" else "ViewEffect"
        return "$state, $actions, $effect"
    }
}