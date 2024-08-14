package ru.redbyte.arch.plugin.data

import com.intellij.psi.PsiDirectory
import ru.redbyte.arch.plugin.data.tmp.*
import ru.redbyte.arch.plugin.domain.Feature

class MakeModule(private val feature: Feature) : Module() {

    private val names = NamesBuilder().build(feature.params.featureName)

    override fun PsiDirectory.createJavaDirectory(): PsiDirectory {
        return createSubdirectory(names.lowerCaseModuleName)
    }

    override fun PsiDirectory.createRootFeatureDirectory(): PsiDirectory {
        return createSubdirectory(names.moduleName)
    }

    override fun createModuleStructure(directory: PsiDirectory, packageName: String) {
        super.createModuleStructure(directory, packageName)
        with(feature.params) {
            makeAndroidManifest()
            makeBuildGradle()
            makeFragmentLayout(withFragmentFiles)
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
                    FragmentTemplate().generate(
                        FragmentParams(
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

    private fun makeFragmentLayout(withFragmentFiles: Boolean) {
        if (!withFragmentFiles) return
        mainDirectory
            ?.createSubdirectory("res")
            ?.createSubdirectory("layout")
            ?.apply {
                //todo: add check is need create fragment
                addFile(
                    "t_${names.snakeCaseName}_fragment.xml",
                    FragmentLayoutTemplate().generate(NoParams)
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