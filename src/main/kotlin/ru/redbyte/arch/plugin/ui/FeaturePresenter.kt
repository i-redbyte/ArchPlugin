package ru.redbyte.arch.plugin.ui

import com.intellij.openapi.ui.ValidationInfo
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiManager
import ru.redbyte.arch.plugin.data.generation.FeatureCreator
import ru.redbyte.arch.plugin.data.generation.FeatureParams
import ru.redbyte.arch.plugin.domain.Feature

interface FeaturePresenter {

    fun createFeature(params: FeatureParams)

    fun validate(name: String): ValidationInfo?

}

class FeaturePresenterImpl(
    private val featureView: FeatureView,
    private val featureCreator: FeatureCreator,
) : FeaturePresenter {

    override fun createFeature(params: FeatureParams) {
        try {
            val feature = Feature(params)
            val targetDirectory = findTargetDirectory(params.selectedDirectory)
                ?: throw IllegalArgumentException("Target directory not found.")
            featureCreator.createModules(feature, targetDirectory)
            featureView.closeSuccessfully()
        } catch (e: IllegalArgumentException) {
            featureView.closeWithError(e.message ?: "Unknown error")
        } catch (e: Exception) {
            featureView.closeWithError("An unexpected error occurred: ${e.message}")
        }
    }

    override fun validate(name: String): ValidationInfo? {
        return when {
            name.isBlank() -> ValidationInfo("Feature name cannot be empty")
            !isValidFeatureName(name) -> ValidationInfo("Not valid feature name")
            else -> null
        }
    }

    private fun isValidFeatureName(name: String): Boolean {
        return name.matches(Regex("^[a-z][a-z0-9-]+"))
    }

    private fun findTargetDirectory(directoryName: String): PsiDirectory? {
        val projectBaseDir = findProjectBaseDirectory() ?: return null
        return projectBaseDir.subdirectories.find { it.name == directoryName }
    }

    private fun findProjectBaseDirectory(): PsiDirectory? {
        val projectBasePath = featureCreator.project.basePath ?: return null
        val projectBaseDir = LocalFileSystem.getInstance().findFileByPath(projectBasePath) ?: return null
        return PsiManager.getInstance(featureCreator.project).findDirectory(projectBaseDir)
    }

}
