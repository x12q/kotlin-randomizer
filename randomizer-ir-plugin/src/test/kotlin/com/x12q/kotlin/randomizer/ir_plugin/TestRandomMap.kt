package com.x12q.kotlin.randomizer.ir_plugin

import com.tschuchort.compiletesting.KotlinCompilation
import com.x12q.kotlin.randomizer.ir_plugin.mock_objects.TestRandomConfig
import com.x12q.kotlin.randomizer.lib.RandomContext
import com.x12q.kotlin.randomizer.lib.RandomContextBuilderImp
import com.x12q.kotlin.randomizer.lib.random
import com.x12q.kotlin.randomizer.lib.randomizer.factoryRandomizer
import com.x12q.kotlin.randomizer.test_utils.assertions.executeRunTestFunction
import com.x12q.kotlin.randomizer.test_utils.test_code.TestImportsBuilder
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

                data class QxC<K,V>(override val data:Map<K,V>):WithData

                fun runTest():TestOutput {
                    return withTestOutput{
                        putData(random<QxC<Short,Double>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<Qx2<Float>,Double>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<Qx2<Qx4<String>>,Qx2<Qx4<Short>>>>(randomConfig=TestRandomConfig()))
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
                            put(nextShort(), nextDouble())
                        }
                    },

                    buildMap {
                        rdConfig.resetRandomState()
                        repeat(mapSize) {
                            put(Qx2(nextFloat()), nextDouble())
                        }
                    },

                    buildMap {
                        rdConfig.resetRandomState()

                        repeat(mapSize) {
                            val z = Qx2(Qx4(nextStr()))
                            val q = Qx2(Qx4(nextShort()))
                            put(z, q)
                        }
                    },
                )
            }
        }
    }

    @Test
    fun `Map in value param with custom randomizer}`() {

        testGeneratedCodeUsingStandardPlugin(
            """
                $imports

                data class QxC<K,V>(override val data:Map<K,V>):WithData

                fun runTest():TestOutput {
                    return withTestOutput{
                        putData(random<QxC<String,Double>>(
                            randomConfig=TestRandomConfig(), 
                            randomizers = {
                                constant{"a"}
                            }
                        ))
                    
                        putData(random<QxC<Short,Double>>(
                            randomConfig=TestRandomConfig(), 
                            randomizers = {
                                constant{buildMap {
                                    put(1.toShort(),3.0)
                                    put(1.toShort(),3.0)
                                }}
                            }
                        ))
                    }
                }
            """,
        ) {
            testCompilation = { result, _ ->
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                val objectList = result.executeRunTestFunction().getObjs()
                val m = buildMap {
                    repeat(mapSize){
                        put("a",nextDouble())
                    }
                }
                objectList shouldBe listOf(
                    m,
                    mapOf(1.toShort() to 3.0)
                )
            }
        }
    }


    @Test
    fun `Map in value param - 2 nest`() {

        testGeneratedCodeUsingStandardPlugin(
            """
                $imports

                data class QxC_Nest2<K,V,T>(override val data:Map<Map<K, V>, Map<K,T>>):WithData

                fun runTest():TestOutput {
                    return withTestOutput{
                        putData(random<QxC_Nest2<Short,Double,String>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC_Nest2<Qx2<Short>,Qx4<Double>,Qx6<String>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC_Nest2<Qx2<Qx4<Short>>,Qx4<Qx4<Double>>, Qx6<Qx4<String>>>>(randomConfig=TestRandomConfig()))
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

                data class QxC<K,V>(override val data:Map<K,Map<Map<K,V>,V>>):WithData

                data class QxC_Nest3<K,V,T,E>(override val data:Map<Map<Map<K,V>, T>, Map<Map<K,V>,E>>):WithData

                fun runTest():TestOutput {
                    return withTestOutput{
                        putData(random<QxC<Int,Double>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC_Nest3<Short,Double,String,Float>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC_Nest3<Qx2<Short>,Qx4<Double>,Qx6<String>,Qx<Float>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC_Nest3<Qx2<Qx4<Short>>,Qx4<Qx4<Double>>,Qx6<Qx4<String>>,Qx<Qx4<Float>>>>(randomConfig=TestRandomConfig()))
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
                            put(nextInt(), buildMap {
                                repeat(mapSize) {
                                    put(buildMap {
                                        repeat(mapSize) {
                                            put(nextInt(), nextDouble())
                                        }
                                    }, nextDouble())
                                }
                            })
                        }
                    },

                    buildMap {
                        rdConfig.resetRandomState()
                        repeat(mapSize) {
                            put(buildMap {
                                repeat(mapSize) {
                                    put(buildMap {
                                        repeat(mapSize) {
                                            put(nextShort(), nextDouble())
                                        }
                                    }, nextStr())
                                }
                            }, buildMap {
                                repeat(mapSize) {
                                    put(buildMap {
                                        repeat(mapSize) {
                                            put(nextShort(), nextDouble())
                                        }
                                    }, nextFloat())
                                }
                            })
                        }
                    },

                    buildMap {
                        rdConfig.resetRandomState()
                        repeat(mapSize) {
                            put(buildMap {
                                repeat(mapSize) {
                                    put(buildMap {
                                        repeat(mapSize) {
                                            put(Qx2(nextShort()), Qx4(nextDouble()))
                                        }
                                    }, Qx6(nextStr()))
                                }
                            }, buildMap {
                                repeat(mapSize) {
                                    put(buildMap {
                                        repeat(mapSize) {
                                            put(Qx2(nextShort()), Qx4(nextDouble()))
                                        }
                                    }, Qx(nextFloat()))
                                }
                            })
                        }
                    },

                    buildMap {
                        rdConfig.resetRandomState()
                        repeat(mapSize) {
                            put(buildMap {
                                repeat(mapSize) {
                                    put(buildMap {
                                        repeat(mapSize) {
                                            put(Qx2(Qx4(nextShort())), Qx4(Qx4(nextDouble())))
                                        }
                                    }, Qx6(Qx4(nextStr())))
                                }
                            }, buildMap {
                                repeat(mapSize) {
                                    put(buildMap {
                                        repeat(mapSize) {
                                            put(Qx2(Qx4(nextShort())), Qx4(Qx4(nextDouble())))
                                        }
                                    }, Qx(Qx4(nextFloat())))
                                }
                            })
                        }
                    }
                )
            }
        }
    }


    @Test
    fun `Map in type param`() {
        testGeneratedCodeUsingStandardPlugin(
            """
                $imports

                data class QxC<T:Any>(override val data:T):WithData

                fun runTest():TestOutput {
                    return withTestOutput{
                         putData(random<QxC<Map<Int,Double>>>(randomConfig=TestRandomConfig()))
                         putData(random<QxC<Map<Qx2<Float>,Double>>>(randomConfig=TestRandomConfig()))
                         putData(random<QxC<Map<Qx2<Float>,Qx4<Double>>>>(randomConfig=TestRandomConfig()))
                         putData(random<QxC<Map<Qx2<Qx4<String>>, Qx4<Qx4<Short>>>>>(randomConfig=TestRandomConfig()))
                         putData(random<QxC<Map<TwoGeneric<Int,String>,Int>>>(randomConfig=TestRandomConfig()))
                         putData(random<QxC<Map<TwoGeneric<Int,String>,TwoGeneric<Double,Short>>>>(randomConfig=TestRandomConfig()))
                         putData(random<QxC<Map<TwoGeneric<Qx2<Int>,String>,TwoGeneric<Qx2<Int>,String>>>>(randomConfig=TestRandomConfig()))
                         putData(random<QxC<Map<ThreeGeneric<Int,Qx2<String>,Double>,ThreeGeneric<Int,Qx2<String>,Double>>>>(randomConfig=TestRandomConfig()))
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
                                rdContext.random<TwoGeneric<Int, String>>(),
                                rdContext.nextInt(),
                            )
                        }
                    },

                    buildMap {
                        rdConfig.resetRandomState()
                        repeat(mapSize) {
                            put(
                                rdContext.random<TwoGeneric<Int, String>>(),
                                rdContext.random<TwoGeneric<Double, Short>>(),
                            )
                        }
                    },

                    buildMap {
                        rdConfig.resetRandomState()
                        repeat(mapSize) {
                            put(
                                rdContext.random<TwoGeneric<Qx2<Int>, String>>(),
                                rdContext.random<TwoGeneric<Qx2<Int>, String>>(),
                            )
                        }
                    },

                    buildMap {
                        rdConfig.resetRandomState()
                        repeat(mapSize) {
                            put(
                                rdContext.random<ThreeGeneric<Int, Qx2<String>, Double>>(),
                                rdContext.random<ThreeGeneric<Int, Qx2<String>, Double>>(),
                            )
                        }
                    },
                )
            }
        }
    }

    @Test
    fun `Map in type param with custom randomizer`() {
        testGeneratedCodeUsingStandardPlugin(
            """
                $imports

                data class QxC<T:Any>(override val data:T):WithData

                fun runTest():TestOutput {
                    return withTestOutput{
                         putData(random<QxC<Map<Int,Double>>>(
                            randomConfig=TestRandomConfig(), 
                            randomizers = {
                                constant(1)
                                constant(2.0)
                            }
                         ))
                         putData(random<QxC<Map<Int,Double>>>(
                            randomConfig=TestRandomConfig(), 
                            randomizers = {
                               constant(mapOf(1 to 3.0, 3 to 4.0))
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
                    mapOf(1 to 2.0),
                    mapOf(1 to 3.0, 3 to 4.0)
                )
            }
        }
    }


    @Test
    fun `Map in type param - 2 nest`() {
        testGeneratedCodeUsingStandardPlugin(
            """
                $imports

                data class QxC<T:Any>(override val data:T):WithData

                fun runTest():TestOutput {
                    return withTestOutput{
                         putData(random<QxC<Map<Map<Int,Double>,Map<Int,Double>>>>(randomConfig=TestRandomConfig()))
                         putData(random<QxC<Map<Map<Qx2<Int>,Qx4<Double>>,Map<Qx4<Int>,Qx6<Double>>>>>(randomConfig=TestRandomConfig()))
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
                            put(buildMap {
                                repeat(mapSize) {
                                    put(nextInt(), nextDouble())
                                }
                            }, buildMap {
                                repeat(mapSize) {
                                    put(nextInt(),nextDouble())
                                }
                            })
                        }
                    },
                    buildMap {
                        rdConfig.resetRandomState()
                        repeat(mapSize) {
                            put(buildMap {
                                repeat(mapSize) {
                                    put(Qx2(nextInt()), Qx4(nextDouble()))
                                }
                            }, buildMap {
                                repeat(mapSize) {
                                    put(Qx4(nextInt()),Qx6(nextDouble()))
                                }
                            })
                        }
                    },
                )
            }
        }
    }

    @Test
    fun `Map in type param - 3 nest`() {
        testGeneratedCodeUsingStandardPlugin(
            """
                $imports

                data class QxC<T:Any>(override val data:T):WithData

                fun runTest():TestOutput {
                    return withTestOutput{
                         putData(random<QxC<Map<Map<Map<Short,Double>, String>, Map<Map<Short,Double>,Float>>>>(randomConfig=TestRandomConfig()))

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
                            put(buildMap {
                                repeat(mapSize) {
                                    put(buildMap {
                                        repeat(mapSize) {
                                            put(nextShort(), nextDouble())
                                        }
                                    }, nextStr())
                                }
                            }, buildMap {
                                repeat(mapSize) {
                                    put(buildMap {
                                        repeat(mapSize) {
                                            put(nextShort(), nextDouble())
                                        }
                                    }, nextFloat())
                                }
                            })
                        }
                    },
                )
            }
        }
    }

}
