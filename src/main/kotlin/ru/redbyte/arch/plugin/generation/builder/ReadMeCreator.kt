package ru.redbyte.arch.plugin.generation.builder

import com.intellij.psi.PsiDirectory
import ru.redbyte.arch.plugin.generation.addFile
import ru.redbyte.arch.plugin.generation.models.FeatureMetadata
import ru.redbyte.arch.plugin.templates.NoParams
import ru.redbyte.arch.plugin.templates.ReadmeTemplate
import ru.redbyte.arch.plugin.utils.NamesBuilder

class ReadMeCreator(
    private val rootDirectory: PsiDirectory?,
    private val featureMetadata: FeatureMetadata,
    private val names: NamesBuilder.Names
) : ArtifactCreator {
    override fun create() {
        rootDirectory?.addFile(
            "README.md",
            ReadmeTemplate().generate(NoParams)
        )
    }
}
