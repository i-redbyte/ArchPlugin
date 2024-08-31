package ru.redbyte.arch.plugin.utils

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.psi.PsiManager
import ru.redbyte.arch.plugin.templates.ContractParams
import java.io.File

fun findVirtualFile(path: String) =
    LocalFileSystem.getInstance().findFileByIoFile(
        File(path)
    )

fun List<String>.sortedImports(): List<String> {
    return sorted().sortedBy { it.contains("javax") }
}

fun loadTopLevelDirectories(project: Project): List<String> {
    fun isSystemDirectory(name: String): Boolean {
        val systemDirs = listOf(".idea", "build", "gradle", "out")
        return name in systemDirs || name.startsWith(".")
    }

    val projectBaseDir = LocalFileSystem
        .getInstance()
        .findFileByPath(
            project.basePath ?: return emptyList()
        )
    val psiProjectBaseDir = projectBaseDir
        ?.let {
            PsiManager
                .getInstance(project)
                .findDirectory(it)
        } ?: return emptyList()

    return psiProjectBaseDir.subdirectories
        .filter { dir -> !isSystemDirectory(dir.name) }
        .map { it.name }
        .sortedWith(compareBy<String> { it != "feature" }.thenBy { it })
}

fun generateImports(params: ContractParams, importList: MutableList<String>) {
    if (!params.withState) importList.add("$IMPORT ${params.packageName}.presentation.base.ViewState")
    if (!params.withActions) importList.add("$IMPORT ${params.packageName}.presentation.base.ViewEvent")
    if (!params.withEffect) importList.add("$IMPORT ${params.packageName}.presentation.base.ViewEffect")
}