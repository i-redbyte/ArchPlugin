package ru.redbyte.arch.plugin.data

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiManager
import ru.redbyte.arch.plugin.domain.Feature
import java.io.File

class FeatureCreator(val project: Project) {

    fun createModules(feature: Feature, targetDirectory: PsiDirectory) {
        ApplicationManager.getApplication().runWriteAction {
            with(targetDirectory) {
                MakeModule(feature).createModuleStructure(this)
            }
        }
    }
}
