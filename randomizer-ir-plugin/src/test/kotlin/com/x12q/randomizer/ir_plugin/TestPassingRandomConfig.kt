package com.x12q.randomizer.ir_plugin

import com.tschuchort.compiletesting.KotlinCompilation
import com.x12q.randomizer.DefaultRandomConfig
import com.x12q.randomizer.test.util.assertions.runMain
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import kotlin.test.Test

/**
 * Test the random config
 */
@OptIn(ExperimentalCompilerApi::class)
class TestPassingRandomConfig{
    @Test
    fun `empty class with a defined RandomConfig object`() {

        DefaultRandomConfig
        testGeneratedCodeUsingStandardPlugin(
            """
                import com.x12q.randomizer.DefaultRandomConfig
                import com.x12q.randomizer.annotations.Randomizable

                fun main(){
                    println(Q123.random(DefaultRandomConfig.default))
                    println(Q123.random())
                }
                @Randomizable(
                    randomConfig = DefaultRandomConfig::class
                )
                data class Q123(val i:Int)
            """,
            fileName = "main.kt"
        ) {
            afterVisitClassNew = { irClass, statement, irPluginContext ->
                if (irClass.name.toString() == "Q123") {

                }
            }
            testCompilation = { result ->
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                result.runMain()
            }
        }
    }

    @Test
    fun `empty class with default RandomConfig`() {

        DefaultRandomConfig
        testGeneratedCodeUsingStandardPlugin(
            """
                import com.x12q.randomizer.DefaultRandomConfig
                import com.x12q.randomizer.annotations.Randomizable

                fun main(){
                    println(Q123.random(DefaultRandomConfig.default))
                    println(Q123.random())
                }
                @Randomizable
                data class Q123(val i:Int, val l:Long)
            """,
            fileName = "main.kt"
        ) {
            testCompilation = { result ->
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                result.runMain()
            }
        }
    }



    @Test
    fun `empty class with custom legal random config class`() {

        DefaultRandomConfig
        testGeneratedCodeUsingStandardPlugin(
            """
                import com.x12q.randomizer.DefaultRandomConfig
                import com.x12q.randomizer.annotations.Randomizable
                import com.x12q.randomizer.ir_plugin.mock_objects.LegalRandomConfig

                fun main(){
                    println(Q123.random(LegalRandomConfig()))
                    println(Q123.random())
                }
                @Randomizable(randomConfig = LegalRandomConfig::class)
                data class Q123(val i:Int)
            """,
            fileName = "main.kt"
        ) {
            testCompilation = { result ->
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                result.runMain()
            }
        }
    }

    @Test
    fun `empty class with custom legal random config object`() {

        DefaultRandomConfig
        testGeneratedCodeUsingStandardPlugin(
            """
                import com.x12q.randomizer.DefaultRandomConfig
                import com.x12q.randomizer.annotations.Randomizable
                import com.x12q.randomizer.ir_plugin.mock_objects.LegalRandomConfigObject

                fun main(){
                    println(Q123.random(LegalRandomConfigObject))
                    println(Q123.random())
                }
                @Randomizable(randomConfig = LegalRandomConfigObject::class)
                data class Q123(val i:Int)
            """,
            fileName = "main.kt"
        ) {
            testCompilation = { result ->
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                result.runMain()
            }
        }
    }


    @Test
    fun `empty class with custom illegal random config`() {

        DefaultRandomConfig
        testGeneratedCodeUsingStandardPlugin(
            """
                import com.x12q.randomizer.DefaultRandomConfig
                import com.x12q.randomizer.annotations.Randomizable
                import com.x12q.randomizer.ir_plugin.mock_objects.IllegalRandomConfig

                fun main(){
                    println(Q123.random())
                }
                @Randomizable(randomConfig = IllegalRandomConfig::class)
                data class Q123(val i:Int)
            """,
            fileName = "main.kt"
        ) {
            testCompilation = { result ->
                result.exitCode shouldNotBe KotlinCompilation.ExitCode.OK
            }
        }
    }
}
