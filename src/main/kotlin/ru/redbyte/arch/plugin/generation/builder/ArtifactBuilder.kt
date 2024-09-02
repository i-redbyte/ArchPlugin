package ru.redbyte.arch.plugin.generation.builder

import com.intellij.psi.PsiDirectory
import ru.redbyte.arch.plugin.generation.models.FeatureContract
import ru.redbyte.arch.plugin.generation.models.FeatureMetadata
import ru.redbyte.arch.plugin.utils.NamesBuilder

class ArtifactBuilder(
    private val featureMetadata: FeatureMetadata,
    private val names: NamesBuilder.Names,
    private val rootDirectory: PsiDirectory?,
    private val mainDirectory: PsiDirectory?,
    private val javaDirectory: PsiDirectory?
) {
    private val artifactCreators = mutableListOf<ArtifactCreator>()

    fun addManifest(): ArtifactBuilder {
        artifactCreators.add(AndroidManifestCreator(mainDirectory, featureMetadata, names))
        return this
    }

    fun addBuildGradle(): ArtifactBuilder {
        artifactCreators.add(BuildGradleCreator(rootDirectory, featureMetadata, names))
        return this
    }

    fun addReadMe(): ArtifactBuilder {
        artifactCreators.add(ReadMeCreator(rootDirectory, featureMetadata, names))
        return this
    }

    fun addPresentationPackage(contractParam: FeatureContract): ArtifactBuilder {
        artifactCreators.add(PresentationPackageCreator(javaDirectory, featureMetadata, names, contractParam))
        return this
    }

    fun addDIPackage(): ArtifactBuilder {
        artifactCreators.add(DIPackageCreator(javaDirectory, featureMetadata, names))
        return this
    }

    fun addResValuesPackage(): ArtifactBuilder {
        artifactCreators.add(ResValuesPackageCreator(mainDirectory, featureMetadata, names))
        return this
    }

    fun build() {
        artifactCreators.forEach { it.create() }
    }
}
