package ru.redbyte.arch.plugin.templates

class ManifestTemplate: Template<ManifestParams> {
    override fun generate(params: ManifestParams): String =
        "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<manifest package=\"${params.packageName}.${params.lowerCaseFeatureName}\" />"
}
