package com.x12q.randomizer.ir_plugin

import com.tschuchort.compiletesting.KotlinCompilation
import com.x12q.randomizer.ir_plugin.mock_objects.AlwaysTrueRandomConfig
import com.x12q.randomizer.lib.RandomConfigImp
import com.x12q.randomizer.test.util.assertions.runMain
import com.x12q.randomizer.test.util.test_code.TestImportsBuilder
import io.kotest.matchers.shouldBe
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import kotlin.test.Test




@OptIn(ExperimentalCompilerApi::class)
class TestRandomNestedClass {

    @Test
    fun `randomize nullable nested object - always not null`() {
        testGeneratedCodeUsingStandardPlugin(
            """
                ${TestImportsBuilder.stdImport}

                fun main(){
                    println(Q123.random(AlwaysTrueRandomConfig))
                }

                @Randomizable
                data class Q123(
                    val b1:B1?,
                    val c2:C2?,
                    val d2:D2?,   
                )

                data object B1{
                    val i = 123
                }

                object C2{
                    val c = "ccc"
                }
                data class D2(val x:Int)
            """,
            fileName = "main.kt"
        ) {
            testCompilation = { result,_->
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                result.runMain()
            }
        }
    }


    @Test
    fun `randomize nullable nested object - always null`() {
        AlwaysTrueRandomConfig
        testGeneratedCodeUsingStandardPlugin(
            """
                ${TestImportsBuilder.stdImport}

                fun main(){
                    println(Q123.random(AlwaysFalseRandomConfig))
                }

                @Randomizable
                data class Q123(
                    val b1:B1?,
                    val c2:C2?,
                    val d2:D2?,
                )

                data object B1{
                    val i = 123
                }

                object C2{
                    val c = "ccc"
                }

                data class D2(val x:Int)
            """,
            fileName = "main.kt"
        ) {
            testCompilation = { result,_->
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                result.runMain()
            }
        }
    }


    @Test
    fun `randomize nested object`() {

        testGeneratedCodeUsingStandardPlugin(
            """
                ${TestImportsBuilder.stdImport}

                fun main(){
                    println(Q123.random())
                    println(Q123.random(${TestImportsBuilder.stdImport.nameOf(RandomConfigImp::class)}.default))

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
            testCompilation = { result,_->
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                result.runMain()
            }
        }
    }

    @Test
    fun `randomize nested enum`() {

        testGeneratedCodeUsingStandardPlugin(
            """
                ${TestImportsBuilder.stdImport}

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
            testCompilation = { result,_->
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                result.runMain()
            }
        }
    }

    @Test
    fun `randomize nested concrete class`() {

        testGeneratedCodeUsingStandardPlugin(
            """
                
                ${TestImportsBuilder.stdImport}

                fun main(){
                    println(Q123.random())
                    println(Q123.random())
                    println(Q123.random())
                }

                @Randomizable
                data class Q123(
                    val a:AA
                )

                data class AA(val int:Int, val bb:BB, val c:CC)
                data class BB(val str:String,val cc:CC)
                data class CC(val aa:Float)

            """,
            fileName = "main.kt"
        ) {
            testCompilation = { result,_->
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                result.runMain()
            }
        }
    }
}


