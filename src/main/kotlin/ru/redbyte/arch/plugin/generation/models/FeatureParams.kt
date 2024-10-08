package ru.redbyte.arch.plugin.generation.models

data class FeatureParams(
    val metadata: FeatureMetadata,
    val contractParam: FeatureContract,
    val withDIFiles: Boolean,
    val withUnitTest: Boolean
)

data class FeatureContract(
    val withState: Boolean,
    val withActions: Boolean,
    val withEffect: Boolean
)

data class FeatureMetadata(
    val featureName: String,
    val selectedDirectory: String,
    val packageName: String
)