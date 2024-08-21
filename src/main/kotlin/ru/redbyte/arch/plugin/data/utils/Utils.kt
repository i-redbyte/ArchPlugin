package ru.redbyte.arch.plugin.data.utils

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.psi.PsiManager
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
    val directories = psiProjectBaseDir.subdirectories
        .filter { dir -> !isSystemDirectory(dir.name) }
        .map { it.name }
        .toMutableList()
    if (directories.remove("features")) {
        directories.add(0, "features")
    }
    return directories
}