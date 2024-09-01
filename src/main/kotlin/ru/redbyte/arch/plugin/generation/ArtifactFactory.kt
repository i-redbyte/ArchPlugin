package ru.redbyte.arch.plugin.generation

import com.intellij.psi.PsiDirectory
import ru.redbyte.arch.plugin.generation.models.FeatureContract
import ru.redbyte.arch.plugin.generation.models.FeatureMetadata
import ru.redbyte.arch.plugin.generation.models.FeatureParams
import ru.redbyte.arch.plugin.templates.*
import ru.redbyte.arch.plugin.utils.NamesBuilder

class ArtifactFactory (
    private val featureMetadata: FeatureMetadata,
    private val names: NamesBuilder.Names,
    private val rootDirectory: PsiDirectory?,
    private val mainDirectory: PsiDirectory?,
    private val javaDirectory: PsiDirectory?
) {

    fun createAll(params: FeatureParams) {
        makeAndroidManifest()
        makeBuildGradle()
        makeReadMe()
        makePresentationPackage(params.contractParam)
        makeDIPackage(params.withDIFiles)
        makeResValuesPackage()
    }

    private fun makeDIPackage(withDIFiles: Boolean) {
        if (!withDIFiles) return
        javaDirectory?.createSubdirectory("di")
    }

    private fun makePresentationPackage(contractParam: FeatureContract) {
        javaDirectory?.createSubdirectory("presentation")
            ?.apply {
                addFile(
                    "${names.camelCaseName}Screen.kt",
                    ScreenTemplate().generate(
                        ScreenParams.build {
                            packageName = featureMetadata.packageName
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
                                packageName = featureMetadata.packageName
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
                            packageName = featureMetadata.packageName
                            lowerCaseFeatureName = names.lowerCaseModuleName
                            camelCaseFeatureName = names.camelCaseName
                            contract = contractParam
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