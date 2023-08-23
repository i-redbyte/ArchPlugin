package ru.redbyte.arch.plugin.data

import com.intellij.psi.PsiDirectory
import ru.redbyte.arch.plugin.data.tmp.*
import ru.redbyte.arch.plugin.domain.Feature

class MakeModule(private val feature: Feature) : Module() {

    private val names = NamesBuilder().build(feature.featureName)

    override fun PsiDirectory.createJavaDirectory(): PsiDirectory {
        return createSubdirectory(names.lowerCaseModuleName)
    }

    override fun PsiDirectory.createRootFeatureDirectory(): PsiDirectory {
        return createSubdirectory(names.moduleName)
    }

    override fun createModuleStructure(directory: PsiDirectory) {
        super.createModuleStructure(directory)
        makeAndroidManifest()
        makeBuildGradle()
        mainDirectory
            ?.createSubdirectory("res")
            ?.createSubdirectory("layout")
            ?.apply {
                //todo: add check is need create fragment
                addFile(
                    "t_${names.snakeCaseName}_container_fragment.xml",
                    ContainerLayoutTemplate().generate(NoParams)
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