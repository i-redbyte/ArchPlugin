package ru.redbyte.arch.plugin.data

data class FeatureParams(
    val featureName: String,
    val withDIFiles: Boolean,
    val withFragmentFiles: Boolean,
    val selectedDirectory: String,
)