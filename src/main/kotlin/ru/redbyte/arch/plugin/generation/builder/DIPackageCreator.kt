package ru.redbyte.arch.plugin.generation.builder

import com.intellij.psi.PsiDirectory
import ru.redbyte.arch.plugin.generation.addFile
import ru.redbyte.arch.plugin.generation.models.FeatureMetadata
import ru.redbyte.arch.plugin.templates.DIParams
import ru.redbyte.arch.plugin.templates.DITemplate
import ru.redbyte.arch.plugin.utils.NamesBuilder

class DIPackageCreator(
    private val javaDirectory: PsiDirectory?,
    private val featureMetadata: FeatureMetadata,
    private val names: NamesBuilder.Names
) : ArtifactCreator {
    override fun create() {
        javaDirectory?.createSubdirectory("di")?.apply {
            addFile(
                "${names.camelCaseName}Module.kt",
                DITemplate().generate(
                    DIParams.build {
                        packageName = featureMetadata.packageName
                        lowerCaseFeatureName = names.lowerCaseModuleName
                    }
                )
            )
        }
    }
}
