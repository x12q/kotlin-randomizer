package com.x12q.randomizer.ir_plugin

import com.tschuchort.compiletesting.KotlinCompilation
import com.x12q.randomizer.DefaultRandomConfig
import com.x12q.randomizer.test.util.assertions.runMain
import io.kotest.matchers.shouldBe
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import kotlin.test.Test




@OptIn(ExperimentalCompilerApi::class)
class TestRandomNestedClass {

    @Test
    fun `randomize nested object`() {

        testGeneratedCodeUsingStandardPlugin(
            """
                import com.x12q.randomizer.DefaultRandomConfig
                import com.x12q.randomizer.annotations.Randomizable

                fun main(){
                    println(Q123.random())
                    println(Q123.random(DefaultRandomConfig.default))

                    val q = Q123.random()
                    println(q.b1.i.toString())
                    println(q.c2.c.toString())
                }

                @Randomizable
                data class Q123(
                    val boolean: Boolean,
                    val b1:B1,
                    val c2:C2,
                )

                data object B1{
                    val i = 123
                }

                object C2{
                    val c = "ccc"
                }
            """,
            fileName = "main.kt"
        ) {
            testCompilation = { result->
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                result.runMain()
            }
        }
    }

    @Test
    fun `randomize nested enum`() {

        testGeneratedCodeUsingStandardPlugin(
            """
                import com.x12q.randomizer.DefaultRandomConfig
                import com.x12q.randomizer.annotations.Randomizable

                fun main(){
                    println(Q123.random())
                    println(Q123.random())
                    println(Q123.random())
                }

                @Randomizable
                data class Q123(
                    val boolean: Boolean,
                    val enum:MyEnumClass,
                )

                enum class MyEnumClass{
                    V1,V2,V3,V4,V5,V6
                }
            """,
            fileName = "main.kt"
        ) {
            testCompilation = { result->
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                result.runMain()
            }
        }
    }





}

