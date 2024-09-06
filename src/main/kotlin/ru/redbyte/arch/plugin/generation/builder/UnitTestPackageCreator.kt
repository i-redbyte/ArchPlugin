package ru.redbyte.arch.plugin.generation.builder

import com.intellij.psi.PsiDirectory
import ru.redbyte.arch.plugin.generation.addFile
import ru.redbyte.arch.plugin.generation.models.FeatureMetadata
import ru.redbyte.arch.plugin.templates.UnitTestParams
import ru.redbyte.arch.plugin.templates.UnitTestTemplate
import ru.redbyte.arch.plugin.utils.NamesBuilder

class UnitTestPackageCreator(
    private val rootDirectory: PsiDirectory?,
    private val featureMetadata: FeatureMetadata,
    private val names: NamesBuilder.Names
) : ArtifactCreator {
    override fun create() {
        fun PsiDirectory.createPackageDirectories(): PsiDirectory = featureMetadata.packageName
            .split(".")
            .fold(this) { currentDirectory, dirName ->
                currentDirectory.createSubdirectory(dirName)
            }

        val srcDir = rootDirectory?.findSubdirectory("src") ?: return
        srcDir
            .createSubdirectory("test")
            .createSubdirectory("java")
            .createPackageDirectories()
            .createSubdirectory("presentation")
            .addFile("${names.camelCaseName}Test.kt",
                UnitTestTemplate().generate(
                    UnitTestParams.build {
                        packageName = featureMetadata.packageName
                        lowerCaseFeatureName = names.lowerCaseModuleName
                        camelCaseName = names.camelCaseName
                    }
                ))
    }
}
