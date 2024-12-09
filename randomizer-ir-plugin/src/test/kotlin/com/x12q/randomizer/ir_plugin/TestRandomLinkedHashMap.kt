package com.x12q.randomizer.ir_plugin

import com.tschuchort.compiletesting.KotlinCompilation
import com.x12q.randomizer.ir_plugin.mock_objects.TestRandomConfig
import com.x12q.randomizer.lib.RandomContext
import com.x12q.randomizer.lib.RandomContextBuilderImp
import com.x12q.randomizer.lib.random
import com.x12q.randomizer.lib.randomizer.factoryRandomizer
import com.x12q.randomizer.test.util.assertions.executeRunTestFunction
import com.x12q.randomizer.test.util.test_code.TestImportsBuilder
import io.kotest.matchers.shouldBe
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import kotlin.test.BeforeTest
import kotlin.test.Test


@OptIn(ExperimentalCompilerApi::class)
class TestRandomLinkedLinkedHashMap {

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
    fun `Map in value param`() {

        testGeneratedCodeUsingStandardPlugin(
            """
                $imports

                @Randomizable(randomConfig = TestRandomConfig::class)
                data class QxC<K,V>(override val data:LinkedHashMap<K,V>):WithData

                fun runTest():TestOutput {
                    return withTestOutput{
                        putData(QxC.random<Short,Double>())
                        putData(QxC.random<Qx2<Float>,Double>())
                        putData(QxC.random<Qx2<Qx4<String>>,Qx2<Qx4<Short>>>())
                    }
                }
            """,
        ) {
            testCompilation = { result, _ ->
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                val objectList = result.executeRunTestFunction().getObjs()

                objectList shouldBe listOf(

                    LinkedHashMap(
                        buildMap {
                            rdConfig.resetRandomState()
                            repeat(mapSize) {
                                put(nextShort(), nextDouble())
                            }
                        }
                    ),
                    LinkedHashMap(buildMap {
                        rdConfig.resetRandomState()
                        repeat(mapSize) {
                            put(Qx2(nextFloat()), nextDouble())
                        }
                    }),

                    LinkedHashMap(
                        buildMap {
                            rdConfig.resetRandomState()

                            repeat(mapSize) {
                                val z = Qx2(Qx4(nextStr()))
                                val q = Qx2(Qx4(nextShort()))
                                put(z, q)
                            }
                        },
                    ),
                )
            }
        }
    }

    @Test
    fun `Map in value param with custom randomizer}`() {

        testGeneratedCodeUsingStandardPlugin(
            """
                $imports

                @Randomizable(randomConfig = TestRandomConfig::class)
                data class QxC<K,V>(override val data:LinkedHashMap<K,V>):WithData

                fun runTest():TestOutput {
                    return withTestOutput{
                        putData(QxC.random<String,Double>(
                            randomizers = {
                                constant{"a"}
                            }
                        ))
                    
                        putData(QxC.random<Short,Double>(
                            randomizers = {
                                constant<LinkedHashMap<Short,Double>>{LinkedHashMap(buildMap {
                                    put(1.toShort(),3.0)
                                    put(1.toShort(),3.0)
                                })}
                            }
                        ))
                    }
                }
            """,
        ) {
            testCompilation = { result, _ ->
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                val objectList = result.executeRunTestFunction().getObjs()
                val m = LinkedHashMap(buildMap {
                    repeat(mapSize) {
                        put("a", nextDouble())
                    }
                })
                objectList shouldBe listOf(
                    m,
                    LinkedHashMap(mapOf(1.toShort() to 3.0))
                )
            }
        }
    }


