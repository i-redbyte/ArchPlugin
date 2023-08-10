package ru.redbyte.arch.plugin

import com.android.tools.idea.gradle.actions.SyncProjectAction
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import ru.redbyte.arch.plugin.presentation.FeatureDialog

class NewFeatureAction : AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        e.project?.let {
            if (FeatureDialog(it).showAndGet()) {
                SyncProjectAction().actionPerformed(e)
            }
        }
    }
}