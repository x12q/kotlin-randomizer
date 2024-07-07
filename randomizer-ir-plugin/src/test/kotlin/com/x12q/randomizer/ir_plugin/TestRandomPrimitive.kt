package com.x12q.randomizer.ir_plugin

import com.tschuchort.compiletesting.KotlinCompilation
import com.x12q.randomizer.DefaultRandomConfig
import com.x12q.randomizer.test.util.assertions.runMain
import io.kotest.matchers.shouldBe
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import kotlin.test.Test




@OptIn(ExperimentalCompilerApi::class)
class TestRandomPrimitive {

    data class A(
        val uInt: UInt
    )
    @Test
    fun qwe(){
        println(A(123.toUInt()))
    }

    @Test
    fun `randomize primitive parameter with default RandomConfig`() {

        testGeneratedCodeUsingStandardPlugin(
            """
                import com.x12q.randomizer.DefaultRandomConfig
                import com.x12q.randomizer.annotations.Randomizable

                fun main(){
                    println(Q123.random())
                    println(Q123.random(DefaultRandomConfig.default))
                }

                @Randomizable
                data class Q123(
                    val boolean: Boolean,
                    val int:Int,
                    val long:Long,
                    val float:Float,
                    val double:Double,
                    val byte:Byte,
                    val char:Char,
                    val short: Short,
                    val string:String,
                    val number:Number,
                    val unit:Unit,
                    val any:Any,
                )
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
    fun `randomize primitive U parameter with default RandomConfig`() {

        testGeneratedCodeUsingStandardPlugin(
            """
                import com.x12q.randomizer.DefaultRandomConfig
                import com.x12q.randomizer.annotations.Randomizable
                import kotlin.UInt

                fun main(){
                    println(Q123.random(DefaultRandomConfig.default))
                    println(Q123.random())
                }

                @Randomizable
                data class Q123(
                    val uint:UInt,
                    val ulong:ULong,
                    val ubyte:UByte,
                    val ushort: UShort,
                )
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
    fun `randomize nullable primitive parameter with default RandomConfig`() {

        testGeneratedCodeUsingStandardPlugin(
            """
                import com.x12q.randomizer.DefaultRandomConfig
                import com.x12q.randomizer.annotations.Randomizable
                import com.x12q.randomizer.ir_plugin.mock_objects.RandomConfigForTest

                fun main(){
                    for(x in 1 .. 10){
                        println(Q123.random())
                        println(Q123.random(RandomConfigForTest))                    
                        println(Q123.random(RandomConfigForTest))
                    }
                }

                @Randomizable
                data class Q123(
                    val int:Int?,
                    val boolean: Boolean?,
                    val long:Long?,
                    val float:Float?,
                    val double:Double?,
                    val byte:Byte?,
                    val char:Char?,
                    val short: Short?,
                    val string:String?,
                    val number:Number?,
                    val unit:Unit?,
                    val any:Any?,
                )
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
    fun `randomize primitive Nothing`() {

        testGeneratedCodeUsingStandardPlugin(
            """
                import com.x12q.randomizer.DefaultRandomConfig
                import com.x12q.randomizer.annotations.Randomizable

                fun main(){
                    println(Q123.random())
                    println(Q123.random(DefaultRandomConfig.default))
                }

                @Randomizable
                data class Q123(
                    val nt:Nothing,
                )
            """,
            fileName = "main.kt"
        ) {
            testCompilation = { result->
                result.exitCode shouldBe KotlinCompilation.ExitCode.INTERNAL_ERROR
            }
        }
    }
    @Test
    fun `randomize primitive Nothing nullable`() {

        testGeneratedCodeUsingStandardPlugin(
            """
                import com.x12q.randomizer.DefaultRandomConfig
                import com.x12q.randomizer.annotations.Randomizable

                fun main(){
                    println(Q123.random())
                    println(Q123.random(DefaultRandomConfig.default))
                }

                @Randomizable
                data class Q123(
                    val nt:Nothing?,
                )
            """,
            fileName = "main.kt"
        ) {
            testCompilation = { result->
                result.exitCode shouldBe KotlinCompilation.ExitCode.INTERNAL_ERROR
            }
        }
    }

    @Test
    fun `randomize nullable U primitive`() {

        testGeneratedCodeUsingStandardPlugin(
            """
                import com.x12q.randomizer.DefaultRandomConfig
                import com.x12q.randomizer.annotations.Randomizable

                fun main(){
                    println(Q123.random())
                    println(Q123.random(DefaultRandomConfig.default))
                }

                @Randomizable
                data class Q123(
                   val uint:UInt?,
                    val ulong:ULong?,
                    val ubyte:UByte?,
                    val ushort: UShort?,
                )
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
