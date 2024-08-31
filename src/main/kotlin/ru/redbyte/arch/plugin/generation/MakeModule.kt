package ru.redbyte.arch.plugin.generation

import com.intellij.psi.PsiDirectory
import ru.redbyte.arch.plugin.templates.*
import ru.redbyte.arch.plugin.utils.NamesBuilder
import ru.redbyte.arch.plugin.domain.Feature

class MakeModule(private val feature: Feature) : Module() {
    private val featureMetadata = feature.params.metadata

    private val names = NamesBuilder().build(featureMetadata.featureName)

    override fun PsiDirectory.createJavaDirectory(): PsiDirectory = createSubdirectory(names.lowerCaseModuleName)

    override fun PsiDirectory.createRootFeatureDirectory(): PsiDirectory = createSubdirectory(names.moduleName)

    override fun PsiDirectory.createPackageDirectories(): PsiDirectory = featureMetadata.packageName
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
            .ensureModuleInSettings(directory, featureMetadata.featureName)
    }

    private fun makeDIPackage(withDIFiles: Boolean) {
        if (!withDIFiles) return
        javaDirectory?.createSubdirectory("di")
    }

    private fun makePresentationPackage() {
        with(feature.params) {
            javaDirectory?.createSubdirectory("presentation")
                ?.apply {
                    addFile(
                        "${names.camelCaseName}Screen.kt",
                        ScreenTemplate().generate(
                            ScreenParams.build {
                                packageName = metadata.packageName
                                lowerCaseFeatureName = names.lowerCaseModuleName
                                camelCaseFeatureName = names.camelCaseName
                                snakeCaseFeatureName = names.snakeCaseName
                                contract = contractParam
                            }
                        )
                    )
                    if (contractParam.withState || contractParam.withActions || contractParam.withEffect) {
                        addFile(
                            "${names.camelCaseName}Contract.kt",
                            ContractTemplate().generate(
                                ContractParams.build {
                                    packageName = metadata.packageName
                                    lowerCaseFeatureName = names.lowerCaseModuleName
                                    camelCaseFeatureName = names.camelCaseName
                                    contract = contractParam
                                }
                            )
                        )
                    }
                    addFile(
                        "${names.camelCaseName}ViewModel.kt",
                        ViewModelTemplate().generate(
                            ViewModelParams.build {
                                packageName = metadata.packageName
                                lowerCaseFeatureName = names.lowerCaseModuleName
                                camelCaseFeatureName = names.camelCaseName
                                contract = contractParam
                            }
                        )
                    )
                }
        }
    }

    private fun makeBuildGradle() {
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
                    packageName = featureMetadata.packageName
                    lowerCaseFeatureName = names.lowerCaseModuleName
                }
            )
        )
    }

    private fun makeResValuesPackage() {
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
