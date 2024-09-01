package ru.redbyte.arch.plugin.generation.builder

import com.intellij.psi.PsiDirectory
import ru.redbyte.arch.plugin.generation.addFile
import ru.redbyte.arch.plugin.generation.models.FeatureMetadata
import ru.redbyte.arch.plugin.templates.StringsParams
import ru.redbyte.arch.plugin.templates.StringsTemplate
import ru.redbyte.arch.plugin.utils.NamesBuilder

class ResValuesPackageCreator(
    private val mainDirectory: PsiDirectory?,
    private val featureMetadata: FeatureMetadata,
    private val names: NamesBuilder.Names
) : ArtifactCreator {
    override fun create() {
        val stringsXmlContent = StringsTemplate().generate(
            StringsParams.build {
                packageName = featureMetadata.packageName
                lowerCaseFeatureName = names.lowerCaseModuleName
            }
        )
        mainDirectory?.createSubdirectory("res")?.apply {
            createSubdirectory("values").addFile("strings.xml", stringsXmlContent)
            createSubdirectory("values-en").addFile("strings.xml", stringsXmlContent)
        }
    }
}