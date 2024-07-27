package com.x12q.randomizer.ir_plugin

import com.tschuchort.compiletesting.KotlinCompilation
import com.x12q.randomizer.ir_plugin.mock_objects.AlwaysTrueRandomConfig
import com.x12q.randomizer.ir_plugin.mock_objects.LegalRandomConfigObject
import com.x12q.randomizer.ir_plugin.mock_objects.RandomConfigForTest
import com.x12q.randomizer.test.util.WithData
import com.x12q.randomizer.test.util.assertions.runRunTest
import io.kotest.matchers.shouldBe
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import kotlin.test.Test


@OptIn(ExperimentalCompilerApi::class)
class TestRandomGenericProperty {

    data class Qx<T1>(val i: T1?)
    data class Qx2<T1>(val i: T1)

    val withData = WithData::class.qualifiedName!!
    val qx = Qx::class.qualifiedName!!
    val qx2 = Qx2::class.qualifiedName!!

    @Test
    fun `nullable generic property`() {

        testGeneratedCodeUsingStandardPlugin(
            """
                import com.x12q.randomizer.DefaultRandomConfig
                import com.x12q.randomizer.annotations.Randomizable
                import com.x12q.randomizer.ir_plugin.mock_objects.LegalRandomConfigObject
                import com.x12q.randomizer.ir_plugin.mock_objects.AlwaysFalseRandomConfig
                import com.x12q.randomizer.ir_plugin.mock_objects.AlwaysTrueRandomConfig
                import com.x12q.randomizer.test.util.TestOutput
                import com.x12q.randomizer.test.util.withTestOutput
                import $qx
                import $withData
                fun runTest():TestOutput{
                    return withTestOutput{
                        putData(QxC.random<Int>(AlwaysFalseRandomConfig, randomT1={it.nextInt()}))
                        putData(QxC.random<Int>(AlwaysTrueRandomConfig, randomT1={it.nextInt()}))
                    }
                }
                @Randomizable
                data class QxC<T1>(override val data:Qx<T1>):WithData
            """,
        ) {
            testCompilation = { result, testStream ->
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                val l = result.runRunTest().getObjs()
                l shouldBe listOf(
                    Qx<Int>(null), Qx(AlwaysTrueRandomConfig.nextInt())
                )
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
                import $qx2
                import $withData

                fun runTest():TestOutput{
                    return withTestOutput{
                        putData(QxC.random<Int>(randomT1={it.nextInt()}))
                        putData(QxC.random<Int>(LegalRandomConfigObject,randomT1={it.nextInt()}))
                    }
                }
                @Randomizable(randomConfig = LegalRandomConfigObject::class)
                data class QxC<T1>(override val data:Qx2<T1>):WithData
            """,
        ) {
            testCompilation = { result, _ ->
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                result.runRunTest().getObjs() shouldBe listOf(
                    Qx2(LegalRandomConfigObject.nextInt()),
                    Qx2(LegalRandomConfigObject.nextInt())
                )
            }
        }
    }


    data class Qx3<T1, T2, T3>(val i1: T1, val i2: T2, val i3: T3)

    val qx3 = Qx3::class.qualifiedName!!

    @Test
    fun `randomize 3 generic property`() {

        testGeneratedCodeUsingStandardPlugin(
            """
                import com.x12q.randomizer.DefaultRandomConfig
                import com.x12q.randomizer.annotations.Randomizable
                import com.x12q.randomizer.test.util.TestOutput
                import com.x12q.randomizer.test.util.withTestOutput
                import com.x12q.randomizer.ir_plugin.mock_objects.RandomConfigForTest
                import $qx3
                import $withData

                fun runTest():TestOutput{
                    return withTestOutput{
                        putData(QxC.random<Int,String,Double>(
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
                data class QxC<T1,T2,T3>(override val data:Qx3<T1,T2,T3>):WithData
            """,
        ) {
            testCompilation = { result, _ ->
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                result.runRunTest().getObjs() shouldBe listOf(
                    Qx3(123, "abc_${RandomConfigForTest.nextInt()}", 1.23)
                )
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
                import $qx
                import $withData
                fun runTest():TestOutput{
                    return withTestOutput{
                        putData(QxC.random<Int>({config->println(config);config.nextInt()}))
                        putData(QxC.random<Int>(randomT1={123}))
                        putData(QxC.random<Int>(randomT1={123}))
                    }
                }
                @Randomizable(randomConfig = RandomConfigForTest::class)
                data class QxC<T1>(override val data:Qx<T1>):WithData
            """,
        ) {
            testCompilation = { result, _ ->
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                result.runRunTest().getObjs() shouldBe listOf(
                    Qx(i = RandomConfigForTest.nextInt()),
                    Qx(i = 123),
                    Qx(i = 123),
                )
            }
        }
    }
}
