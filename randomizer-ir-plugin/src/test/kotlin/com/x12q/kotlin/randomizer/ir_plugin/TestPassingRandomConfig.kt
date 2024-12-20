package com.x12q.kotlin.randomizer.ir_plugin

import com.tschuchort.compiletesting.KotlinCompilation
import com.x12q.kotlin.randomizer.ir_plugin.mock_objects.LegalRandomConfig
import com.x12q.kotlin.randomizer.ir_plugin.mock_objects.LegalRandomConfigObject
import com.x12q.kotlin.randomizer.ir_plugin.mock_objects.LegalRandomConfigWithOppositeInt
import com.x12q.kotlin.randomizer.lib.RandomConfig
import com.x12q.kotlin.randomizer.lib.RandomConfigImp
import com.x12q.kotlin.randomizer.test.util.WithData
import com.x12q.kotlin.randomizer.test.util.assertions.executeRunTestFunction
import com.x12q.kotlin.randomizer.test.util.test_code.TestImportsBuilder
import io.kotest.matchers.shouldBe
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import kotlin.test.Test

/**
 * Test passing random config directly via random() function and indirectly via @Randomizable annotation
 */
@OptIn(ExperimentalCompilerApi::class)
class TestPassingRandomConfig{


    data class Q123(val i:Int)
    data class QxC(override val data: Q123):WithData
    val imports = TestImportsBuilder.stdImport
        .import(Q123::class)
        .import(QxC::class)

    @Test
    fun `class with no RandomConfig`() {
        testGeneratedCodeUsingStandardPlugin(
            """
                $imports
                fun runTest():TestOutput{
                    return withTestOutput{
                        putData(random<QxC>())
                        putData(random<QxC>(randomConfig=${TestImportsBuilder.stdImport.nameOf(RandomConfig::class)}.default))
                    }
                }
            """,
            fileName = "main.kt"
        ) {
            testCompilation = { result,_ ->
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                result.executeRunTestFunction {
                    it.getObjs().size shouldBe 2
                }
            }
        }
    }


    @Test
    fun `class with legal custom random config class via annotation`() {

        testGeneratedCodeUsingStandardPlugin(
            """
                $imports

                fun runTest():TestOutput{
                    return withTestOutput{
                        putData(random<QxC>(randomConfig=LegalRandomConfig()))
                    }
                }
            """,
            fileName = "main.kt"
        ) {
            testCompilation = { result,_ ->
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                result.executeRunTestFunction {
                    it.getObjs() shouldBe listOf(
                        Q123(LegalRandomConfig().nextInt()),
                    )
                }
            }
        }
    }
    @Test
    fun `class with legal custom random config object via annotation`() {

        testGeneratedCodeUsingStandardPlugin(
            """
                $imports

                fun runTest():TestOutput{
                    return withTestOutput{
                        putData(random<QxC>(randomConfig=LegalRandomConfigObject))
                    }
                }
            """,
            fileName = "main.kt"
        ) {
            testCompilation = { result,_ ->
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                result.executeRunTestFunction {
                    it.getObjs() shouldBe listOf(
                        Q123(LegalRandomConfigObject.nextInt()),
                    )
                }
            }
        }
    }

    @Test
    fun `overriding random config in annotation`() {

        testGeneratedCodeUsingStandardPlugin(
            """
                $imports

                fun runTest():TestOutput{
                    return withTestOutput{
                        putData(random<QxC>(randomConfig=${imports.nameOf(LegalRandomConfigObject::class)}))
                        putData(random<QxC>(randomConfig=${imports.nameOf(LegalRandomConfigWithOppositeInt::class)}))
                    }
                }
            """,
            fileName = "main.kt"
        ) {
            testCompilation = { result,_ ->
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                result.executeRunTestFunction {
                    it.getObjs() shouldBe listOf(
                        Q123(LegalRandomConfigObject.nextInt()),
                        Q123(LegalRandomConfigWithOppositeInt.nextInt())
                    )
                }
            }
        }
    }


    @Test
    fun `class with illegal random config via annotation`() {

        testGeneratedCodeUsingStandardPlugin(
            """
                $imports

                fun main(){
                    println(random<Q123>(randomConfig=IllegalRandomConfig))
                }
            """,
            fileName = "main.kt"
        ) {
            testCompilation = { result,_ ->
                result.exitCode shouldBe KotlinCompilation.ExitCode.COMPILATION_ERROR
            }
        }
    }
}
