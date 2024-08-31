package ru.redbyte.arch.plugin.templates

import ru.redbyte.arch.plugin.utils.*

class ContractTemplate : Template<ContractParams> {
    override fun generate(params: ContractParams): String {
        val (withState, withActions, withEffect) = params.contract
        val importList = mutableListOf<String>()
        if (withState) importList.add("$IMPORT ${params.packageName}.presentation.base.ViewState")
        if (withActions) importList.add("$IMPORT ${params.packageName}.presentation.base.ViewEvent")
        if (withEffect) importList.add("$IMPORT ${params.packageName}.presentation.base.ViewEffect")

        return """
$PACKAGE ${params.packageName}.${params.lowerCaseFeatureName}.presentation        

${importList.sortedImports().joinToString("\n")}   
 
${makeStateClass(params)}${makeActionsClass(params)}${makeEffectClass(params)}
            """.trimIndent()

    }

    private fun makeStateClass(params: ContractParams): String {
        if (!params.contract.withState) return ""
        return """
$INTERNAL $DATA_CLASS ${params.camelCaseFeatureName}State(  

) : ViewState


        """.trimIndent()
    }

    private fun makeActionsClass(params: ContractParams): String {
        if (!params.contract.withActions) return ""
        return """
$INTERNAL $SEALED_CLASS ${params.camelCaseFeatureName}Actions : ViewEvent {  

}


        """.trimIndent()
    }

    private fun makeEffectClass(params: ContractParams): String {
        if (!params.contract.withEffect) return ""
        return """
$INTERNAL $SEALED_CLASS ${params.camelCaseFeatureName}Effect : ViewEffect {  

}

        """.trimIndent()
    }

}