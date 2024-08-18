package ru.redbyte.arch.plugin.data

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDirectory
import ru.redbyte.arch.plugin.domain.Feature

class FeatureCreator(val project: Project) {

    fun createModules(
        feature: Feature,
        targetDirectory: PsiDirectory
    ) {
        ApplicationManager.getApplication().runWriteAction {
            with(targetDirectory) {
                MakeModule(feature).createModuleStructure(this)
            }
        }
    }
}
