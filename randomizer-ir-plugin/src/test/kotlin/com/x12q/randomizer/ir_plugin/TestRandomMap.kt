package com.x12q.randomizer.ir_plugin

import com.tschuchort.compiletesting.KotlinCompilation
import com.x12q.randomizer.ir_plugin.mock_objects.TestRandomConfig
import com.x12q.randomizer.lib.RandomContext
import com.x12q.randomizer.lib.RandomContextBuilderImp
import com.x12q.randomizer.lib.random
import com.x12q.randomizer.lib.randomizer.factoryRandomizer
import com.x12q.randomizer.test.util.assertions.runRunTest
import com.x12q.randomizer.test.util.test_code.TestImportsBuilder
import io.kotest.matchers.shouldBe
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import kotlin.test.BeforeTest
import kotlin.test.Test


@OptIn(ExperimentalCompilerApi::class)
class TestRandomMap {

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
    fun nextSize(): Int = rdConfig.randomCollectionSize()
    val mapSize = nextSize()
    fun nextInt(): Int = rdContext.nextInt()
    fun nextFloat(): Float = rdContext.nextFloat()
    fun nextStr(): String = rdContext.nextString()
    fun nextDouble(): Double = rdContext.nextDouble()
    fun nextShort(): Short = rdContext.nextShort()

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
                TwoGeneric(rdConfig.nextInt(),rdConfig.nextString())
            })
            .add(factoryRandomizer {
                TwoGeneric(rdConfig.nextDouble(),rdConfig.nextShort())
            })
            .add(factoryRandomizer {
                TwoGeneric(Qx2(rdConfig.nextInt()),rdConfig.nextString())
            })
            .add(factoryRandomizer {
                ThreeGeneric(rdConfig.nextInt(),Qx2(rdConfig.nextString()), rdConfig.nextDouble())
            })
            .build()
    }

    @Test
    fun `map in value param`() {
        testGeneratedCodeUsingStandardPlugin(
            """
                $imports

                @Randomizable(randomConfig = TestRandomConfig::class)
                data class QxC<K,V>(override val data:Map<K,V>):WithData

                fun runTest():TestOutput {
                    return withTestOutput{
                        putData(QxC.random<Int,Double>())
                        // putData(QxC.random<Qx2<Float>,Double>())
                        // putData(QxC.random<Qx2<Float>,Qx2<Double>>())
                        // putData(QxC.random<Qx2<Qx4<String>>,Qx2<Qx4<Short>>>())
                    }
                }
            """,
        ) {
            testCompilation = { result, _ ->
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                val objectList = result.runRunTest().getObjs()


                objectList shouldBe listOf(
                   buildMap {
                       rdConfig.resetRandomState()
                       repeat(mapSize) {
                           put(nextInt(), nextDouble())
                       }
                   },

                   // buildMap {
                   //     rdConfig.resetRandomState()
                   //     repeat(mapSize) {
                   //         put(rdContext.random<Qx2<Float>>(), nextDouble())
                   //     }
                   // },
                   //
                   //  buildMap {
                   //      rdConfig.resetRandomState()
                   //      repeat(mapSize) {
                   //          put(rdContext.random<Qx2<Float>>(), rdContext.random<Qx2<Double>>())
                   //      }
                   //  },
                   //
                   //  buildMap {
                   //      rdConfig.resetRandomState()
                   //      repeat(mapSize) {
                   //          put(rdContext.random<Qx2<Qx4<String>>>(), rdContext.random<Qx2<Qx4<Short>>>())
                   //      }
                   //  },
                )
            }
        }
    }

    @Test
    fun `map in value param - 3 nested`() {
        testGeneratedCodeUsingStandardPlugin(
            """
                $imports

                @Randomizable(randomConfig = TestRandomConfig::class)
                data class QxC<K,V>(override val data:Map<K,Map<Map<K,V>,V>>):WithData

                fun runTest():TestOutput {
                    return withTestOutput{
                        putData(QxC.random<Int,Double>())
                        // putData(QxC.random<Qx2<Float>,Double>())
                        // putData(QxC.random<Qx2<Float>,Qx2<Double>>())
                        // putData(QxC.random<Qx2<Qx4<String>>,Qx2<Qx4<Short>>>())
                    }
                }
            """,
        ) {
            testCompilation = { result, _ ->
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                val objectList = result.runRunTest().getObjs()


                objectList shouldBe listOf(
                    buildMap {
                        rdConfig.resetRandomState()
                        repeat(mapSize) {
                            put(nextInt(), nextDouble())
                        }
                    },

                    // buildMap {
                    //     rdConfig.resetRandomState()
                    //     repeat(mapSize) {
                    //         put(rdContext.random<Qx2<Float>>(), nextDouble())
                    //     }
                    // },
                    //
                    // buildMap {
                    //     rdConfig.resetRandomState()
                    //     repeat(mapSize) {
                    //         put(rdContext.random<Qx2<Float>>(), rdContext.random<Qx2<Double>>())
                    //     }
                    // },
                    //
                    // buildMap {
                    //     rdConfig.resetRandomState()
                    //     repeat(mapSize) {
                    //         put(rdContext.random<Qx2<Qx4<String>>>(), rdContext.random<Qx2<Qx4<Short>>>())
                    //     }
                    // },
                )
            }
        }
    }


    @Test
    fun `map in type param`() {
        testGeneratedCodeUsingStandardPlugin(
            """
                $imports

                @Randomizable(randomConfig = TestRandomConfig::class)
                data class QxC<T:Any>(override val data:T):WithData

                fun runTest():TestOutput {
                    return withTestOutput{
                         putData(QxC.random<Map<Int,Double>>())
                         putData(QxC.random<Map<Qx2<Float>,Double>>())
                         putData(QxC.random<Map<Qx2<Float>,Qx4<Double>>>())
                         putData(QxC.random<Map<Qx2<Qx4<String>>, Qx4<Qx4<Short>>>>())
                         putData(QxC.random<Map<TwoGeneric<Int,String>,Int>>())
                         putData(QxC.random<Map<TwoGeneric<Int,String>,TwoGeneric<Double,Short>>>())
                         putData(QxC.random<Map<TwoGeneric<Qx2<Int>,String>,TwoGeneric<Qx2<Int>,String>>>())
                         putData(QxC.random<Map<ThreeGeneric<Int,Qx2<String>,Double>,ThreeGeneric<Int,Qx2<String>,Double>>>())
                    }
                }
            """,
        ) {
            testCompilation = { result, _ ->
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                val objectList = result.runRunTest().getObjs()


                objectList shouldBe listOf(
                     buildMap {
                         rdConfig.resetRandomState()
                         repeat(mapSize) {
                             put(nextInt(), nextDouble())
                         }
                     },

                    buildMap {
                        rdConfig.resetRandomState()
                        repeat(mapSize) {
                            put(rdContext.random<Qx2<Float>>(), nextDouble())
                        }
                    },

                    buildMap {
                        rdConfig.resetRandomState()
                        repeat(mapSize) {
                            put(rdContext.random<Qx2<Float>>(), rdContext.random<Qx4<Double>>())
                        }
                    },

                    buildMap {
                        rdConfig.resetRandomState()
                        repeat(mapSize) {
                            put(rdContext.random<Qx2<Qx4<String>>>(), rdContext.random<Qx4<Qx4<Short>>>())
                        }
                    },

                    buildMap {
                        rdConfig.resetRandomState()
                        repeat(mapSize) {
                            put(
                                rdContext.random<TwoGeneric<Int,String>>(),
                                rdContext.nextInt(),
                            )
                        }
                    },

                    buildMap {
                        rdConfig.resetRandomState()
                        repeat(mapSize) {
                            put(
                                rdContext.random<TwoGeneric<Int,String>>(),
                                rdContext.random<TwoGeneric<Double,Short>>(),
                            )
                        }
                    },

                    buildMap {
                        rdConfig.resetRandomState()
                        repeat(mapSize) {
                            put(
                                rdContext.random<TwoGeneric<Qx2<Int>,String>>(),
                                rdContext.random<TwoGeneric<Qx2<Int>,String>>(),
                            )
                        }
                    },

                    buildMap {
                        rdConfig.resetRandomState()
                        repeat(mapSize) {
                            put(
                                rdContext.random<ThreeGeneric<Int,Qx2<String>,Double>>(),
                                rdContext.random<ThreeGeneric<Int,Qx2<String>,Double>>(),
                            )
                        }
                    },
                )
            }
        }
    }
}