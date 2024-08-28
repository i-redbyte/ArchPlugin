package ru.redbyte.arch.plugin.data.generation

import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiManager
import ru.redbyte.arch.plugin.data.templates.*
import ru.redbyte.arch.plugin.data.utils.NamesBuilder
import ru.redbyte.arch.plugin.domain.Feature
import java.io.File
import java.io.IOException

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
            makePresentationPackage()
            makeDIPackage(withDIFiles)
            makeResValuesPackage()
        }
        addModuleToSettingsGradle(directory)
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

    private fun addModuleToSettingsGradle(directory: PsiDirectory) {
        val project = directory.project
        val basePath = project.basePath
        val baseDir = basePath?.let { LocalFileSystem.getInstance().findFileByPath(it) }
        val settingsFile = baseDir?.findChild("settings.gradle.kts") ?: baseDir?.findChild("settings.gradle")

        settingsFile?.let {
            val psiFile = PsiManager.getInstance(project).findFile(it)
            val relativePath = directory.virtualFile.path
                .removePrefix(basePath.toString())
                .replace(File.separator, ":")
                .removePrefix(":")
            val moduleName = ":$relativePath:${feature.params.featureName.lowercase()}"

            psiFile?.let { file ->
                val content = file.text
                if (!content.contains(moduleName)) {
                    WriteCommandAction.runWriteCommandAction(project) {
                        try {
                            val document = file.viewProvider.document
                            document?.let { doc ->
                                doc.insertString(doc.textLength, "\ninclude '$moduleName'")
                            }
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        }
    }

}
