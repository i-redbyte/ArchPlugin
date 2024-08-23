package ru.redbyte.arch.plugin.data.templates

import ru.redbyte.arch.plugin.data.utils.*

class ViewModelTemplate : Template<ViewModelParams> {
    override fun generate(params: ViewModelParams): String {
        val importList = mutableListOf<String>()
        if (!params.withState) importList.add("$IMPORT ${params.packageName}.presentation.base.ViewState")
        if (!params.withActions) importList.add("$IMPORT ${params.packageName}.presentation.base.ViewEvent")
        if (!params.withEffect) importList.add("$IMPORT ${params.packageName}.presentation.base.ViewEffect")
        importList.addAll(
            listOf(
                "$IMPORT javax.inject.Inject",
                "$IMPORT dagger.hilt.android.lifecycle.HiltViewModel",
                "$IMPORT com.noxx.navigator.navigator.Navigator",
                "$IMPORT androidx.lifecycle.viewModelScope",
            )
        )
        val viewState = if (params.withState) "${params.camelCaseFeatureName}State" else "ViewState"
        val viewActions = if (params.withActions) "${params.camelCaseFeatureName}Actions" else "ViewEvent"
        return """
$PACKAGE ${params.packageName}.${params.lowerCaseFeatureName}.presentation        

${importList.sortedImports().joinToString("\n")}   

@HiltViewModel
$INTERNAL $CLASS ${params.camelCaseFeatureName}ViewModel @Inject constructor(
    private val navigator: Navigator
) : BaseViewModel<${makeViewModelContent(params)}>() { 

    override fun setInitialState(): $viewState(){
        return $viewState()
    }
    
    override suspend fun handleEvents(event$viewActions) {
        when (event) {
           TODO("Add your events")
        }
    }
    
}
        """.trimIndent()
    }

    private fun makeViewModelContent(params: ViewModelParams): String {
        val state = if (params.withState) "${params.camelCaseFeatureName}State" else "ViewState"
        val actions = if (params.withActions) "${params.camelCaseFeatureName}Actions" else "ViewEvent"
        val effect = if (params.withEffect) "${params.camelCaseFeatureName}Effect" else "ViewEffect"
        return "$state, $actions, $effect"
    }
}