package ru.redbyte.arch.plugin.domain

sealed class Feature(
    val featureName: String
)

class BaseFeature(featureName: String) : Feature(featureName)

class FragmentFeature(
    featureName: String,
    val createContainer: Boolean
) : Feature(featureName)
