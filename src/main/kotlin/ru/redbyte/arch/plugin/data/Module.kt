package ru.redbyte.arch.plugin.data

import com.intellij.openapi.fileTypes.PlainTextLanguage
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiFileFactory

abstract class Module {

    var javaDirectory: PsiDirectory? = null
    var rootDirectory: PsiDirectory? = null
    var mainDirectory: PsiDirectory? = null

    abstract fun PsiDirectory.createJavaDirectory(): PsiDirectory

    abstract fun PsiDirectory.createRootFeatureDirectory(): PsiDirectory

    open fun createModuleStructure(directory: PsiDirectory) {
        directory.createRootFeatureDirectory().also { rootDirectory = it }
            .createSubdirectory("src")
            .createSubdirectory("main").also { mainDirectory = it }
            .createSubdirectory("java")
            .createSubdirectory("ru")
            .createSubdirectory("redbyte")
            .createSubdirectory("arch")
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