    @Test
    fun `Map in value param - 2 nest`() {

        testGeneratedCodeUsingStandardPlugin(
            """
                $imports

                @Randomizable(randomConfig = TestRandomConfig::class)
                data class QxC_Nest2<K,V,T>(override val data:LinkedHashMap<LinkedHashMap<K, V>, LinkedHashMap<K,T>>):WithData


                fun runTest():TestOutput {
                    return withTestOutput{
                        putData(QxC_Nest2.random<Short,Double,String>())
                        putData(QxC_Nest2.random<Qx2<Short>,Qx4<Double>,Qx6<String>>())
                        putData(QxC_Nest2.random<Qx2<Qx4<Short>>,Qx4<Qx4<Double>>, Qx6<Qx4<String>>>())
                    }
                }
            """,
        ) {
            testCompilation = { result, _ ->
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                val objectList = result.executeRunTestFunction().getObjs()

                objectList shouldBe listOf(
                    buildMap {
                        rdConfig.resetRandomState()
                        repeat(mapSize) {
                            put(
                                buildMap {
                                    repeat(mapSize) {
                                        put(nextShort(), nextDouble())
                                    }
                                },
                                buildMap {
                                    repeat(mapSize) {
                                        put(nextShort(), nextStr())
                                    }
                                }
                            )
                        }
                    },
                    buildMap {
                        rdConfig.resetRandomState()
                        repeat(mapSize) {
                            put(
                                buildMap {
                                    repeat(mapSize) {
                                        put(Qx2(nextShort()), Qx4(nextDouble()))
                                    }
                                },
                                buildMap {
                                    repeat(mapSize) {
                                        put(Qx2(nextShort()), Qx6(nextStr()))
                                    }
                                }
                            )
                        }
                    },

                    buildMap {
                        rdConfig.resetRandomState()
                        repeat(mapSize) {
                            put(
                                buildMap {
                                    repeat(mapSize) {
                                        put(Qx2(Qx4(nextShort())), Qx4(Qx4(nextDouble())))
                                    }
                                },
                                buildMap {
                                    repeat(mapSize) {
                                        put(Qx2(Qx4(nextShort())), Qx6(Qx4(nextStr())))
                                    }
                                }
                            )
                        }
                    },
                )
            }
        }
    }


    @Test
    fun `Map in value param - 3 nested`() {
        testGeneratedCodeUsingStandardPlugin(
            """
                $imports

                @Randomizable(randomConfig = TestRandomConfig::class)
                data class QxC<K,V>(override val data:LinkedHashMap<K,LinkedHashMap<LinkedHashMap<K,V>,V>>):WithData

                @Randomizable(randomConfig = TestRandomConfig::class)
                data class QxC_Nest3<K,V,T,E>(override val data:LinkedHashMap<LinkedHashMap<LinkedHashMap<K,V>, T>, LinkedHashMap<LinkedHashMap<K,V>,E>>):WithData
                fun runTest():TestOutput {
                    return withTestOutput{
                        putData(QxC.random<Int,Double>())
                        putData(QxC_Nest3.random<Short,Double,String,Float>())
                        putData(QxC_Nest3.random<Qx2<Short>,Qx4<Double>,Qx6<String>,Qx<Float>>())
                        putData(QxC_Nest3.random<Qx2<Qx4<Short>>,Qx4<Qx4<Double>>,Qx6<Qx4<String>>,Qx<Qx4<Float>>>())
                    }
                }
            """,
        ) {
            testCompilation = { result, _ ->
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                val objectList = result.executeRunTestFunction().getObjs()


                objectList shouldBe listOf(
                    LinkedHashMap(buildMap {
                        rdConfig.resetRandomState()
                        repeat(mapSize) {
                            put(nextInt(), LinkedHashMap(buildMap {
                                repeat(mapSize) {
                                    put(LinkedHashMap(buildMap {
                                        repeat(mapSize) {
                                            put(nextInt(), nextDouble())
                                        }
                                    }), nextDouble())
                                }
                            }))
                        }
                    }),

                    LinkedHashMap(buildMap {
                        rdConfig.resetRandomState()
                        repeat(mapSize) {
                            put(LinkedHashMap(buildMap {
                                repeat(mapSize) {
                                    put(LinkedHashMap(buildMap {
                                        repeat(mapSize) {
                                            put(nextShort(), nextDouble())
                                        }
                                    }), nextStr())
                                }
                            }), LinkedHashMap(buildMap {
                                repeat(mapSize) {
                                    put(LinkedHashMap(buildMap {
                                        repeat(mapSize) {
                                            put(nextShort(), nextDouble())
                                        }
                                    }), nextFloat())
                                }
                            }))
                        }
                    }),

                    LinkedHashMap(buildMap {
                        rdConfig.resetRandomState()
                        repeat(mapSize) {
                            put(LinkedHashMap(buildMap {
                                repeat(mapSize) {
                                    put(LinkedHashMap(buildMap {
                                        repeat(mapSize) {
                                            put(Qx2(nextShort()), Qx4(nextDouble()))
                                        }
                                    }), Qx6(nextStr()))
                                }
                            }), LinkedHashMap(buildMap {
                                repeat(mapSize) {
                                    put(LinkedHashMap(buildMap {
                                        repeat(mapSize) {
                                            put(Qx2(nextShort()), Qx4(nextDouble()))
                                        }
                                    }), Qx(nextFloat()))
                                }
                            }))
                        }
                    }),

                    LinkedHashMap(buildMap {
                        rdConfig.resetRandomState()
                        repeat(mapSize) {
                            put(LinkedHashMap(buildMap {
                                repeat(mapSize) {
                                    put(buildMap {
                                        repeat(mapSize) {
                                            put(Qx2(Qx4(nextShort())), Qx4(Qx4(nextDouble())))
                                        }
                                    }, Qx6(Qx4(nextStr())))
                                }
                            }), LinkedHashMap(buildMap {
                                repeat(mapSize) {
                                    put(buildMap {
                                        repeat(mapSize) {
                                            put(Qx2(Qx4(nextShort())), Qx4(Qx4(nextDouble())))
                                        }
                                    }, Qx(Qx4(nextFloat())))
                                }
                            }))
                        }
                    })
                )
            }
        }
    }


