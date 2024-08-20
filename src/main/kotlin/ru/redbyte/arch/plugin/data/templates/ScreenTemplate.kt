package ru.redbyte.arch.plugin.data.templates

import ru.redbyte.arch.plugin.data.utils.*

class ScreenTemplate : Template<ScreenParams> {
    override fun generate(params: ScreenParams): String {
        val importList = mutableListOf(
            "$IMPORT android.os.Bundle",
            "$IMPORT androidx.fragment.app.Fragment",
            "$IMPORT android.content.Context",
            "$IMPORT android.view.View",
            "$IMPORT $PACKAGE_PREFIX.${params.lowerCaseFeatureName}.R", // TODO: 23.08.2023 add other dependencies
        )
        with(importList) {
            if (params.createDi) {
                // TODO: 23.08.2023 add di  dependencies
                //add("import ru.redbyte.arch.${params.lowerCaseFeatureName}.di.${params.camelCaseFeatureName}Container")
            }
            if (params.withSetupBackNavigation) {
                add("$IMPORT androidx.activity.OnBackPressedCallback")
            }
        }

        return "$PACKAGE $PACKAGE_PREFIX.${params.lowerCaseFeatureName}.presentation.ui\n\n" +
                importList.sortedImports().joinToString("\n") +
                "\n\n" +
                "$INTERNAL $CLASS ${params.camelCaseFeatureName}Fragment : Fragment(R.layout.t_${params.snakeCaseFeatureName}_fragment) {\n" +
                // TODO: 24.08.2023 add viewModel init
//                "\n" +
//                "    private val viewModel: ${params.camelCaseFeatureName}ViewModel\n" +
                "\n" +
                "$TAB$OVERRIDE $FUN onViewCreated(view: View, savedInstanceState: Bundle?) {\n" +
                "$TAB${TAB}super.onViewCreated(view, savedInstanceState)\n" +
                "$TAB${TAB}setupView(view)\n" +
                if (params.withSetupBackNavigation) "$TAB${TAB}setupBackNavigation()\n" else {
                    ""
                } +
                "$TAB}\n" +
                "\n" +
                makeSetupBackNavigation(params.withSetupBackNavigation) +
                "\n" +
                "$TAB$OVERRIDE $FUN setupView(view: View) {\n" +
                "$TAB$TAB//TODO: add setup views\n" +
                "$TAB}\n" +
                "\n" +
                "}"
    }

    private fun makeSetupBackNavigation(withSetupBackNavigation: Boolean): String {
        if (!withSetupBackNavigation) return ""
        return "$TAB$PRIVATE $FUN setupBackNavigation() {\n" +
                "$TAB${TAB}requireActivity().onBackPressedDispatcher.addCallback(\n" +
                "$TAB$TAB${TAB}viewLifecycleOwner,\n" +
                "$TAB$TAB${TAB}object : OnBackPressedCallback(true) {\n" +
                "$TAB$TAB$TAB$TAB$OVERRIDE $FUN handleOnBackPressed() {\n" +
                "$TAB$TAB$TAB$TAB$TAB//TODO: viewModel.accept()\n" +
                "$TAB$TAB$TAB$TAB}\n"+
                "$TAB$TAB$TAB}\n"+
                "$TAB$TAB)\n"+
                "$TAB}\n"
    }
}
