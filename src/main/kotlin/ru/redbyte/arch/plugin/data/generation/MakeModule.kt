package ru.redbyte.arch.plugin.data.generation

import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiFile
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
        val settingsFile = getSettingsFile(project) ?: return

        val moduleName = generateModuleName(directory, project.basePath ?: return)

        if (!isModuleIncluded(settingsFile, moduleName)) {
            insertModuleInAlphabeticalOrder(project, settingsFile, moduleName)
        }
    }

    private fun getSettingsFile(project: Project): PsiFile? {
        val basePath = project.basePath ?: return null
        val baseDir = LocalFileSystem.getInstance().findFileByPath(basePath) ?: return null
        val settingsFile = baseDir.findChild("settings.gradle.kts") ?: baseDir.findChild("settings.gradle")
        return settingsFile?.let { PsiManager.getInstance(project).findFile(it) }
    }

    private fun generateModuleName(directory: PsiDirectory, basePath: String): String {
        val relativePath = directory.virtualFile.path
            .removePrefix(basePath)
            .replace(File.separator, ":")
            .removePrefix(":")
        return ":$relativePath:${feature.params.featureName.lowercase()}"
    }

    private fun isModuleIncluded(settingsFile: PsiFile, moduleName: String): Boolean {
        return settingsFile.text.contains(moduleName)
    }

    private fun insertModuleInAlphabeticalOrder(project: Project, settingsFile: PsiFile, moduleName: String) {
        WriteCommandAction.runWriteCommandAction(project) {
            val document = settingsFile.viewProvider.document ?: return@runWriteCommandAction
            val lines = document.text.split("\n").toMutableList()
            val includeLines = lines.filter { it.startsWith("include") }
            val insertIndex = includeLines.indexOfFirst { it > "include '$moduleName'" }

            if (includeLines.isEmpty()) {
                document.insertString(document.textLength, "\ninclude '$moduleName'")
            } else if (insertIndex == -1) {
                val lastIncludeLine = lines.indexOf(includeLines.last())
                document.insertString(document.getLineEndOffset(lastIncludeLine), "\ninclude '$moduleName'")
            } else {
                val includeInsertLine = lines.indexOf(includeLines[insertIndex])
                document.insertString(document.getLineStartOffset(includeInsertLine), "include '$moduleName'\n")
            }
        }
    }

}
