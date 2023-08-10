package ru.redbyte.arch.plugin.presentation

import com.intellij.openapi.ui.ValidationInfo
import ru.redbyte.arch.plugin.data.FeatureCreator
import ru.redbyte.arch.plugin.domain.BaseFeature
import ru.redbyte.arch.plugin.domain.FragmentFeature


interface FeaturePresenter {

    fun getTypeArray(): Array<String>

    fun createFeature(name: String, createFragment: Boolean) // TODO: 10.08.2023 add di state

    fun validate(name: String): ValidationInfo?

    fun onTypeSelected(type: String)
}

class FeaturePresenterImpl(
    private val featureView: FeatureView,
    private val featureCreator: FeatureCreator
) : FeaturePresenter {

    private var type = Type.FragmentFeature

    override fun getTypeArray(): Array<String> {
        return Type.values().map { it.text }.toTypedArray()
    }

    override fun createFeature(name: String, createFragment: Boolean) {
        try {
            val feature = when (type) {
                Type.BaseFeature -> BaseFeature(name)
                Type.FragmentFeature -> FragmentFeature(name, createFragment)
            }

            featureCreator.createModules(feature)
            featureView.closeSuccessfully()
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
}

enum class Type(val text: String) {
    FragmentFeature("Fragment Feature"), BaseFeature("Base Feature")
}