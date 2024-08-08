package com.x12q.randomizer.ir_plugin

import com.tschuchort.compiletesting.KotlinCompilation
import com.x12q.randomizer.ir_plugin.mock_objects.AlwaysTrueRandomConfig
import com.x12q.randomizer.ir_plugin.mock_objects.LegalRandomConfigObject
import com.x12q.randomizer.ir_plugin.mock_objects.RandomConfigForTest
import com.x12q.randomizer.test.util.WithData
import com.x12q.randomizer.test.util.assertions.runRunTest
import com.x12q.randomizer.test.util.test_code.TestImportsBuilder
import io.kotest.matchers.shouldBe
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import kotlin.test.Test


@OptIn(ExperimentalCompilerApi::class)
class TestRandomGenericProperty {

    data class Qx<T1>(val i: T1?)
    data class Qx2<T1>(val i: T1)

    @Test
    fun `prioritize generic function over random context`() {

        testGeneratedCodeUsingStandardPlugin(
            """
                ${TestImportsBuilder.stdImport.import(Qx2::class)}

                fun runTest():TestOutput{
                    return withTestOutput{
                        putData(QxC.random<Int>(randomT1={LegalRandomConfigObject.nextInt()+100}, randomizers = {
                            add(ConstantClassRandomizer(LegalRandomConfigObject.nextInt()-100,Int::class))
                        }))
                    }
                }
                @Randomizable(randomConfig = LegalRandomConfigObject::class)
                data class QxC<T1>(override val data:Qx2<T1>):WithData
            """,
        ) {
            testCompilation = { result, _ ->
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                result.runRunTest().getObjs() shouldBe listOf(
                    Qx2(LegalRandomConfigObject.nextInt()+100),
                )
            }
        }
    }

    @Test
    fun `nullable generic property`() {

        testGeneratedCodeUsingStandardPlugin(
            """
                ${TestImportsBuilder.stdImport.import(Qx::class)}
                fun runTest():TestOutput{
                    return withTestOutput{
                        putData(QxC.random<Int>(AlwaysFalseRandomConfig, randomT1={nextInt()}))
                        putData(QxC.random<Int>(AlwaysTrueRandomConfig, randomT1={nextInt()}))
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
    fun `accessing random logic of RandomContext from generic function`() {

        testGeneratedCodeUsingStandardPlugin(
            """
                ${TestImportsBuilder.stdImport.import(Qx2::class)}

                fun runTest():TestOutput{
                    return withTestOutput{
                        putData(QxC.random<Int>(randomT1={nextInt()}))
                        putData(QxC.random<Int>(LegalRandomConfigObject,randomT1={nextInt()}))
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


    @Test
    fun `randomize 3 generic property`() {

        testGeneratedCodeUsingStandardPlugin(
            """
                ${TestImportsBuilder.stdImport.import(Qx3::class)}

                fun runTest():TestOutput{
                    return withTestOutput{
                        putData(QxC.random<Int,String,Double>(
                                randomT1={123}, 
                                randomT2={
                                    val config = this
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
                ${TestImportsBuilder.stdImport.import(Qx::class)}

                fun runTest():TestOutput{
                    return withTestOutput{
                        putData(QxC.random<Int>({val config=this;println(config);config.nextInt()}))
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
                val o = result.runRunTest().getObjs()
                o shouldBe listOf(
                    Qx(i = RandomConfigForTest.nextInt()),
                    Qx(i = 123),
                    Qx(i = 123),
                )
                println(o)
            }
        }
    }

//    data class QxC<T1:Number>(override val data:Qx<T1>):WithData

    @Test
    fun `randomize 1 generic property with bound - ok case`() {
        testGeneratedCodeUsingStandardPlugin(
            """
                ${TestImportsBuilder.stdImport.import(Qx::class)}

                fun runTest():TestOutput{
                    return withTestOutput{
                        putData(QxC.random<Int>(randomT1={-999}))
                        putData(QxC.random<Float>(randomT1={-31f}))
                    }
                }
                @Randomizable(randomConfig = RandomConfigForTest::class)
                data class QxC<T1:Number>(override val data:Qx<T1>):WithData
            """,
        ) {
            testCompilation = { result, _ ->
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                val o = result.runRunTest().getObjs()
                o shouldBe listOf(
                    Qx(i = -999),
                    Qx(i = -31f),
                )
            }
        }
    }

    @Test
    fun `randomize 1 generic property with bound - fail case`() {
        testGeneratedCodeUsingStandardPlugin(
            """
                ${TestImportsBuilder.stdImport.import(Qx::class)}

                fun runTest():TestOutput{
                    return withTestOutput{
                        putData(QxC.random<String>(randomT1={"zzzz"}))
                    }
                }
                @Randomizable(randomConfig = RandomConfigForTest::class)
                data class QxC<T1:Number>(override val data:Qx<T1>):WithData
            """,
        ) {
            testCompilation = { result, _ ->
                result.exitCode shouldBe KotlinCompilation.ExitCode.COMPILATION_ERROR
            }
        }
    }

}