    @Test
    fun `Map in type param`() {
        testGeneratedCodeUsingStandardPlugin(
            """
                $imports

                @Randomizable(randomConfig = TestRandomConfig::class)
                data class QxC<T:Any>(override val data:T):WithData

                fun runTest():TestOutput {
                    return withTestOutput{
                         putData(QxC.random<LinkedHashMap<Int,Double>>())
                         putData(QxC.random<LinkedHashMap<Qx2<Float>,Double>>())
                         putData(QxC.random<LinkedHashMap<Qx2<Float>,Qx4<Double>>>())
                         putData(QxC.random<LinkedHashMap<Qx2<Qx4<String>>, Qx4<Qx4<Short>>>>())
                         putData(QxC.random<LinkedHashMap<TwoGeneric<Int,String>,Int>>())
                         putData(QxC.random<LinkedHashMap<TwoGeneric<Int,String>,TwoGeneric<Double,Short>>>())
                         putData(QxC.random<LinkedHashMap<TwoGeneric<Qx2<Int>,String>,TwoGeneric<Qx2<Int>,String>>>())
                         putData(QxC.random<LinkedHashMap<ThreeGeneric<Int,Qx2<String>,Double>,ThreeGeneric<Int,Qx2<String>,Double>>>())
                    }
                }
            """,
        ) {
            testCompilation = { result, _ ->
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                val objectList = result.executeRunTestFunction().getObjs()


                objectList shouldBe listOf(
                    LinkedHashMap(buildMap {
                        rdConfig.resetRandomState()
                        repeat(mapSize) {
                            put(nextInt(), nextDouble())
                        }
                    }),

                    LinkedHashMap(buildMap {
                        rdConfig.resetRandomState()
                        repeat(mapSize) {
                            put(rdContext.random<Qx2<Float>>(), nextDouble())
                        }
                    }),

                    LinkedHashMap(buildMap {
                        rdConfig.resetRandomState()
                        repeat(mapSize) {
                            put(rdContext.random<Qx2<Float>>(), rdContext.random<Qx4<Double>>())
                        }
                    }),

                    LinkedHashMap(buildMap {
                        rdConfig.resetRandomState()
                        repeat(mapSize) {
                            put(rdContext.random<Qx2<Qx4<String>>>(), rdContext.random<Qx4<Qx4<Short>>>())
                        }
                    }),

                    LinkedHashMap(buildMap {
                        rdConfig.resetRandomState()
                        repeat(mapSize) {
                            put(
                                rdContext.random<TwoGeneric<Int, String>>(),
                                rdContext.nextInt(),
                            )
                        }
                    }),

                    LinkedHashMap(buildMap {
                        rdConfig.resetRandomState()
                        repeat(mapSize) {
                            put(
                                rdContext.random<TwoGeneric<Int, String>>(),
                                rdContext.random<TwoGeneric<Double, Short>>(),
                            )
                        }
                    }),

                    LinkedHashMap(buildMap {
                        rdConfig.resetRandomState()
                        repeat(mapSize) {
                            put(
                                rdContext.random<TwoGeneric<Qx2<Int>, String>>(),
                                rdContext.random<TwoGeneric<Qx2<Int>, String>>(),
                            )
                        }
                    }),

                    LinkedHashMap(buildMap {
                        rdConfig.resetRandomState()
                        repeat(mapSize) {
                            put(
                                rdContext.random<ThreeGeneric<Int, Qx2<String>, Double>>(),
                                rdContext.random<ThreeGeneric<Int, Qx2<String>, Double>>(),
                            )
                        }
                    }),
                )
            }
        }
    }

