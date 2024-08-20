package ru.redbyte.arch.plugin.data.generation

import com.intellij.psi.PsiDirectory
import ru.redbyte.arch.plugin.data.templates.*
import ru.redbyte.arch.plugin.data.utils.NamesBuilder
import ru.redbyte.arch.plugin.domain.Feature

class MakeModule(private val feature: Feature) : Module() {

    private val names = NamesBuilder().build(feature.params.featureName)

    override fun PsiDirectory.createJavaDirectory(): PsiDirectory = createSubdirectory(names.lowerCaseModuleName)

    override fun PsiDirectory.createRootFeatureDirectory(): PsiDirectory = createSubdirectory(names.moduleName)

    override fun PsiDirectory.createPackageDirectories(): PsiDirectory = feature.params.packageName
        .split(".")
        .fold(this) { currentDirectory, dirName ->
            currentDirectory.createSubdirectory(dirName)
        }

    override fun createModuleStructure(directory: PsiDirectory) {
        super.createModuleStructure(directory)
        with(feature.params) {
            makeAndroidManifest()
            makeBuildGradle()
            makePresentationPackage(withFragmentFiles)
            makeDIPackage(withDIFiles)
        }
    }

    private fun makeDIPackage(withDIFiles: Boolean) {
        if (!withDIFiles) return
        javaDirectory?.createSubdirectory("di")
    }

    private fun makePresentationPackage(withFragmentFiles: Boolean) {
        javaDirectory?.createSubdirectory("presentation")
        if (withFragmentFiles) makeUIPackage()
    }


    private fun makeUIPackage() {
        javaDirectory
            ?.findSubdirectory("presentation")
            ?.apply {
                addFile(
                    "${names.camelCaseName}Screen.kt",
                    ScreenTemplate().generate(
                        ScreenParams.build {
                            packageName = feature.params.packageName
                            lowerCaseFeatureName = names.lowerCaseModuleName
                            camelCaseFeatureName = names.camelCaseName
                            snakeCaseFeatureName = names.snakeCaseName
                        }
                    )
                )
            }
    }

    private fun makeBuildGradle() {
        rootDirectory?.addFile(
            "build.gradle",
            BuildGradleTemplate().generate(
                BuildGradleParams.build {
                    packageName = feature.params.packageName
                    lowerCaseFeatureName = names.lowerCaseModuleName
                    feature = this@MakeModule.feature
                }
            )
        )
    }

    private fun makeAndroidManifest() {
        mainDirectory?.addFile(
            "AndroidManifest.xml",
            ManifestTemplate().generate(
                ManifestParams.build {
                    packageName = feature.params.packageName
                    lowerCaseFeatureName = names.lowerCaseModuleName
                }
            )
        )
    }
}
