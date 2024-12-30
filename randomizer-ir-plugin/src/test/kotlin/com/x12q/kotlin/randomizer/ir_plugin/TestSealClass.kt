package com.x12q.kotlin.randomizer.ir_plugin

import com.tschuchort.compiletesting.KotlinCompilation
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
class TestSealClass {

    sealed class A{
        data class A1(val i: Int, val d: Double, val str: String): A() {
            constructor(i: Int, d: Double) : this(i, d, "str0")
            @Randomizable
            constructor(i: Int) : this(i, -1.0, "str1")
            constructor() : this(0, -2.0, "str2")
        }
        object A2:A()
        data class A3(val d:List<Int>) : A()
        class A4<T>(val t:T): A()
    }

    data class QxC<K_Q : Any>(override val data: K_Q) : WithData

    private val imports = TestImportsBuilder.stdImport
        .import(A::class)
        .import(A.A1::class)
        .import(A.A2::class)
        .import(A.A3::class)
        .import(A.A4::class)
        .import(B::class)
        .import(B.B1::class)
        .import(B.B2::class)
        .import(B.B3::class)
        .import(B.B4::class)
        .import(QxC::class)


    /**
     * Test passing generic param from "random" function to generic with a property.
     * The generic type is further passed down to deeper layer within the class of that property.
     */
    @Test
    fun `test randomizing sealed class with custom randomizer`() {
        testGeneratedCodeUsingStandardPlugin(
            """
                $imports

                fun runTest():TestOutput {
                    return withTestOutput {
                        putData(random<QxC<A>>(randomConfig=TestRandomConfig(), randomizers = {
                            factory<A>{A.A2}
                        }))

                        putData(random<QxC<A>>(randomConfig=TestRandomConfig(), randomizers = {
                            factory<A>{random<A.A1>()}
                        }))
                        putData(random<QxC<A>>(randomConfig=TestRandomConfig(), randomizers = {
                            factory<A>{random<A.A1>(randomizers={
                                factory<A.A1>{A.A1()}
                            })}
                        }))
                    }
                }
            """,
        ) {
            testCompilation = { result, _ ->
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK

                val objectList = result.executeRunTestFunction().getObjs()
                val cf = TestRandomConfigWithRandomizableCandidateIndex(0)
                val cf2 = TestRandomConfigWithRandomizableCandidateIndex(1)
                objectList shouldBe listOf(
                    A.A2,
                    A.A1(cf.nextInt()),
                    A.A1()
                )
            }
        }
    }

    @Test
    fun `test randomizing sealed class without custom randomizer`() {
        testGeneratedCodeUsingStandardPlugin(
            """
                $imports

                fun runTest():TestOutput {
                    return withTestOutput {
                        putData(random<QxC<A>>(randomConfig=TestRandomConfig()))
                    }
                }
            """,
        ) {
            testCompilation = { result, _ ->
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                shouldThrow<UnableToMakeRandomException> {
                    result.executeRunTestFunction().getObjs()
                }
            }
        }
    }

    sealed interface B{
        data class B1(val i: Int, val d: Double, val str: String): B {
            constructor(i: Int, d: Double) : this(i, d, "str0")
            @Randomizable
            constructor(i: Int) : this(i, -1.0, "str1")
            constructor() : this(0, -2.0, "str2")
        }
        object B2:B
        data class B3(val d:List<Int>) : B
        class B4<T>(val t:T): A()
    }
    @Test
    fun `test randomizing sealed interface without custom randomizer`() {
        testGeneratedCodeUsingStandardPlugin(
            """
                $imports

                fun runTest():TestOutput {
                    return withTestOutput {
                        putData(random<QxC<B>>(randomConfig=TestRandomConfig(), randomizers = {
                            factory<B>{B.B2}
                        }))

                        putData(random<QxC<B>>(randomConfig=TestRandomConfig(), randomizers = {
                            factory<B>{random<B.B1>()}
                        }))

                        putData(random<QxC<B>>(randomConfig=TestRandomConfig(), randomizers = {
                            factory<B>{random<B.B1>(randomizers={
                                factory{B.B1()}
                            })}
                        }))
                    }
                }
            """,
        ) {
            testCompilation = { result, _ ->
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK

                val objectList = result.executeRunTestFunction().getObjs()
                val cf = TestRandomConfigWithRandomizableCandidateIndex(0)
                val cf2 = TestRandomConfigWithRandomizableCandidateIndex(1)
                objectList shouldBe listOf(
                    B.B2,
                    B.B1(cf.nextInt()),
                    B.B1()
                )
            }
        }
    }


    @Test
    fun `test randomizing sealed interface with custom randomizer`() {
        testGeneratedCodeUsingStandardPlugin(
            """
                $imports

                fun runTest():TestOutput {
                    return withTestOutput {
                        putData(random<QxC<B>>(randomConfig=TestRandomConfig()))
                    }
                }
            """,
        ) {
            testCompilation = { result, _ ->
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                shouldThrow<UnableToMakeRandomException> {
                    result.executeRunTestFunction().getObjs()
                }
            }
        }
    }


}
