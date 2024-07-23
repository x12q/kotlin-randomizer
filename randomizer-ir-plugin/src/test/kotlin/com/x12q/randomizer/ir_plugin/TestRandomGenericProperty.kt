package com.x12q.randomizer.ir_plugin

import com.tschuchort.compiletesting.KotlinCompilation
import com.x12q.randomizer.DefaultRandomConfig
import com.x12q.randomizer.RandomConfig
import com.x12q.randomizer.annotations.Randomizable
import com.x12q.randomizer.ir_plugin.mock_objects.AlwaysFalseRandomConfig
import com.x12q.randomizer.ir_plugin.mock_objects.AlwaysTrueRandomConfig
import com.x12q.randomizer.ir_plugin.mock_objects.LegalRandomConfigObject
import com.x12q.randomizer.ir_plugin.mock_objects.RandomConfigForTest
import com.x12q.randomizer.test.util.assertions.runMain
import com.x12q.randomizer.test.util.assertions.runMain2
import io.kotest.matchers.shouldBe
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import kotlin.test.Test

@OptIn(ExperimentalCompilerApi::class)
class TestRandomGenericProperty {


    @Test
    fun `nullable generic property - always null x`() {

        testGeneratedCodeUsingStandardPlugin(
            """
                import com.x12q.randomizer.DefaultRandomConfig
                import com.x12q.randomizer.annotations.Randomizable
                import com.x12q.randomizer.ir_plugin.mock_objects.LegalRandomConfigObject
                import com.x12q.randomizer.ir_plugin.mock_objects.AlwaysFalseRandomConfig
                import com.x12q.randomizer.ir_plugin.mock_objects.AlwaysTrueRandomConfig
                import com.x12q.randomizer.test.util.TestOutput
                import com.x12q.randomizer.test.util.withTestOutput
                
                fun main2():TestOutput{
                    return withTestOutput{
                        printOutput(Qx.random<Int>(AlwaysFalseRandomConfig, randomT1={it.nextInt()}))
                        printOutput(Qx.random<Int>(AlwaysTrueRandomConfig, randomT1={it.nextInt()}))
                    }
                }
                @Randomizable
                data class Qx<T1>(val i:T1?)
            """,
            fileName = "main.kt",
        ) {
            testCompilation = { result,testStream->
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                result.runMain2().getStr() shouldBe """
                    Qx(i=null)
                    Qx(i=${AlwaysTrueRandomConfig.nextInt()})
                """.trimIndent()
            }
        }
    }

    @Test
    fun `nullable generic property - always null`() {

        testGeneratedCodeUsingStandardPlugin(
            """
                import com.x12q.randomizer.DefaultRandomConfig
                import com.x12q.randomizer.annotations.Randomizable
                import com.x12q.randomizer.ir_plugin.mock_objects.LegalRandomConfigObject
                import com.x12q.randomizer.ir_plugin.mock_objects.AlwaysFalseRandomConfig
                import com.x12q.randomizer.ir_plugin.mock_objects.AlwaysTrueRandomConfig
                import com.x12q.randomizer.test.util.TestOutput
                import com.x12q.randomizer.test.util.withTestOutput

                 fun main2():TestOutput{
                    return withTestOutput{
                        printOutput(Qx.random<Int>(AlwaysFalseRandomConfig, randomT1={it.nextInt()}))
                        printOutput(Qx.random<Int>(AlwaysTrueRandomConfig, randomT1={it.nextInt()}))
                    }
                }
                @Randomizable
                data class Qx<T1>(val i:T1?)
            """,
            fileName = "main.kt",
        ) {
            testCompilation = { result,testStream->
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                result.runMain2().getStr() shouldBe """
                    Qx(i=null)
                    Qx(i=${AlwaysTrueRandomConfig.nextInt()})
                """.trimIndent()
            }
        }
    }

    @Test
    fun `class with generic property + legal random config object in annotation`() {

        testGeneratedCodeUsingStandardPlugin(
            """
                import com.x12q.randomizer.DefaultRandomConfig
                import com.x12q.randomizer.annotations.Randomizable
                import com.x12q.randomizer.ir_plugin.mock_objects.LegalRandomConfigObject
                import com.x12q.randomizer.test.util.TestOutput
                import com.x12q.randomizer.test.util.withTestOutput

                fun main2():TestOutput{
                    return withTestOutput{
                        printOutput(Qx.random<Int>(randomT1={it.nextInt()}))
                        printOutput(Qx.random<Int>(LegalRandomConfigObject,randomT1={it.nextInt()}))
                    }
                }
                @Randomizable(randomConfig = LegalRandomConfigObject::class)
                data class Qx<T1>(val i:T1)
            """,
            fileName = "main.kt"
        ) {
            testCompilation = { result,_->
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                result.runMain2().getStr() shouldBe """
                    Qx(i=${LegalRandomConfigObject.nextInt()})
                    Qx(i=${LegalRandomConfigObject.nextInt()})
                """.trimIndent()
            }
        }
    }


    @Test
    fun `randomize 3 generic property`() {

        testGeneratedCodeUsingStandardPlugin(
            """
                import com.x12q.randomizer.DefaultRandomConfig
                import com.x12q.randomizer.annotations.Randomizable
                import com.x12q.randomizer.test.util.TestOutput
                import com.x12q.randomizer.test.util.withTestOutput
                import com.x12q.randomizer.ir_plugin.mock_objects.RandomConfigForTest

                fun main2():TestOutput{
                    return withTestOutput{
                        printOutput(Qx.random<Int,String,Double>(
                            randomT1={123}, 
                            randomT2={config-> 
                                val num=config.nextInt()
                                "abc_"+num.toString()
                            }, 
                            randomT3 = {1.23}
                            )
                        )
                    }
                }
                @Randomizable(randomConfig = RandomConfigForTest::class)
                data class Qx<T1,T2,T3>(val i1:T1,val i2:T2, val i3:T3)
            """,
            fileName = "main.kt"
        ) {
            testCompilation = { result,_->
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                result.runMain2().getStr() shouldBe """
                    Qx(i1=123, i2=abc_${RandomConfigForTest.nextInt()}, i3=1.23)
                """.trimIndent()
            }
        }
    }

    @Test
    fun `randomize 1 generic property`() {

        testGeneratedCodeUsingStandardPlugin(
            """
                import com.x12q.randomizer.DefaultRandomConfig
                import com.x12q.randomizer.annotations.Randomizable
                import com.x12q.randomizer.RandomConfig
                import com.x12q.randomizer.test.util.TestOutput
                import com.x12q.randomizer.test.util.withTestOutput
                import com.x12q.randomizer.ir_plugin.mock_objects.RandomConfigForTest

                fun main2():TestOutput{
                    return withTestOutput{
                        printOutput(Qx.random<Int>({config->println(config);config.nextInt()}))
                        printOutput(Qx.random<Int>({123}))
                        printOutput(Qx.random<Int>(randomT1={123}))
                    }
                }
                @Randomizable(randomConfig = RandomConfigForTest::class)
                data class Qx<T1>(val i:T1)
            """,
            fileName = "main.kt"
        ) {
            testCompilation = { result,_->
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                result.runMain2().getStr() shouldBe """
                    Qx(i=${RandomConfigForTest.nextInt()})
                    Qx(i=123)
                    Qx(i=123)
                """.trimIndent()
            }
        }
    }
}
