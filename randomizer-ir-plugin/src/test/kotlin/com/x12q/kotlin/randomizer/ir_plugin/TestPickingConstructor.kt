package com.x12q.kotlin.randomizer.ir_plugin

import com.tschuchort.compiletesting.KotlinCompilation
import com.x12q.kotlin.randomizer.ir_plugin.mock_objects.TestRandomConfigWithRandomizableCandidateIndex
import com.x12q.kotlin.randomizer.lib.UnableToMakeRandomException
import com.x12q.kotlin.randomizer.lib.annotations.Randomizable
import com.x12q.kotlin.randomizer.test_utils.WithData
import com.x12q.kotlin.randomizer.test_utils.assertions.executeRunTestFunction
import com.x12q.kotlin.randomizer.test_utils.test_code.TestImportsBuilder
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import kotlin.test.Test


@OptIn(ExperimentalCompilerApi::class)
class TestPickingConstructor {

    data class A1(val i: Int, val d: Double, val str: String) {
        @Randomizable
        constructor(i: Int, d: Double) : this(i, d, "str0")
        @Randomizable
        constructor(i: Int) : this(i, -1.0, "str1")
        constructor() : this(0, -2.0, "str2")
    }

    data class QxC<K_Q : Any>(override val data: K_Q) : WithData

    private val imports = TestImportsBuilder.stdImport
        .import(A1::class)
        .import(A2::class)
        .import(A3::class)
        .import(A4::class)
        .import(A5::class)
        .import(QxC::class)


    /**
     * Test passing generic param from "random" function to generic with a property.
     * The generic type is further passed down to deeper layer within the class of that property.
     */
    @Test
    fun `test using the only annotated constructor`() {
        testGeneratedCodeUsingStandardPlugin(
            """
                $imports

                fun runTest():TestOutput {
                    return withTestOutput {
                        putData(random<QxC<A1>>(randomConfig=TestRandomConfigWithRandomizableCandidateIndex(0), randomizers = {
                            factory{
                                QxC(random<A1>())
                            }
                        }))
                        putData(random<QxC<A1>>(randomConfig=TestRandomConfigWithRandomizableCandidateIndex(1), randomizers = {
                            factory{
                                QxC(random<A1>())
                            }
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
                    A1(i = cf.nextInt(),d=cf.nextDouble()),
                    A1(i = cf2.nextInt())
                )
            }
        }
    }


    data class A2 @Randomizable constructor(val i: Int, val d: Double, val str: String) {
        constructor(i: Int, d: Double) : this(i, d, "str0")
        constructor(i: Int) : this(i, -1.0, "str1")
        constructor() : this(0, -2.0, "str2")
    }

    @Randomizable
    data class A3(val i: Int, val d: Double, val str: String) {
        constructor(i: Int, d: Double) : this(i, d, "str0")
        constructor(i: Int) : this(i, -1.0, "str1")
        constructor() : this(0, -2.0, "str2")
    }

    @Test
    fun `test using annotated primary constructor`() {
        testGeneratedCodeUsingStandardPlugin(
            """
                $imports

                fun runTest():TestOutput {
                    return withTestOutput {
                        putData(random<QxC<A2>>(randomConfig=TestRandomConfigWithRandomizableCandidateIndex(0), randomizers = {
                            factory{
                                QxC(random<A2>())
                            }
                        }))
                        putData(random<QxC<A3>>(randomConfig=TestRandomConfigWithRandomizableCandidateIndex(0), randomizers = {
                            factory{
                                QxC(random<A3>())
                            }
                        }))
                    }
                }
            """,
        ) {
            testCompilation = { result, _ ->
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                val objectList = result.executeRunTestFunction().getObjs()
                val cf = TestRandomConfigWithRandomizableCandidateIndex(0)
                val cf2 = TestRandomConfigWithRandomizableCandidateIndex(0)
                objectList shouldBe listOf(
                    A2(i = cf.nextInt(),d=cf.nextDouble(), str =cf.nextString()),
                    A3(i = cf2.nextInt(),d=cf2.nextDouble(), str =cf2.nextString()),
                )
            }
        }
    }


    data class A4(val i: Int, val d: Double, val str: String) {
        constructor(i: Int, d: Double) : this(i, d, "str0")
        constructor(i: Int) : this(i, -1.0, "str1")
        constructor() : this(0, -2.0, "str2")
    }

    @Test
    fun `test using non-annotated constructor`() {
        testGeneratedCodeUsingStandardPlugin(
            """
                $imports

                fun runTest():TestOutput {
                    return withTestOutput {
                        putData(random<QxC<A4>>(randomConfig=TestRandomConfigWithRandomizableCandidateIndex(3), randomizers = {
                            factory{
                                QxC(random<A4>())
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
                    A4(),
                )
            }
        }
    }


    @Randomizable
    data class A5(val i: Int, val d: Double, val str: String) {
        constructor(i: Int, d: Double) : this(i, d, "str0")
        constructor(i: Int) : this(i, -1.0, "str1")
        constructor() : this(0, -2.0, "str2")
    }

    @Test
    fun `test using using wrong index`() {
        testGeneratedCodeUsingStandardPlugin(
            """
                $imports

                fun runTest():TestOutput {
                    return withTestOutput {
                        putData(random<QxC<A5>>(randomConfig=TestRandomConfigWithRandomizableCandidateIndex(2), randomizers = {
                            factory<QxC<A5>>{
                                QxC(random<A5>())
                            }
                        }))
                    }
                }
            """,
        ) {
            testCompilation = { result, _ ->
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                shouldThrow<UnableToMakeRandomException> {
                    result.executeRunTestFunction().getObjs()
                }.message shouldContain "A5"
            }
        }
    }

}
