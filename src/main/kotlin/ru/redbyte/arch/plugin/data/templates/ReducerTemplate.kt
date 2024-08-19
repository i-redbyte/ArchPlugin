package ru.redbyte.arch.plugin.data.templates

import ru.redbyte.arch.plugin.data.utils.*

class ReducerTemplate : Template<ReducerParams> {
    override fun generate(params: ReducerParams): String {
        val name = params.camelCaseFeatureName

        val importList = mutableListOf(
            "$IMPORT $PACKAGE_PREFIX.architecture.contract.Event",
            "$IMPORT $PACKAGE_PREFIX.architecture.contract.Update",
            "$IMPORT $PACKAGE_PREFIX.architecture.contract.StateReducer",
            "$IMPORT $PACKAGE_PREFIX.$name.presentation.reducer.${name}Event"
        )
        return "$PACKAGE $PACKAGE_PREFIX.${params.lowerCaseFeatureName}.presentation.reducer\n\n" +
                importList.sortedImports().joinToString("\n") +
                "\n\n" +
                "$INTERNAL $CLASS ${params.camelCaseFeatureName}Reducer : StateReducer<${name}State>() {\n" +
                "$TAB$OVERRIDE $FUN update(event: Event, state: ${name}State): Update<${name}State> {\n" +
                "$TAB${TAB}if (event !is ${name}Event) return Update.idle()\n" +
                "$TAB${TAB}when (event) {\n" +
                "$TAB$TAB$TAB${name}Event.OnScreenOpened -> {\n" +
                "$TAB$TAB$TAB${TAB}TODO(\"Release it\")\n" +
                "$TAB$TAB$TAB}\n" +
                "$TAB$TAB}\n" +
                "$TAB}\n" +
                "}"
    }
}