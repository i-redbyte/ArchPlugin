package ru.redbyte.arch.plugin.data

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiManager
import ru.redbyte.arch.plugin.domain.Feature
import java.io.File

class FeatureCreator(private val project: Project) {

    private val featuresRoot: PsiDirectory = requireNotNull(
        LocalFileSystem.getInstance().findFileByIoFile(
            File("${project.basePath}/features")
        )?.let {
            PsiManager.getInstance(project).findDirectory(it)
        }
    )

    fun createModules(feature: Feature) {
        ApplicationManager.getApplication().runWriteAction{
            with(featuresRoot.createSubdirectory(feature.featureName)) {
                // TODO: 10.08.2023 release it
            }
        }
    }
}