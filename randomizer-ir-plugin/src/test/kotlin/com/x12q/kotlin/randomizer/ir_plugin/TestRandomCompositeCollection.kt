package com.x12q.kotlin.randomizer.ir_plugin

import com.tschuchort.compiletesting.KotlinCompilation
import com.x12q.kotlin.randomizer.ir_plugin.mock_objects.TestRandomConfig
import com.x12q.kotlin.randomizer.lib.RandomContext
import com.x12q.kotlin.randomizer.lib.RandomContextBuilderImp
import com.x12q.kotlin.randomizer.lib.randomizer.factoryRandomizer
import com.x12q.kotlin.randomizer.test_utils.assertions.executeRunTestFunction
import com.x12q.kotlin.randomizer.test_utils.test_code.TestImportsBuilder
import io.kotest.matchers.shouldBe
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import kotlin.test.BeforeTest
import kotlin.test.Test


@OptIn(ExperimentalCompilerApi::class)
class TestRandomCompositeCollection {

    data class Qx<T1>(val i: T1?)
    data class Qx2<Q2T>(val paramOfQ2: Q2T)
    data class Qx4<Q4T>(val paramOfQ4: Q4T)
    data class Qx6<H>(val paramOfQ6: H)
    data class Qx3<T1, T2, T3>(val i1: T1, val i2: T2, val i3: T3)
    data class TwoGeneric<G1, G2>(val g1: G1, val g2: G2)
    data class ThreeGeneric<G1, G2, G3>(val g1: G1, val g2: G2, val g3: G3)
    data class QxList<TL>(val listT: List<TL>)

    private val imports = TestImportsBuilder.stdImport
        .import(Qx::class)
        .import(Qx2::class)
        .import(Qx3::class)
        .import(Qx4::class)
        .import(Qx6::class)
        .import(TwoGeneric::class)
        .import(ThreeGeneric::class)
        .import(QxList::class)

    private val rdConfig = TestRandomConfig()
    lateinit var rdContext: RandomContext

    val cSize get() = rdContext.randomCollectionSize()
    val int get() = rdContext.nextInt()
    val boolean get() = rdContext.nextBoolean()
    val float get() = rdContext.nextFloat()
    val str get() = rdContext.nextString()
    val double get() = rdContext.nextDouble()
    val short get() = rdContext.nextShort()


    @BeforeTest
    fun bt() {
        rdContext = RandomContextBuilderImp()
            .setRandomConfigAndGenerateStandardRandomizers(rdConfig)
            .add(factoryRandomizer {
                Qx2(rdConfig.nextFloat())
            })
            .add(factoryRandomizer {
                Qx2(rdConfig.nextDouble())
            })
            .add(factoryRandomizer {
                Qx4(rdConfig.nextDouble())
            })
            .add(factoryRandomizer {
                Qx4(Qx4(rdConfig.nextShort()))
            })
            .add(factoryRandomizer {
                Qx2(Qx4(rdConfig.nextString()))
            })
            .add(factoryRandomizer {
                Qx2(Qx4(rdConfig.nextShort()))
            })
            .add(factoryRandomizer {
                TwoGeneric(rdConfig.nextInt(), rdConfig.nextString())
            })
            .add(factoryRandomizer {
                TwoGeneric(rdConfig.nextDouble(), rdConfig.nextShort())
            })
            .add(factoryRandomizer {
                TwoGeneric(Qx2(rdConfig.nextInt()), rdConfig.nextString())
            })
            .add(factoryRandomizer {
                ThreeGeneric(rdConfig.nextInt(), Qx2(rdConfig.nextString()), rdConfig.nextDouble())
            })
            .build()
    }

    @Test
    fun `set in type param - 3 nested`() {
        testGeneratedCodeUsingStandardPlugin(
            """
                $imports

                data class QxC<T1:Any>(override val data:T1):WithData

                fun runTest():TestOutput {
                    return withTestOutput {
                        putData(random<QxC<Set<Set<List<Double>>>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<List<Set<Set<Qx2<Float>>>>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<Set<List<Set<Qx2<Qx4<String>>>>>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<Set<Set<Map<TwoGeneric<Int, String>,Qx2<Boolean>>>>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<List<List<Map<TwoGeneric<Int, String>,Qx2<Boolean>>>>>>(randomConfig=TestRandomConfig()))
                    }
                }
            """,
        ) {
            testCompilation = { result, _ ->
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                val objectList = result.executeRunTestFunction().getObjs()

                objectList shouldBe listOf<Any>(
                    makeList(
                        cSize,
                        { rdConfig.resetRandomState() }) { List(cSize) { List(cSize) { double } }.toSet() }.toSet(),
                    makeList(
                        cSize,
                        { rdConfig.resetRandomState() }) { List(cSize) { List(cSize) { Qx2(float) }.toSet() }.toSet() },
                    makeList(
                        cSize,
                        { rdConfig.resetRandomState() }) { List(cSize) { List(cSize) { Qx2(Qx4(str)) }.toSet() } }.toSet(),
                    makeList(cSize, { rdConfig.resetRandomState() }) {
                        List(cSize) {
                            buildMap {
                                repeat(cSize) {
                                    put(TwoGeneric(int, str), Qx2(boolean))
                                }
                            }
                        }.toSet()
                    }.toSet(),

                    makeList(cSize, { rdConfig.resetRandomState() }) {
                        List(cSize) {
                            buildMap {
                                repeat(cSize) {
                                    put(TwoGeneric(int, str), Qx2(boolean))
                                }
                            }
                        }
                    } as List<List<Map<TwoGeneric<Int, String>, Qx2<Boolean>>>> //casting is actually needed here, otherwise compiler complains
                )
            }
        }
    }

    @Test
    fun `list in value param with 3 nested list`() {
        testGeneratedCodeUsingStandardPlugin(
            """
                $imports

                data class QxC<T1>(override val data:List<Map<Set<T1>,T1>>):WithData

                fun runTest():TestOutput {
                    return withTestOutput {
                        putData(random<QxC<Double>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<Qx2<Float>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<Qx2<Qx4<String>>>>(randomConfig=TestRandomConfig()))
                    }
                }
            """,
        ) {
            testCompilation = { result, _ ->
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                val objectList = result.executeRunTestFunction().getObjs()
                objectList shouldBe listOf<Any>(
                    makeList(cSize, { rdConfig.resetRandomState() }) {
                        buildMap {
                            repeat(cSize) {
                                put(List(cSize) { double }.toSet(), double)
                            }
                        }
                    },
                    makeList(cSize, { rdConfig.resetRandomState() }) {
                        buildMap {
                            repeat(cSize) {
                                put(List(cSize) { Qx2(float) }.toSet(), Qx2(float))
                            }
                        }
                    },
                    makeList(cSize, { rdConfig.resetRandomState() }) {
                        buildMap {
                            repeat(cSize) {
                                val key = List(cSize) { Qx2(Qx4(str)) }.toSet()
                                val v = Qx2(Qx4(str))
                                put(key,v)
                            }
                        }
                    }
                )
            }
        }
    }

    private fun <T> makeList(size: Int, sideEffect: () -> Unit, makeElement: () -> T): List<T> {
        sideEffect()
        return List(size) { makeElement() }
    }
}
