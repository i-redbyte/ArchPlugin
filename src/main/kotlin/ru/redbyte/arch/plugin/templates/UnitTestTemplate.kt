package ru.redbyte.arch.plugin.templates

import ru.redbyte.arch.plugin.utils.*

class UnitTestTemplate : Template<UnitTestParams> {
    override fun generate(params: UnitTestParams): String {
        val importList = listOf(
            "$IMPORT io.mockk.*",
            "$IMPORT org.junit.jupiter.api.*",
            "$IMPORT com.noxx.navigator.navigator.Navigator",
            "$IMPORT $PROJECT_BASE_TEST"
        )

        return """
$PACKAGE ${params.packageName}.${params.lowerCaseFeatureName}.presentation

${importList.sortedImports().joinToString("\n")}    

$CLASS ${params.camelCaseFeatureName}Test: BaseTest() {

$TAB$PRIVATE val navigator: Navigator = mockk()

$TAB@Test
$TAB@DisplayName("Test")
$TAB$FUN test() = runTurbineTest {

$TAB}

}
""".trimIndent()
    }
}