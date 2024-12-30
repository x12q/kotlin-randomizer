package com.x12q.kotlin.randomizer.ir_plugin

import com.tschuchort.compiletesting.KotlinCompilation
import com.x12q.kotlin.randomizer.ir_plugin.mock_objects.TestRandomConfig
import com.x12q.kotlin.randomizer.ir_plugin.mock_objects.TestRandomConfigWithRandomizableCandidateIndex
import com.x12q.kotlin.randomizer.lib.UnableToMakeRandomException
import com.x12q.kotlin.randomizer.lib.annotations.Randomizable
import com.x12q.kotlin.randomizer.test.util.WithData
import com.x12q.kotlin.randomizer.test.util.assertions.executeRunTestFunction
import com.x12q.kotlin.randomizer.test.util.test_code.TestImportsBuilder
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import kotlin.test.Test


@OptIn(ExperimentalCompilerApi::class)
class TestRandomizingInterface {

    @Randomizable(
        candidates = [PlainImplementation_2::class, PlainImplementation_1::class],
    )
    interface PlainInterface {
        val str: String
        val d: Double
    }

    data class PlainImplementation_1(override val str: String, override val d: Double) : PlainInterface

    object PlainImplementation_2 : PlainInterface {
        override val d: Double = 222.0
        override val str: String = "zzz"
    }

    private val imports = TestImportsBuilder.stdImport
        .import(QxC::class)
        .import(PlainInterface::class)
        .import(PlainImplementation_1::class)
        .import(PlainImplementation_2::class)
        .import(GenericInterface::class)
        .import(GenericImplementation_1::class)
        .import(GenericImplementation_2::class)
        .import(GenericImplementation_3::class)

    data class QxC<K_Q : Any>(override val data: K_Q) : WithData

    @Test
    fun `custom randomizer for interface using custom randomizer`() {
        testGeneratedCodeUsingStandardPlugin(
            """
                $imports

                fun runTest():TestOutput {
                    return withTestOutput {
                        putData(random<QxC<PlainInterface>>(randomConfig=TestRandomConfig(), randomizers = {
                            constant<PlainInterface>(PlainImplementation_2)
                        }))

                        putData(random<QxC<PlainInterface>>(randomConfig=TestRandomConfig(), randomizers = {
                            constant<PlainInterface>{
                                random<PlainImplementation_1>()
                            }
                        }))
                    }
                }
            """,
        ) {
            testCompilation = { result, _ ->
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                val objectList = result.executeRunTestFunction().getObjs()
                val t = TestRandomConfig()
                objectList shouldBe listOf(
                    PlainImplementation_2,
                    PlainImplementation_1(
                        str = t.nextString(),
                        d = t.nextDouble()
                    )
                )
            }
        }
    }


    /**
     * This feature is not supported, so the code should throw an exception
     */
    @Test
    fun `generate random plain interface without providing custom randomizer`() {
        testGeneratedCodeUsingStandardPlugin(
            """
                $imports

                fun runTest():TestOutput {
                    return withTestOutput {
                        putData(random<QxC<PlainInterface>>(randomConfig=TestRandomConfigWithRandomizableCandidateIndex(0)))
                        putData(random<QxC<PlainInterface>>(randomConfig=TestRandomConfigWithRandomizableCandidateIndex(1)))
                    }
                }
            """,
        ) {
            testCompilation = { result, _ ->
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                shouldThrow<UnableToMakeRandomException> {
                    result.executeRunTestFunction()
                }
            }
        }
    }

    @Randomizable(
        candidates = [GenericImplementation_1::class, GenericImplementation_2::class],
    )
    interface GenericInterface<T1, T2> {
        val t1: T1
        val t2: T2
    }

    data class GenericImplementation_1<T_0>(override val t1: String, override val t2: T_0) :
        GenericInterface<String, T_0>

    data class GenericImplementation_2<E_0, E_1>(override val t1: E_1, override val t2: E_0) :
        GenericInterface<E_1, E_0>

    data class GenericImplementation_3<H_0, H_1, H_3>(override val t1: H_1, override val t2: H_0, val t3: H_3) :
        GenericInterface<H_1, H_0>

    @Test
    fun `generate random generic interface using custom randomizers`() {
        testGeneratedCodeUsingStandardPlugin(
            """
                $imports

                fun runTest():TestOutput {
                    return withTestOutput {
                        putData(random<QxC<GenericInterface<String,Double>>>(randomConfig=TestRandomConfig(), randomizers = {
                            constant<GenericInterface<String,Double>>{
                                random<GenericImplementation_1<Double>>()
                            }
                        }))
                        putData(random<QxC<GenericInterface<String,Double>>>(randomConfig=TestRandomConfig(), randomizers = {
                            constant<GenericInterface<String,Double>>{
                                random<GenericImplementation_2<Double,String>>()
                            }
                        }))

                        putData(random<QxC<GenericInterface<String,Double>>>(randomConfig=TestRandomConfig(), randomizers = {
                            constant<GenericInterface<String,Double>>{
                                random<GenericImplementation_3<Double, String,Float>>()
                            }
                        }))
                    }
                }
            """,
        ) {
            testCompilation = { result, _ ->
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                val objectList = result.executeRunTestFunction().getObjs()

                objectList shouldBe listOf(
                    run {
                        val cf = TestRandomConfig()
                        GenericImplementation_1(t1 = cf.nextString(), t2 = cf.nextDouble())
                    },
                    run {
                        val cf = TestRandomConfig()
                        GenericImplementation_2(t1 = cf.nextString(), t2 = cf.nextDouble())
                    },
                    run {
                        val cf = TestRandomConfig()
                        GenericImplementation_3(t1 = cf.nextString(), t2 = cf.nextDouble(), t3 = cf.nextFloat())
                    },
                )
            }
        }
    }


    /**
     * This feature is not supported, so the code should throw an exception
     */
    @Test
    fun `generate random generic interface without providing custom randomizers`() {
        testGeneratedCodeUsingStandardPlugin(
            """
                $imports

                fun runTest():TestOutput {
                    return withTestOutput {
                        putData(random<QxC<GenericInterface<String,Double>>>(randomConfig=TestRandomConfigWithRandomizableCandidateIndex(0)))
                        putData(random<QxC<GenericInterface<String,Double>>>(randomConfig=TestRandomConfigWithRandomizableCandidateIndex(1)))
                    }
                }
            """,
        ) {
            testCompilation = { result, _ ->
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                shouldThrow<UnableToMakeRandomException> {
                    result.executeRunTestFunction()
                }
            }
        }
    }


}
