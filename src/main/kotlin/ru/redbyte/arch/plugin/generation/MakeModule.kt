package ru.redbyte.arch.plugin.generation

import com.intellij.psi.PsiDirectory
import ru.redbyte.arch.plugin.utils.NamesBuilder
import ru.redbyte.arch.plugin.domain.Feature
import kotlin.properties.Delegates

class MakeModule(private val feature: Feature) : Module() {
    private val featureMetadata = feature.params.metadata

    private val names = NamesBuilder().build(featureMetadata.featureName)
    private var artifactFactory: ArtifactFactory by Delegates.notNull()

    override fun PsiDirectory.createJavaDirectory(): PsiDirectory = createSubdirectory(names.lowerCaseModuleName)

    override fun PsiDirectory.createRootFeatureDirectory(): PsiDirectory = createSubdirectory(names.moduleName)

    override fun PsiDirectory.createPackageDirectories(): PsiDirectory = featureMetadata.packageName
        .split(".")
        .fold(this) { currentDirectory, dirName ->
            currentDirectory.createSubdirectory(dirName)
        }

    override fun createModuleStructure(directory: PsiDirectory) {
        super.createModuleStructure(directory)
        artifactFactory = ArtifactFactory(
            featureMetadata,
            names,
            rootDirectory,
            mainDirectory,
            javaDirectory
        )

        artifactFactory.createAll(feature.params)
        SettingsGradleManager(directory.project)
            .ensureModuleInSettings(directory, featureMetadata.featureName)
    }
}
