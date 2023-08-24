package ru.redbyte.arch.plugin.data.tmp

import ru.redbyte.arch.plugin.data.*

class StateTemplate : Template<StateParams> {
    override fun generate(params: StateParams): String {
        val importList = mutableListOf(
            "$IMPORT $PACKAGE_PREFIX.architecture.contract.State"
        )
        val name = params.camelCaseFeatureName
        return "$PACKAGE $PACKAGE_PREFIX.${params.lowerCaseFeatureName}.presentation.reducer\n\n" +
                importList.sortedImports().joinToString("\n") +
                "\n\n" +
                "$INTERNAL $CLASS ${name}State : State() {\n" +
                "\n" +
                "$TAB$COMPANION_OBJECT {\n" +
                "$TAB$TAB$FUN initialState(): ${name}State = ${name}State()\n" +
                "$TAB}\n"+
                "}"
    }
}