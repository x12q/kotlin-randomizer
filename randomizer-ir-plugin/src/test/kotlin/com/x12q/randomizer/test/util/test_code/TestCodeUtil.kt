package com.x12q.randomizer.test.util.test_code

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
    testImportsBuilder: TestImportsBuilder = TestImportsBuilder.stdImport,
    withTestOutputBody: (TestImportsBuilder) -> String = { "" },
    below: (TestImportsBuilder) -> String = { "" }
): String {
    return testBlock(
        importCode = testImportsBuilder.importCode,
        withTestOutputBody = withTestOutputBody(testImportsBuilder),
        below = below(testImportsBuilder),
    )
}
