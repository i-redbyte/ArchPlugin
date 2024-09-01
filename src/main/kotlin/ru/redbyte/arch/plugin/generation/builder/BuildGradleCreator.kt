package ru.redbyte.arch.plugin.generation.builder

import com.intellij.psi.PsiDirectory
import ru.redbyte.arch.plugin.generation.addFile
import ru.redbyte.arch.plugin.generation.models.FeatureMetadata
import ru.redbyte.arch.plugin.templates.BuildGradleParams
import ru.redbyte.arch.plugin.templates.BuildGradleTemplate
import ru.redbyte.arch.plugin.utils.NamesBuilder

class BuildGradleCreator(
    private val rootDirectory: PsiDirectory?,
    private val featureMetadata: FeatureMetadata,
    private val names: NamesBuilder.Names
) : ArtifactCreator {
    override fun create() {
        rootDirectory?.addFile(
            "build.gradle",
            BuildGradleTemplate().generate(
                BuildGradleParams.build {
                    packageName = featureMetadata.packageName
                    lowerCaseFeatureName = names.lowerCaseModuleName
                }
            )
        )
    }
}
