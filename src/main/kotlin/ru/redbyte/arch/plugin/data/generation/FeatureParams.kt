package ru.redbyte.arch.plugin.data.generation

data class FeatureParams(
    val featureName: String,
    val withDIFiles: Boolean,
    val selectedDirectory: String,
    val packageName: String
)