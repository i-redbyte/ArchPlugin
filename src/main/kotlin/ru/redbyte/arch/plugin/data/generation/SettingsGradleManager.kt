package ru.redbyte.arch.plugin.data.generation

import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import java.io.File

class SettingsGradleManager(private val project: Project) {

    fun ensureModuleInSettings(directory: PsiDirectory, featureName: String) {
        val settingsFile = getSettingsFile() ?: return

        val moduleName = generateModuleName(directory, project.basePath ?: return, featureName)

        if (!isModuleIncluded(settingsFile, moduleName)) {
            insertModuleInAlphabeticalOrder(settingsFile, moduleName)
        }
    }

    private fun getSettingsFile(): PsiFile? {
        val basePath = project.basePath ?: return null
        val baseDir = LocalFileSystem.getInstance().findFileByPath(basePath) ?: return null
        val settingsFile = baseDir.findChild("settings.gradle.kts") ?: baseDir.findChild("settings.gradle")
        return settingsFile?.let { PsiManager.getInstance(project).findFile(it) }
    }

    private fun generateModuleName(directory: PsiDirectory, basePath: String, featureName: String): String {
        val relativePath = directory.virtualFile.path
            .removePrefix(basePath)
            .replace(File.separator, ":")
            .removePrefix(":")
        return ":$relativePath:${featureName.lowercase()}"
    }

    private fun isModuleIncluded(settingsFile: PsiFile, moduleName: String): Boolean {
        return settingsFile.text.contains(moduleName)
    }

    private fun insertModuleInAlphabeticalOrder(settingsFile: PsiFile, moduleName: String) {
        WriteCommandAction.runWriteCommandAction(project) {
            val document = settingsFile.viewProvider.document ?: return@runWriteCommandAction
            val lines = document.text.split("\n").toMutableList()
            val includeLines = lines.filter { it.startsWith("include") }
            val insertIndex = includeLines.indexOfFirst { it > "include '$moduleName'" }

            if (includeLines.isEmpty()) {
                document.insertString(document.textLength, "\ninclude '$moduleName'")
            } else if (insertIndex == -1) {
                val lastIncludeLine = lines.indexOf(includeLines.last())
                document.insertString(document.getLineEndOffset(lastIncludeLine), "\ninclude '$moduleName'")
            } else {
                val includeInsertLine = lines.indexOf(includeLines[insertIndex])
                document.insertString(document.getLineStartOffset(includeInsertLine), "include '$moduleName'\n")
            }
        }
    }
}
