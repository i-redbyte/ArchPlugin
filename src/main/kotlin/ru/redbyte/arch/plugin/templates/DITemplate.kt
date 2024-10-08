package ru.redbyte.arch.plugin.templates

import ru.redbyte.arch.plugin.utils.*

class DITemplate : Template<DIParams> {

    override fun generate(params: DIParams): String {

        val importList = mutableListOf(
            "$IMPORT $DAGGER.$MODULE",
            "$IMPORT $DAGGER.$PROVIDES",
            "$IMPORT $DAGGER.$HILT.InstallIn",
            "$IMPORT $DAGGER.$HILT.android.components.$VIEW_MODEL_COMPONENT"
        )


        return """
$PACKAGE ${params.packageName}.${params.lowerCaseFeatureName}.di

${importList.sortedImports().joinToString("\n")}

@$MODULE
@InstallIn($VIEW_MODEL_COMPONENT::$CLASS)
$INTERNAL $CLASS ${params.camelCaseFeatureName}$MODULE {

$TAB@$PROVIDES
$TAB$FUN provideYoursDependency(
$TAB){
$TAB${TAB}TODO("provide yours dependency for ${params.lowerCaseFeatureName}")
$TAB}
    
}
        """.trimIndent()
    }

}
