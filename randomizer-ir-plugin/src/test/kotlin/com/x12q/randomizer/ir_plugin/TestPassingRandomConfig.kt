package com.x12q.randomizer.ir_plugin

import com.tschuchort.compiletesting.KotlinCompilation
import com.x12q.randomizer.DefaultRandomConfig
import com.x12q.randomizer.test.util.assertions.runMain
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
    fun `class with no RandomConfig`() {

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
    fun `class with legal custom random config class via annotation`() {

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
    fun `class with legal custom random config object via annotation`() {

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
    fun `class with illegal random config via annotation`() {

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
