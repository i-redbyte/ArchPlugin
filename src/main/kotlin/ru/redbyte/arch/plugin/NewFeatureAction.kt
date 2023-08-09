package ru.redbyte.arch.plugin

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent

class NewFeatureAction : AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        e.project?.let {
            // TODO: 10.08.2023 Release: show feature dialog
        }
    }
}