    @Test
    fun `Map in type param with custom randomizer`() {
        testGeneratedCodeUsingStandardPlugin(
            """
                $imports

                @Randomizable(randomConfig = TestRandomConfig::class)
                data class QxC<T:Any>(override val data:T):WithData

                fun runTest():TestOutput {
                    return withTestOutput{
                         putData(QxC.random<LinkedHashMap<Int,Double>>(
                            randomizers = {
                                constant(1)
                                constant(2.0)
                            }
                         ))
                         putData(QxC.random<LinkedHashMap<Int,Double>>(
                            randomizers = {
                               constant<LinkedHashMap<Int,Double>>(LinkedHashMap(mapOf(1 to 3.0, 3 to 4.0)))
                            }
                         ))
                    }
                }
            """,
        ) {
            testCompilation = { result, _ ->
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                val objectList = result.executeRunTestFunction().getObjs()

                objectList shouldBe listOf(
                    LinkedHashMap(mapOf(1 to 2.0)),
                    LinkedHashMap(mapOf(1 to 3.0, 3 to 4.0))
                )
            }
        }
    }


    @Test
    fun `Map in type param - 2 nest`() {
        testGeneratedCodeUsingStandardPlugin(
            """
                $imports

                @Randomizable(randomConfig = TestRandomConfig::class)
                data class QxC<T:Any>(override val data:T):WithData

                fun runTest():TestOutput {
                    return withTestOutput{
                         putData(QxC.random<LinkedHashMap<LinkedHashMap<Int,Double>,LinkedHashMap<Int,Double>>>())
                         putData(QxC.random<LinkedHashMap<LinkedHashMap<Qx2<Int>,Qx4<Double>>,LinkedHashMap<Qx4<Int>,Qx6<Double>>>>())
                    }
                }
            """,
        ) {
            testCompilation = { result, _ ->
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                val objectList = result.executeRunTestFunction().getObjs()

                val m1:LinkedHashMap<LinkedHashMap<Int,Double>,LinkedHashMap<Int,Double>> = buildLinkedHashMap {
                    rdConfig.resetRandomState()
                    repeat(mapSize) {
                        put(buildLinkedHashMap {
                            repeat(mapSize) {
                                put(nextInt(), nextDouble())
                            }
                        }, buildLinkedHashMap {
                            repeat(mapSize) {
                                put(nextInt(), nextDouble())
                            }
                        })
                    }
                }
                val m2:LinkedHashMap<LinkedHashMap<Qx2<Int>,Qx4<Double>>,LinkedHashMap<Qx4<Int>,Qx6<Double>>> = buildLinkedHashMap {
                    rdConfig.resetRandomState()
                    repeat(mapSize) {
                        put(buildLinkedHashMap {
                            repeat(mapSize) {
                                put(Qx2(nextInt()), Qx4(nextDouble()))
                            }
                        }, buildLinkedHashMap {
                            repeat(mapSize) {
                                put(Qx4(nextInt()), Qx6(nextDouble()))
                            }
                        })
                    }
                }

                objectList shouldBe listOf(m1, m2)
            }
        }
    }

    @Test
    fun `Map in type param - 3 nest`() {
        testGeneratedCodeUsingStandardPlugin(
            """
                $imports

                @Randomizable(randomConfig = TestRandomConfig::class)
                data class QxC<T:Any>(override val data:T):WithData

                fun runTest():TestOutput {
                    return withTestOutput{
                         putData(QxC.random<LinkedHashMap<LinkedHashMap<LinkedHashMap<Short,Double>, String>, LinkedHashMap<LinkedHashMap<Short,Double>,Float>>>())

                    }
                }
            """,
        ) {
            testCompilation = { result, _ ->
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                val objectList = result.executeRunTestFunction().getObjs()


                val m: LinkedHashMap<LinkedHashMap<LinkedHashMap<Short, Double>, String>, LinkedHashMap<LinkedHashMap<Short, Double>, Float>> =
                    buildLinkedHashMap {
                        rdConfig.resetRandomState()
                        repeat(mapSize) {
                            put(buildLinkedHashMap {
                                repeat(mapSize) {
                                    put(buildLinkedHashMap {
                                        repeat(mapSize) {
                                            put(nextShort(), nextDouble())
                                        }
                                    }, nextStr())
                                }
                            }, buildLinkedHashMap {
                                repeat(mapSize) {
                                    put(buildLinkedHashMap {
                                        repeat(mapSize) {
                                            put(nextShort(), nextDouble())
                                        }
                                    }, nextFloat())
                                }
                            })
                        }
                    }
                objectList shouldBe listOf(
                    m
                )
            }
        }
    }

    private inline fun <K, V> buildLinkedHashMap(builderAction: MutableMap<K, V>.() -> Unit): LinkedHashMap<K, V> {
        return LinkedHashMap(buildMap(builderAction))
    }
}
