package ru.redbyte.arch.plugin.domain

import ru.redbyte.arch.plugin.data.FeatureParams

sealed class Feature(
    val params: FeatureParams
)

class BaseFeature(params: FeatureParams) : Feature(params)

class FragmentFeature(params: FeatureParams) : Feature(params)
