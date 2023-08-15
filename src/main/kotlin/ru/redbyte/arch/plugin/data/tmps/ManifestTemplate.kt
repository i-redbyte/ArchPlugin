package ru.redbyte.arch.plugin.data.tmps

class ManifestTemplate: Template<ManifestParams> {
    override fun generate(params: ManifestParams): String =
        "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<manifest package=\"ru.redbyte.arch.${params.lowerCaseFeatureName}\" />"
}
