package ru.redbyte.arch.plugin.presentation

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.ValidationInfo
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiManager
import ru.redbyte.arch.plugin.data.FeatureCreator
import ru.redbyte.arch.plugin.data.FeatureParams
import ru.redbyte.arch.plugin.domain.BaseFeature
import ru.redbyte.arch.plugin.domain.FragmentFeature

interface FeaturePresenter {
    var defaultPackageName: String?

    fun getTypeArray(): Array<String>

    fun createFeature(params: FeatureParams)

    fun validate(name: String): ValidationInfo?

    fun onTypeSelected(type: String)
}

class FeaturePresenterImpl(
    private val featureView: FeatureView,
    private val featureCreator: FeatureCreator,
) : FeaturePresenter {

    override var defaultPackageName: String? = null

    private var type = Type.FragmentFeature

    override fun getTypeArray(): Array<String> {
        return Type.values().map { it.text }.toTypedArray()
    }

    override fun createFeature(params: FeatureParams) {
        try {
            val feature = when (type) {
                Type.BaseFeature -> BaseFeature(params)
                Type.FragmentFeature -> FragmentFeature(params)
            }

            val targetDirectory = findTargetDirectory(params.selectedDirectory)
            if (targetDirectory != null) {
                featureCreator.createModules(feature, targetDirectory)
                featureView.closeSuccessfully()
            } else {
                featureView.closeWithError("Target directory not found.")
            }
        } catch (e: Exception) {
            featureView.closeWithError("Error occurred: ${e.message}")
        }
    }

    override fun validate(name: String): ValidationInfo? {
        if (name.isBlank()) {
            return ValidationInfo("Feature name cannot be empty")
        } else if (!name.matches(Regex("^[a-z][a-z0-9-]+"))) {
            return ValidationInfo("Not valid feature name")
        }
        return null
    }

    override fun onTypeSelected(type: String) {
        Type.values().find { it.text == type }?.let {
            this.type = it
            featureView.enableCheckBoxes(it == Type.FragmentFeature)
        }
    }

    private fun findTargetDirectory(directoryName: String): PsiDirectory? {
        val projectBaseDir = LocalFileSystem
            .getInstance()
            .findFileByPath(
                featureCreator.project.basePath ?: return null
            )
        val psiProjectBaseDir = projectBaseDir
            ?.let {
                PsiManager
                    .getInstance(featureCreator.project)
                    .findDirectory(it)
            } ?: return null

        return psiProjectBaseDir.subdirectories.find { it.name == directoryName }
    }


}

enum class Type(val text: String) {
    FragmentFeature("Fragment Feature"), BaseFeature("Base Feature")
}