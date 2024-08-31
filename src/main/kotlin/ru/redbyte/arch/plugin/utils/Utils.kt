package ru.redbyte.arch.plugin.utils

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.psi.PsiManager
import ru.redbyte.arch.plugin.generation.models.FeatureContract
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

fun fillImportsByContract(
    contract: FeatureContract,
    packageName: String,
    importList: MutableList<String>,
    reverseCondition: Boolean = false
) {
    if (contract.withState != reverseCondition) importList.add("$IMPORT ${packageName}.presentation.base.ViewState")
    if (contract.withActions != reverseCondition) importList.add("$IMPORT ${packageName}.presentation.base.ViewEvent")
    if (contract.withEffect != reverseCondition) importList.add("$IMPORT ${packageName}.presentation.base.ViewEffect")
}