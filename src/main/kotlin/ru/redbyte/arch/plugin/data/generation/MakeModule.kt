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
            makeReadMe()
            makePresentationPackage()
            makeDIPackage(withDIFiles)
            makeResValuesPackage()
        }
        SettingsGradleManager(directory.project)
            .ensureModuleInSettings(directory, feature.params.featureName)
    }

    private fun makeDIPackage(withDIFiles: Boolean) {
        if (!withDIFiles) return
        javaDirectory?.createSubdirectory("di")
    }

    private fun makePresentationPackage() {
        javaDirectory?.createSubdirectory("presentation")
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
                if (feature.params.withState || feature.params.withActions || feature.params.withEffect) {
                    addFile(
                        "${names.camelCaseName}Contract.kt",
                        ContractTemplate().generate(
                            ContractParams.build {
                                packageName = feature.params.packageName
                                lowerCaseFeatureName = names.lowerCaseModuleName
                                camelCaseFeatureName = names.camelCaseName
                                withState = feature.params.withState
                                withActions = feature.params.withActions
                                withEffect = feature.params.withEffect
                            }
                        )
                    )
                }
                addFile(
                    "${names.camelCaseName}ViewModel.kt",
                    ViewModelTemplate().generate(
                        ViewModelParams.build {
                            packageName = feature.params.packageName
                            lowerCaseFeatureName = names.lowerCaseModuleName
                            camelCaseFeatureName = names.camelCaseName
                            withState = feature.params.withState
                            withActions = feature.params.withActions
                            withEffect = feature.params.withEffect
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
                }
            )
        )
    }

    private fun makeReadMe() {
        rootDirectory?.addFile(
            "README.md",
            ReadmeTemplate().generate(NoParams)
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

    private fun makeResValuesPackage() {
        val stringsXmlContent = StringsTemplate().generate(
            StringsParams.build {
                packageName = feature.params.packageName
                lowerCaseFeatureName = names.lowerCaseModuleName
            }
        )
        mainDirectory?.createSubdirectory("res")?.apply {
            createSubdirectory("values").addFile("strings.xml", stringsXmlContent)
            createSubdirectory("values-en").addFile("strings.xml", stringsXmlContent)
        }
    }

}
