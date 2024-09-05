package ru.redbyte.arch.plugin.generation

import com.intellij.psi.PsiDirectory
import ru.redbyte.arch.plugin.utils.NamesBuilder
import ru.redbyte.arch.plugin.domain.Feature
import ru.redbyte.arch.plugin.generation.builder.ArtifactBuilder
import java.util.*


class MakeModule(feature: Feature) : Module() {
    private val featureMetadata = feature.params.metadata
    private val withDIFiles = feature.params.withDIFiles
    private val contractParam = feature.params.contractParam

    private val names = NamesBuilder().build(featureMetadata.featureName)
    private val artifactBuilder: ArtifactBuilder by lazy(mode = LazyThreadSafetyMode.NONE) {
        ArtifactBuilder(featureMetadata, names, rootDirectory, mainDirectory, javaDirectory)
    }

    override fun PsiDirectory.createJavaDirectory(): PsiDirectory = createSubdirectory(names.lowerCaseModuleName)

    override fun PsiDirectory.createRootFeatureDirectory(): PsiDirectory = createSubdirectory(
        names.camelCaseName
            .replaceFirstChar { it.lowercase(Locale.getDefault()) }
    )

    override fun PsiDirectory.createPackageDirectories(): PsiDirectory = featureMetadata.packageName
        .split(".")
        .fold(this) { currentDirectory, dirName ->
            currentDirectory.createSubdirectory(dirName)
        }

    override fun createModuleStructure(directory: PsiDirectory) {
        super.createModuleStructure(directory)
        artifactBuilder.apply {
            addManifest()
            addBuildGradle()
            addReadMe()
            addPresentationPackage(contractParam)
            if (withDIFiles) addDIPackage()
            addResValuesPackage()
        }.build()

        GradleManager(directory.project)
            .ensureModuleInSettingsAndBuild(directory, featureMetadata.featureName)
    }
}
