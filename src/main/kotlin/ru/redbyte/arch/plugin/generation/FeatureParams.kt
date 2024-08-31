package ru.redbyte.arch.plugin.generation

data class FeatureParams(
    val featureName: String,
    val withDIFiles: Boolean,
    val selectedDirectory: String,
    val packageName: String,
    val withState: Boolean,
    val withActions: Boolean,
    val withEffect: Boolean
)