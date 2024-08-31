package ru.redbyte.arch.plugin.utils

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import java.io.File

fun getPackageName(project: Project): String {
    val projectBasePath = project.basePath
        ?: throw IllegalArgumentException("[getPackageName] project.basePath is null")

    val projectBaseDir = LocalFileSystem.getInstance().findFileByPath(projectBasePath)
        ?: throw IllegalStateException("[getPackageName] Unable to find project base directory at $projectBasePath")

    val packageNameInBuildFile = findPackageNameInBuildFile(projectBaseDir)
    if (packageNameInBuildFile != null) {
        return packageNameInBuildFile
    }

    return findTopLevelPackageName(projectBaseDir)
        ?: throw IllegalStateException("[getPackageName] No package name found in project")
}

private fun findPackageNameInBuildFile(projectBaseDir: VirtualFile): String? {
    val buildFilePaths = listOf(
        "${projectBaseDir.path}/app/build.gradle.kts",
        "${projectBaseDir.path}/app/build.gradle"
    )

    val buildFile = buildFilePaths.map(::File).firstOrNull { it.exists() }
        ?: return null

    val buildFileContent = buildFile.readText()

    val namespaceRegex = Regex("""namespace\s*=\s*["'](.+?)["']""")
    val matchResult = namespaceRegex.find(buildFileContent)

    return matchResult?.groups?.get(1)?.value
}

private fun findTopLevelPackageName(projectBaseDir: VirtualFile): String? {
    val sourceDirectories = listOf(
        "${projectBaseDir.path}/app/src/main/java",
        "${projectBaseDir.path}/app/src/main/kotlin"
    )

    return sourceDirectories.asSequence()
        .map(::File)
        .filter { it.exists() }
        .mapNotNull { findTopLevelPackageName(it) }
        .firstOrNull()
}

private fun findTopLevelPackageName(directory: File): String? {
    directory.walkTopDown().forEach { file ->
        if (file.isFile && (file.extension == "java" || file.extension == "kt")) {
            val packageName = extractPackageNameFromFile(file)
            if (packageName != null) {
                return extractTopLevelPackageName(packageName)
            }
        }
    }
    return null
}

private fun extractTopLevelPackageName(packageName: String): String {
    return packageName.split('.').take(3).joinToString(".")
}

private fun extractPackageNameFromFile(file: File): String? {
    val packageRegex = Regex("""^\s*package\s+([\w.]+)""")
    file.useLines { lines ->
        for (line in lines) {
            val matchResult = packageRegex.find(line)
            if (matchResult != null) {
                return matchResult.groups[1]?.value
            }
        }
    }
    return null
}

