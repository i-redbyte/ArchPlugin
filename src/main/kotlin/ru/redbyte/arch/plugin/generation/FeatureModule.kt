package ru.redbyte.arch.plugin.generation

import com.intellij.openapi.fileTypes.PlainTextLanguage
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiFileFactory

abstract class FeatureModule {

    var javaDirectory: PsiDirectory? = null
    var rootDirectory: PsiDirectory? = null
    var mainDirectory: PsiDirectory? = null

    abstract fun PsiDirectory.createJavaDirectory(): PsiDirectory

    abstract fun PsiDirectory.createRootFeatureDirectory(): PsiDirectory

    abstract fun PsiDirectory.createPackageDirectories(): PsiDirectory

    open fun createModuleStructure(directory: PsiDirectory) {
        directory.createRootFeatureDirectory().also { rootDirectory = it }
            .createSubdirectory("src")
            .createSubdirectory("main").also { mainDirectory = it }
            .createSubdirectory("java")
            .createPackageDirectories()
            .createJavaDirectory().also { javaDirectory = it }
    }
}

fun PsiDirectory.addFile(name: String, content: String) {
    add(
        PsiFileFactory
            .getInstance(project)
            .createFileFromText(name, PlainTextLanguage.INSTANCE, content)
    )
}