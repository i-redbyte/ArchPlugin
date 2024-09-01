package ru.redbyte.arch.plugin.generation.builder

import com.intellij.psi.PsiDirectory

class DIPackageCreator(
    private val javaDirectory: PsiDirectory?,
    private val withDIFiles: Boolean
) : ArtifactCreator {
    override fun create() {
        if (withDIFiles) {
            javaDirectory?.createSubdirectory("di")
        }
    }
}
