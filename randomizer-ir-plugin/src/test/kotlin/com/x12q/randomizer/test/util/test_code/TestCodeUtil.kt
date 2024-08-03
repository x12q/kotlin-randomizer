package com.x12q.randomizer.test.util.test_code

import com.x12q.randomizer.lib.DefaultRandomConfig
import com.x12q.randomizer.lib.annotations.Randomizable
import com.x12q.randomizer.ir_plugin.mock_objects.AlwaysFalseRandomConfig
import com.x12q.randomizer.ir_plugin.mock_objects.AlwaysTrueRandomConfig
import com.x12q.randomizer.ir_plugin.mock_objects.LegalRandomConfigObject
import com.x12q.randomizer.test.util.TestOutput
import com.x12q.randomizer.test.util.WithData
import com.x12q.randomizer.test.util.withTestOutput
import org.intellij.lang.annotations.Language


fun testFunctionTemplate(
    @Language("kotlin")
    withTestOutputBody: String
): String {
    return """
        fun runTest():TestOutput{
            return withTestOutput{
                $withTestOutputBody
            }
        }
    """.trimIndent()
}


fun testBlock(
    @Language("kotlin")
    importCode: String = "",
    @Language("kotlin")
    withTestOutputBody: String = "",
    @Language("kotlin")
    below: String = ""
): String {
    return """
        $importCode
        
        ${testFunctionTemplate(withTestOutputBody)}
        
        $below
    """.trimIndent()
}

fun testBlock(
    importData: ImportData = ImportData.stdImport,
    withTestOutputBody: (ImportData) -> String = { "" },
    below: (ImportData) -> String = { "" }
): String {
    return testBlock(
        importCode = importData.importCode,
        withTestOutputBody = withTestOutputBody(importData),
        below = below(importData),
    )
}
