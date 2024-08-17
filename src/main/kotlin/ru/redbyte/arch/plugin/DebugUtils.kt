package ru.redbyte.arch.plugin

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages

fun showMessage(project: Project, title: String = "debug", msg: String) {
    Messages.showMessageDialog(
        project,
        msg,
        title,
        Messages.getInformationIcon()
    )
}
