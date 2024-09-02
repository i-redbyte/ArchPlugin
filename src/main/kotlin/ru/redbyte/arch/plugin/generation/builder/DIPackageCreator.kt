package ru.redbyte.arch.plugin.generation.builder

import com.intellij.psi.PsiDirectory

class DIPackageCreator(
    private val javaDirectory: PsiDirectory?
) : ArtifactCreator {
    override fun create() {
        javaDirectory?.createSubdirectory("di")
    }
}
