package ru.redbyte.arch.plugin.data

import com.intellij.openapi.fileTypes.PlainTextLanguage
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiFileFactory

abstract class Module {

    var javaDirectory: PsiDirectory? = null
    var rootDirectory: PsiDirectory? = null
    var mainDirectory: PsiDirectory? = null

    abstract fun PsiDirectory.createJavaDirectory(): PsiDirectory

    abstract fun PsiDirectory.createRootFeatureDirectory(): PsiDirectory

    open fun createModuleStructure(directory: PsiDirectory, packageName: String) {
        directory.createRootFeatureDirectory().also { rootDirectory = it }
            .createSubdirectory("src")
            .createSubdirectory("main").also { mainDirectory = it }
            .createSubdirectory("java")
            .createPackageDirectories(packageName)
            .createJavaDirectory().also { javaDirectory = it }
    }
}

private fun PsiDirectory.createPackageDirectories(packageName: String): PsiDirectory {
    var currentDirectory = this
    val packageParts = packageName.split(".")
    for (part in packageParts) {
        currentDirectory = currentDirectory.createSubdirectory(part)
    }
    return currentDirectory
}

fun PsiDirectory.addFile(name: String, content: String) {
    add(
        PsiFileFactory
            .getInstance(project)
            .createFileFromText(name, PlainTextLanguage.INSTANCE, content)
    )
}