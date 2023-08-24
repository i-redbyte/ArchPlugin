package ru.redbyte.arch.plugin.data.tmp

import ru.redbyte.arch.plugin.data.*

class EventTemplate : Template<EventParams> {
    override fun generate(params: EventParams): String {
        val importList = mutableListOf(
            "$IMPORT $PACKAGE_PREFIX.architecture.contract.Event"
        )
        return "$PACKAGE $PACKAGE_PREFIX.${params.lowerCaseFeatureName}.presentation.reducer\n\n" +
                importList.sortedImports().joinToString("\n") +
                "\n\n" +
                "$INTERNAL $SEALED_CLASS ${params.camelCaseFeatureName}Event : Event() {\n"+
                "$TAB$OBJECT OnScreenOpened : ${params.camelCaseFeatureName}Event()\n"+
                "}"
    }
}