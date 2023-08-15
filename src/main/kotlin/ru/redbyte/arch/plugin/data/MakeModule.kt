package ru.redbyte.arch.plugin.data

import com.intellij.psi.PsiDirectory
import ru.redbyte.arch.plugin.domain.Feature

class MakeModule(private val feature: Feature) : Module() {

    private val names = NamesBuilder().build(feature.featureName)

    override fun PsiDirectory.createJavaDirectory(): PsiDirectory {
        return createSubdirectory(names.lowerCaseModuleName)
    }

    override fun PsiDirectory.createRootFeatureDirectory(): PsiDirectory {
        return createSubdirectory(names.moduleName)
    }

    override fun createModuleStructure(directory: PsiDirectory) {
        super.createModuleStructure(directory)
        // TODO: 15.08.2023 release create module
    }
}