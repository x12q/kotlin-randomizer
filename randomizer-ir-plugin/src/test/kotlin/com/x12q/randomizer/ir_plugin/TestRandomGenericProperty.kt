package com.x12q.randomizer.ir_plugin

import com.tschuchort.compiletesting.KotlinCompilation
import com.x12q.randomizer.test.util.assertions.runMain
import io.kotest.matchers.shouldBe
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import kotlin.test.Test

@OptIn(ExperimentalCompilerApi::class)
class TestRandomGenericProperty {

    @Test
    fun `class with generic property + legal random config object in annotation`() {

        testGeneratedCodeUsingStandardPlugin(
            """
                import com.x12q.randomizer.DefaultRandomConfig
                import com.x12q.randomizer.annotations.Randomizable
                import com.x12q.randomizer.ir_plugin.mock_objects.LegalRandomConfigObject
                fun main(){
                    println(Qx.random<Int>(randomT1={123}))
                    println(Qx.random<Int>(LegalRandomConfigObject,randomT1={123}))
                }
                @Randomizable(randomConfig = LegalRandomConfigObject::class)
                data class Qx<T1>(val i:T1)
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
    fun `randomize 3 generic property`() {

        testGeneratedCodeUsingStandardPlugin(
            """
                import com.x12q.randomizer.DefaultRandomConfig
                import com.x12q.randomizer.annotations.Randomizable

                fun main(){
                    println(Qx.random<Int,String,Double>(randomT1={123}, randomT2={"abc"}, randomT3 = {1.23}))
                }
                @Randomizable
                data class Qx<T1,T2,T3>(val i1:T1,val i2:T2, val i3:T3)
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
    fun `randomize 1 generic property`() {

        testGeneratedCodeUsingStandardPlugin(
            """
                import com.x12q.randomizer.DefaultRandomConfig
                import com.x12q.randomizer.annotations.Randomizable

                fun main(){
                    println(Qx.randomSample<Int>({123}))
                    println(Qx.random<Int>(randomT1={123}))
                }
                @Randomizable
                data class Qx<T1>(val i:T1){
                    companion object{
                        fun <Tx>randomSample(randomTx:()->Tx):Qx<Tx>{
                            return Qx(randomTx())
                        }
                    }
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
