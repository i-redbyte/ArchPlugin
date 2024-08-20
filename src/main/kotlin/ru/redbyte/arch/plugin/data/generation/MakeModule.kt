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
        makeReducerPackage()

    }

    private fun makeReducerPackage() {
        javaDirectory
            ?.findSubdirectory("presentation")
            ?.createSubdirectory("reducer")
            ?.apply {
                addFile(
                    "${names.camelCaseName}Event.kt",
                    EventTemplate().generate(
                        EventParams(
                            names.lowerCaseModuleName,
                            names.camelCaseName
                        )
                    )
                )
                addFile(
                    "${names.camelCaseName}State.kt",
                    StateTemplate().generate(
                        StateParams(
                            names.lowerCaseModuleName,
                            names.camelCaseName
                        )
                    )
                )
                addFile(
                    "${names.camelCaseName}Reducer.kt",
                    ReducerTemplate().generate(
                        ReducerParams(
                            names.lowerCaseModuleName,
                            names.camelCaseName
                        )
                    )
                )
            }
    }

    private fun makeUIPackage() {
        javaDirectory
            ?.findSubdirectory("presentation")
            ?.createSubdirectory("ui")
            ?.apply {
                addFile(
                    "${names.camelCaseName}Fragment.kt",
                    ScreenTemplate().generate(
                        ScreenParams(
                            names.lowerCaseModuleName,
                            names.camelCaseName,
                            names.snakeCaseName,
                            false,      // TODO: get with params
                            true // TODO: Add to params
                        )
                    )
                )
            }
    }


    private fun makeBuildGradle() {
        rootDirectory?.addFile(
            "build.gradle",
            BuildGradleTemplate().generate(BuildGradleParams(feature))
        )
    }

    private fun makeAndroidManifest() {
        mainDirectory?.addFile(
            "AndroidManifest.xml",
            ManifestTemplate().generate(ManifestParams(names.lowerCaseModuleName))
        )
    }
}