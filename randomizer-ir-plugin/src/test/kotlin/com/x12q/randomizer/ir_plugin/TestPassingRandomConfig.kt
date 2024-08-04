package com.x12q.randomizer.ir_plugin

import com.tschuchort.compiletesting.KotlinCompilation
import com.x12q.randomizer.lib.RandomConfigImp
import com.x12q.randomizer.test.util.assertions.runMain
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
    @Test
    fun `class with a RandomConfig object via annotation`() {

        RandomConfigImp
        testGeneratedCodeUsingStandardPlugin(
            """
                ${TestImportsBuilder.stdImport}

                fun main(){
                    println(Q123.random(${TestImportsBuilder.stdImport.nameOf(RandomConfigImp::class)}.default))
                    println(Q123.random())
                }
                @Randomizable(
                    randomConfig = ${TestImportsBuilder.stdImport.nameOf(RandomConfigImp::class)}::class
                )
                data class Q123(val i:Int)
            """,
            fileName = "main.kt"
        ) {
            testCompilation = { result,_ ->
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                result.runMain()
            }
        }
    }

    @Test
    fun `class with no RandomConfig`() {

        RandomConfigImp
        testGeneratedCodeUsingStandardPlugin(
            """
                ${TestImportsBuilder.stdImport}

                fun main(){
                    println(Q123.random(${TestImportsBuilder.stdImport.nameOf(RandomConfigImp::class)}.default))
                    println(Q123.random())
                }
                @Randomizable
                data class Q123(val i:Int, val l:Long)
            """,
            fileName = "main.kt"
        ) {
            testCompilation = { result,_ ->
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                result.runMain()
            }
        }
    }



    @Test
    fun `class with legal custom random config class via annotation`() {

        RandomConfigImp
        testGeneratedCodeUsingStandardPlugin(
            """
                ${TestImportsBuilder.stdImport}

                fun main(){
                    println(Q123.random(LegalRandomConfig()))
                    println(Q123.random())
                }
                @Randomizable(randomConfig = LegalRandomConfig::class)
                data class Q123(val i:Int)
            """,
            fileName = "main.kt"
        ) {
            testCompilation = { result,_ ->
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                result.runMain()
            }
        }
    }

    @Test
    fun `class with legal custom random config object via annotation`() {

        RandomConfigImp
        testGeneratedCodeUsingStandardPlugin(
            """
                ${TestImportsBuilder.stdImport}

                fun main(){
                    println(Q123.random(LegalRandomConfigObject))
                    println(Q123.random())
                }
                @Randomizable(randomConfig = LegalRandomConfigObject::class)
                data class Q123(val i:Int)
            """,
            fileName = "main.kt"
        ) {
            testCompilation = { result,_ ->
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                result.runMain()
            }
        }
    }


    @Test
    fun `class with illegal random config via annotation`() {

        RandomConfigImp
        testGeneratedCodeUsingStandardPlugin(
            """
                import com.x12q.randomizer.lib.DefaultRandomConfig
                import com.x12q.randomizer.lib.annotations.Randomizable
                import com.x12q.randomizer.ir_plugin.mock_objects.IllegalRandomConfig

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
