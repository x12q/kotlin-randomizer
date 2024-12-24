package com.x12q.kotlin.randomizer.ir_plugin

import com.tschuchort.compiletesting.KotlinCompilation
import com.x12q.kotlin.randomizer.ir_plugin.mock_objects.TestRandomConfig
import com.x12q.kotlin.randomizer.test.util.WithData
import com.x12q.kotlin.randomizer.test.util.assertions.executeRunTestFunction
import com.x12q.kotlin.randomizer.test.util.test_code.TestImportsBuilder
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import kotlin.test.Test


@OptIn(ExperimentalCompilerApi::class)
class TestNestedRandomFunction {

    private val imports = TestImportsBuilder.stdImport
        .import(QxC::class)
        .import(XYZ::class)

    data class QxC<K_Q:Any>(override val data:K_Q):WithData
    data class XYZ<T,E>(val i:T, val d:E)

    @Test
    fun `test nested random function calls`() {

        testGeneratedCodeUsingStandardPlugin(
            """
               $imports

                fun runTest():TestOutput{
                    return withTestOutput{
                        putData(random<QxC<XYZ<Int,Double>>>(randomizers = {
                            int(123)
                            double{ random<Int>().toDouble() }
                        }))
                    }
                }
            """,
        ) {
            testCompilation = { result, testStream ->
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                result.executeRunTestFunction {
                    it.getObjs() shouldBe listOf(
                        XYZ(123, 123.0)
                    )
                }
            }
        }
    }

    @Test
    fun `failing cases on lowering`() {

        testGeneratedCodeUsingStandardPlugin(
            """
               $imports

                fun runTest():TestOutput{
                    println(random<XYZ<Int,Long>>(randomConfig=TestRandomConfig(), randomizers = {
                            int(123)
                            long{ 123L }
                    }))
                    println(random<Int>())
                    return withTestOutput{
                        putData(random<QxC<XYZ<Int,Long>>>(randomConfig=TestRandomConfig(), randomizers = {
                            int(123)
                            // long{ random<Int>().toLong() }
                            long{ random<Int>().toLong() }
                        }))
                    }
                }
               
            """,
        ) {
            testCompilation = { result, testStream ->
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                result.executeRunTestFunction {
                    val c = TestRandomConfig()
                    it.getObjs() shouldBe listOf(
                        XYZ(123, 123L)
                    )
                }
            }
        }
    }

}
