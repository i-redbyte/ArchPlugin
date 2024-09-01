package ru.redbyte.arch.plugin.generation.builder

import com.intellij.psi.PsiDirectory
import ru.redbyte.arch.plugin.generation.addFile
import ru.redbyte.arch.plugin.generation.models.FeatureMetadata
import ru.redbyte.arch.plugin.templates.ManifestParams
import ru.redbyte.arch.plugin.templates.ManifestTemplate
import ru.redbyte.arch.plugin.utils.NamesBuilder

class AndroidManifestCreator(
    private val mainDirectory: PsiDirectory?,
    private val featureMetadata: FeatureMetadata,
    private val names: NamesBuilder.Names
) : ArtifactCreator {
    override fun create() {
        mainDirectory?.addFile(
            "AndroidManifest.xml",
            ManifestTemplate().generate(
                ManifestParams.build {
                    packageName = featureMetadata.packageName
                    lowerCaseFeatureName = names.lowerCaseModuleName
                }
            )
        )
    }
}