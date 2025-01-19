package com.x12q.kotlin.randomizer.ir_plugin

import com.tschuchort.compiletesting.KotlinCompilation
import com.x12q.kotlin.randomizer.test_utils.WithData
import com.x12q.kotlin.randomizer.test_utils.test_code.TestImportsBuilder
import io.kotest.matchers.shouldBe
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import kotlin.test.*
class TestRandomInnerClass {

    class Q{
        inner class I1(val i: Int,)
    }
    data class QxC<T1:Any>(override val data:T1):WithData

    private val imports = TestImportsBuilder.stdImport
        .import(Q::class)
        .import(QxC::class)

    @OptIn(ExperimentalCompilerApi::class)
    @Test
    fun `list in type param - 3 nested`() {
        testGeneratedCodeUsingStandardPlugin(
            """
                $imports

                fun runTest():TestOutput {
                    return withTestOutput {
                        val v = random<Q.I1>(randomConfig=LegalRandomConfigObject)
                        putData(QxC(v))
                    }
                }
            """,
        ) {
            testCompilation = { result, _ ->
                result.exitCode shouldBe KotlinCompilation.ExitCode.INTERNAL_ERROR
            }
        }
    }

}
