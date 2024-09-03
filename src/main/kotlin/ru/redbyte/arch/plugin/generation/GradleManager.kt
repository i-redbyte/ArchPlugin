package ru.redbyte.arch.plugin.generation

import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import ru.redbyte.arch.plugin.utils.IMPLEMENTATION
import ru.redbyte.arch.plugin.utils.PROJECT
import ru.redbyte.arch.plugin.utils.TAB
import java.io.File

class GradleManager(private val project: Project) {

    fun ensureModuleInSettingsAndBuild(directory: PsiDirectory, featureName: String) {
        val settingsFile = getSettingsFile() ?: return
        val buildFile = getAppBuildFile() ?: return

        val moduleName = generateModuleName(directory, project.basePath ?: return, featureName)

        if (!isModuleIncluded(settingsFile, moduleName)) {
            insertModuleInAlphabeticalOrder(settingsFile, moduleName)
        }

        if (!isDependencyIncluded(buildFile, moduleName)) {
            insertDependencyInAlphabeticalOrder(buildFile, moduleName)
        }
    }

    private fun getSettingsFile(): PsiFile? {
        val basePath = project.basePath ?: return null
        val baseDir = LocalFileSystem.getInstance().findFileByPath(basePath) ?: return null
        val settingsFile = baseDir.findChild("settings.gradle.kts") ?: baseDir.findChild("settings.gradle")
        return settingsFile?.let { PsiManager.getInstance(project).findFile(it) }
    }

    private fun getAppBuildFile(): PsiFile? {
        val basePath = project.basePath ?: return null
        val appDir = LocalFileSystem.getInstance().findFileByPath("$basePath/app") ?: return null
        val buildFile = appDir.findChild("build.gradle.kts") ?: appDir.findChild("build.gradle")
        return buildFile?.let { PsiManager.getInstance(project).findFile(it) }
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

    private fun isDependencyIncluded(buildFile: PsiFile, moduleName: String): Boolean {
        val dependencyText = "implementation project(\":$moduleName\")"
        return buildFile.text.contains(dependencyText)
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

    private fun insertDependencyInAlphabeticalOrder(buildFile: PsiFile, moduleName: String) {
        WriteCommandAction.runWriteCommandAction(project) {
            val document = buildFile.viewProvider.document ?: return@runWriteCommandAction
            val lines = document.text.split("\n").toMutableList()
            val dependenciesStartIndex = lines.indexOfFirst { it.trim().startsWith("dependencies") }
            if (dependenciesStartIndex == -1) return@runWriteCommandAction

            val dependenciesEndIndex = lines.subList(dependenciesStartIndex, lines.size)
                .indexOfFirst { it.trim().startsWith("}") } + dependenciesStartIndex
            if (dependenciesEndIndex == -1) return@runWriteCommandAction

            val dependencyLines = lines.subList(dependenciesStartIndex + 1, dependenciesEndIndex)
                .filter { it.trim().startsWith("$IMPLEMENTATION $PROJECT") }

            val newDependency = "$TAB$IMPLEMENTATION $PROJECT(path: '$moduleName')"
            val insertIndex = dependencyLines.indexOfFirst { it > newDependency }

            if (dependencyLines.isEmpty()) {
                lines.add(dependenciesStartIndex + 1, newDependency)
            } else if (insertIndex == -1) {
                lines.add(dependenciesStartIndex + dependencyLines.size + 1, newDependency)
            } else {
                lines.add(dependenciesStartIndex + insertIndex + 1, newDependency)
            }

            document.setText(lines.joinToString("\n"))
        }
    }
}
