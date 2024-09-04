package ru.redbyte.arch.plugin.templates

import ru.redbyte.arch.plugin.utils.*

class ContractTemplate : Template<ContractParams> {
    override fun generate(params: ContractParams): String {
        val importList = mutableListOf<String>()
        fillImportsByContract(params.contract, params.packageName, importList)
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
${TAB}val name: String
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