package ru.redbyte.arch.plugin.data.templates

import ru.redbyte.arch.plugin.data.utils.*

class ContractTemplate : Template<ContractParams> {
    override fun generate(params: ContractParams): String {
        val importList = mutableListOf<String>()
        if (params.withState) importList.add("$IMPORT ${params.packageName}.presentation.base.ViewState")
        if (params.withActions) importList.add("$IMPORT ${params.packageName}.presentation.base.ViewEvent")
        if (params.withEffect) importList.add("$IMPORT ${params.packageName}.presentation.base.ViewEffect")

        return """
$PACKAGE ${params.packageName}.${params.lowerCaseFeatureName}.presentation        

${importList.sortedImports().joinToString("\n")}   
 
${makeStateClass(params)}${makeActionsClass(params)}${makeEffectClass(params)}
            """.trimIndent()

    }

    private fun makeStateClass(params: ContractParams): String {
        if (!params.withState) return ""
        return """
$INTERNAL $DATA_CLASS ${params.camelCaseFeatureName}State(  

) : ViewState


        """.trimIndent()
    }

    private fun makeActionsClass(params: ContractParams): String {
        if (!params.withActions) return ""
        return """
$INTERNAL $SEALED_CLASS ${params.camelCaseFeatureName}Actions : ViewEvent {  

}


        """.trimIndent()
    }

    private fun makeEffectClass(params: ContractParams): String {
        if (!params.withEffect) return ""
        return """
$INTERNAL $SEALED_CLASS ${params.camelCaseFeatureName}Effect : ViewEffect {  

}

        """.trimIndent()
    }

}