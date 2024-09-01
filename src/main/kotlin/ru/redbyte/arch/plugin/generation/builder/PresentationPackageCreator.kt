package ru.redbyte.arch.plugin.generation.builder

import com.intellij.psi.PsiDirectory
import ru.redbyte.arch.plugin.generation.addFile
import ru.redbyte.arch.plugin.generation.models.FeatureContract
import ru.redbyte.arch.plugin.generation.models.FeatureMetadata
import ru.redbyte.arch.plugin.templates.*
import ru.redbyte.arch.plugin.utils.NamesBuilder

class PresentationPackageCreator(
    private val javaDirectory: PsiDirectory?,
    private val featureMetadata: FeatureMetadata,
    private val names: NamesBuilder.Names,
    private val contractParam: FeatureContract
) : ArtifactCreator {
    override fun create() {
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
}