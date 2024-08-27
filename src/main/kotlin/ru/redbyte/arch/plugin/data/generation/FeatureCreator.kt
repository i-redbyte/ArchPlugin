package ru.redbyte.arch.plugin.data.generation

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiManager
import ru.redbyte.arch.plugin.domain.Feature
import java.io.File
import java.io.IOException

class FeatureCreator(val project: Project) {

    fun createModules(
        feature: Feature,
        targetDirectory: PsiDirectory
    ) {
        ApplicationManager.getApplication().runWriteAction {
            with(targetDirectory) {
                MakeModule(feature).createModuleStructure(this)
                addModuleToSettingsGradle(feature, this)
            }
        }
    }

    private fun addModuleToSettingsGradle(feature: Feature, directory: PsiDirectory) {
        val basePath = project.basePath
        val baseDir = basePath?.let { LocalFileSystem.getInstance().findFileByPath(it) }
        val settingsFile = baseDir?.findChild("settings.gradle.kts") ?: baseDir?.findChild("settings.gradle")

        settingsFile?.let {
            val psiFile = PsiManager.getInstance(project).findFile(it)
            val relativePath = directory.virtualFile.path
                .removePrefix(basePath.toString())
                .replace(File.separator, ":")
                .removePrefix(":")
            val moduleName = ":$relativePath:${feature.params.featureName.lowercase()}"

            psiFile?.let { file ->
                val content = file.text
                if (!content.contains(moduleName)) {
                    WriteCommandAction.runWriteCommandAction(project) {
                        try {
                            val document = file.viewProvider.document
                            document?.let { doc ->
                                doc.insertString(doc.textLength, "\ninclude '$moduleName'")
                            }
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        }
    }
}
