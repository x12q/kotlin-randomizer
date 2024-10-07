package com.x12q.randomizer.ir_plugin

import com.tschuchort.compiletesting.KotlinCompilation
import com.x12q.randomizer.ir_plugin.mock_objects.LegalRandomConfig
import com.x12q.randomizer.ir_plugin.mock_objects.LegalRandomConfigObject
import com.x12q.randomizer.ir_plugin.mock_objects.LegalRandomConfigObject2
import com.x12q.randomizer.lib.RandomConfigImp
import com.x12q.randomizer.test.util.assertions.runRunTest
import com.x12q.randomizer.test.util.test_code.TestImportsBuilder
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import kotlin.test.Test

/**
 * Test passing random config directly via random() function and indirectly via @Randomizable annotation
 */
@OptIn(ExperimentalCompilerApi::class)
class TestPassingRandomConfig{


    data class Q123(val i:Int)

    @Test
    fun `class with no RandomConfig`() {
        val imports = TestImportsBuilder.stdImport.import(Q123::class)
        testGeneratedCodeUsingStandardPlugin(
            """
                $imports
                fun runTest():TestOutput{
                    return withTestOutput{
                        putData(QxC.random())
                        putData(QxC.random(${TestImportsBuilder.stdImport.nameOf(RandomConfigImp::class)}.default))
                    }
                }

                @Randomizable
                data class QxC(override val data: Q123):WithData
            """,
            fileName = "main.kt"
        ) {
            testCompilation = { result,_ ->
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                result.runRunTest {
                    it.getObjs().size shouldBe 2
                }
            }
        }
    }



    @Test
    fun `class with legal custom random config class via annotation`() {

        val imports = TestImportsBuilder.stdImport.import(Q123::class)
        testGeneratedCodeUsingStandardPlugin(
            """
                $imports

                fun runTest():TestOutput{
                    return withTestOutput{
                        putData(QxC.random())
                        putData(QxC.random(LegalRandomConfig()))
                    }
                }

                @Randomizable(randomConfig = LegalRandomConfig::class)
                data class QxC(override val data: Q123):WithData
            """,
            fileName = "main.kt"
        ) {
            testCompilation = { result,_ ->
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                result.runRunTest {
                    it.getObjs() shouldBe listOf(
                        Q123(LegalRandomConfig().nextInt()),
                        Q123(LegalRandomConfig().nextInt())
                    )
                }
            }
        }
    }
    @Test
    fun `class with legal custom random config object via annotation`() {
        val imports = TestImportsBuilder.stdImport.import(Q123::class)
        testGeneratedCodeUsingStandardPlugin(
            """
                $imports

                fun runTest():TestOutput{
                    return withTestOutput{
                        putData(QxC.random())
                        putData(QxC.random(LegalRandomConfigObject))
                    }
                }

                @Randomizable(randomConfig = LegalRandomConfigObject::class)
                data class QxC(override val data: Q123):WithData
            """,
            fileName = "main.kt"
        ) {
            testCompilation = { result,_ ->
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                result.runRunTest {
                    it.getObjs() shouldBe listOf(
                        Q123(LegalRandomConfigObject.nextInt()),
                        Q123(LegalRandomConfigObject.nextInt())
                    )
                }
            }
        }
    }

    @Test
    fun `overriding random config in annotation`() {
        val imports = TestImportsBuilder.stdImport.import(Q123::class)
        testGeneratedCodeUsingStandardPlugin(
            """
                $imports

                fun runTest():TestOutput{
                    return withTestOutput{
                        putData(QxC.random())
                        putData(QxC.random(${imports.nameOf(LegalRandomConfigObject2::class)}))
                    }
                }

                @Randomizable(randomConfig = ${imports.nameOf(LegalRandomConfigObject::class)}::class)
                data class QxC(override val data: Q123):WithData
            """,
            fileName = "main.kt"
        ) {
            testCompilation = { result,_ ->
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                result.runRunTest {
                    it.getObjs() shouldBe listOf(
                        Q123(LegalRandomConfigObject.nextInt()),
                        Q123(LegalRandomConfigObject2.nextInt())
                    )
                }
            }
        }
    }


    @Test
    fun `class with illegal random config via annotation`() {

        RandomConfigImp
        testGeneratedCodeUsingStandardPlugin(
            """
                ${TestImportsBuilder.stdImport}

                fun main(){
                    println(Q123.random())
                }
                @Randomizable(randomConfig = IllegalRandomConfig::class)
                data class Q123(val i:Int)
            """,
            fileName = "main.kt"
        ) {
            testCompilation = { result,_ ->
                result.exitCode shouldNotBe KotlinCompilation.ExitCode.OK
            }
        }
    }
}
