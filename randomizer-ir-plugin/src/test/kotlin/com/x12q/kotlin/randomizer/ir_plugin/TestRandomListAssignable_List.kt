package com.x12q.kotlin.randomizer.ir_plugin

import com.tschuchort.compiletesting.KotlinCompilation
import com.x12q.kotlin.randomizer.ir_plugin.mock_objects.TestRandomConfig
import com.x12q.kotlin.randomizer.lib.RandomContext
import com.x12q.kotlin.randomizer.lib.RandomContextBuilderImp
import com.x12q.kotlin.randomizer.lib.randomizer.factoryRandomizer
import com.x12q.kotlin.randomizer.test.util.assertions.executeRunTestFunction
import com.x12q.kotlin.randomizer.test.util.test_code.TestImportsBuilder
import io.kotest.matchers.shouldBe
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import kotlin.test.BeforeTest
import kotlin.test.Test


@OptIn(ExperimentalCompilerApi::class)
class TestRandomListAssignable_List {

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

    val size get() = rdContext.randomCollectionSize()
    val int get() = rdContext.nextInt()
    val boolean get() = rdContext.nextInt()
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
    fun `list in type param - 3 nested`() {
        testGeneratedCodeUsingStandardPlugin(
            """
                $imports

                data class QxC<T1:Any>(override val data:T1):WithData

                fun runTest():TestOutput {
                    return withTestOutput {
                    
                        putData(random<QxC<List<List<List<Double>>>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<List<List<List<Qx2<Float>>>>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<List<List<List<Qx2<Qx4<String>>>>>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<List<List<List<TwoGeneric<Int, String>>>>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<List<List<List<TwoGeneric<Qx2<Int>, String>>>>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<List<List<List<TwoGeneric<Qx2<Int>, Qx4<String>>>>>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<List<List<List<ThreeGeneric<Int, String, Double>>>>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<List<List<List<ThreeGeneric<Int, Qx2<String>, Double>>>>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<List<List<List<ThreeGeneric<Qx6<Int>, Qx4<String>, Qx2<Double>>>>>>>(randomConfig=TestRandomConfig()))

                        putData(random<QxC<List<List<List<Double>>>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<List<List<List<Qx2<Float>>>>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<List<List<List<Qx2<Qx4<String>>>>>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<List<List<List<TwoGeneric<Int, String>>>>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<List<List<List<TwoGeneric<Qx2<Int>, String>>>>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<List<List<List<TwoGeneric<Qx2<Int>, Qx4<String>>>>>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<List<List<List<ThreeGeneric<Int, String, Double>>>>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<List<List<List<ThreeGeneric<Int, Qx2<String>, Double>>>>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<List<List<List<ThreeGeneric<Qx6<Int>, Qx4<String>, Qx2<Double>>>>>>>(randomConfig=TestRandomConfig()))
                    }
                }
            """,
        ) {
            testCompilation = { result, _ ->
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                val objectList = result.executeRunTestFunction().getObjs()

                objectList shouldBe listOf(
                    makeList(size, { rdConfig.resetRandomState() }) { List(size) { List(size) { double } } },
                    makeList(size, { rdConfig.resetRandomState() }) { List(size) { List(size) { Qx2(float) } } },
                    makeList(size, { rdConfig.resetRandomState() }) { List(size) { List(size) { Qx2(Qx4(str)) } } },
                    makeList(size, { rdConfig.resetRandomState() }) {
                        List(size) {
                            List(size) {
                                TwoGeneric(
                                    int,
                                    str
                                )
                            }
                        }
                    },
                    makeList(size, { rdConfig.resetRandomState() }) {
                        List(size) {
                            List(size) {
                                TwoGeneric(
                                    Qx2(int),
                                    str
                                )
                            }
                        }
                    },
                    makeList(size, { rdConfig.resetRandomState() }) {
                        List(size) {
                            List(size) {
                                TwoGeneric(
                                    Qx2(int),
                                    Qx4(str)
                                )
                            }
                        }
                    },
                    makeList(size, { rdConfig.resetRandomState() }) {
                        List(size) {
                            List(size) {
                                ThreeGeneric(
                                    int,
                                    str,
                                    double
                                )
                            }
                        }
                    },
                    makeList(size, { rdConfig.resetRandomState() }) {
                        List(size) {
                            List(size) {
                                ThreeGeneric(
                                    int,
                                    Qx2(str),
                                    double
                                )
                            }
                        }
                    },
                    makeList(size, { rdConfig.resetRandomState() }) {
                        List(size) {
                            List(size) {
                                ThreeGeneric(
                                    Qx6(int),
                                    Qx4(str),
                                    Qx2(double)
                                )
                            }
                        }
                    },

                    makeList(size, { rdConfig.resetRandomState() }) { List(size) { List(size) { double } } },
                    makeList(size, { rdConfig.resetRandomState() }) { List(size) { List(size) { Qx2(float) } } },
                    makeList(size, { rdConfig.resetRandomState() }) { List(size) { List(size) { Qx2(Qx4(str)) } } },
                    makeList(size, { rdConfig.resetRandomState() }) {
                        List(size) {
                            List(size) {
                                TwoGeneric(
                                    int,
                                    str
                                )
                            }
                        }
                    },
                    makeList(size, { rdConfig.resetRandomState() }) {
                        List(size) {
                            List(size) {
                                TwoGeneric(
                                    Qx2(int),
                                    str
                                )
                            }
                        }
                    },
                    makeList(size, { rdConfig.resetRandomState() }) {
                        List(size) {
                            List(size) {
                                TwoGeneric(
                                    Qx2(int),
                                    Qx4(str)
                                )
                            }
                        }
                    },
                    makeList(size, { rdConfig.resetRandomState() }) {
                        List(size) {
                            List(size) {
                                ThreeGeneric(
                                    int,
                                    str,
                                    double
                                )
                            }
                        }
                    },
                    makeList(size, { rdConfig.resetRandomState() }) {
                        List(size) {
                            List(size) {
                                ThreeGeneric(
                                    int,
                                    Qx2(str),
                                    double
                                )
                            }
                        }
                    },
                    makeList(size, { rdConfig.resetRandomState() }) {
                        List(size) {
                            List(size) {
                                ThreeGeneric(
                                    Qx6(int),
                                    Qx4(str),
                                    Qx2(double)
                                )
                            }
                        }
                    },
                )
            }
        }
    }

    @Test
    fun `list in type param - 1 nested`() {
        testGeneratedCodeUsingStandardPlugin(
            """
                $imports

                data class QxC<T1:Any>(override val data:T1):WithData

                fun runTest():TestOutput {
                    return withTestOutput {
                        putData(random<QxC<List<Double>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<List<Qx2<Float>>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<List<Qx2<Qx4<String>>>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<List<TwoGeneric<Int,String>>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<List<TwoGeneric<Qx2<Int>,String>>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<List<TwoGeneric<Qx2<Int>,Qx4<String>>>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<List<ThreeGeneric<Int,String,Double>>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<List<ThreeGeneric<Int,Qx2<String>,Double>>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<List<ThreeGeneric<Qx6<Int>,Qx4<String>,Qx2<Double>>>>>(randomConfig=TestRandomConfig()))

                        putData(random<QxC<List<Double>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<List<Qx2<Float>>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<List<Qx2<Qx4<String>>>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<List<TwoGeneric<Int,String>>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<List<TwoGeneric<Qx2<Int>,String>>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<List<TwoGeneric<Qx2<Int>,Qx4<String>>>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<List<ThreeGeneric<Int,String,Double>>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<List<ThreeGeneric<Int,Qx2<String>,Double>>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<List<ThreeGeneric<Qx6<Int>,Qx4<String>,Qx2<Double>>>>>(randomConfig=TestRandomConfig()))
                    }
                }
            """,
        ) {
            testCompilation = { result, _ ->
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                val objectList = result.executeRunTestFunction().getObjs()

                objectList shouldBe listOf(
                    makeList(size, { rdConfig.resetRandomState() }) {  double  },
                    makeList(size, { rdConfig.resetRandomState() }) {  Qx2(float) } ,
                    makeList(size, { rdConfig.resetRandomState() }) { Qx2(Qx4(str)) },
                    makeList(size, { rdConfig.resetRandomState() }) { TwoGeneric(int, str) },
                    makeList(size, { rdConfig.resetRandomState() }) { TwoGeneric(Qx2(int), str) },
                    makeList(size, { rdConfig.resetRandomState() }) { TwoGeneric(Qx2(int), Qx4(str)) },
                    makeList(size, { rdConfig.resetRandomState() }) { ThreeGeneric(int, str, double) },
                    makeList(size, { rdConfig.resetRandomState() }) {
                        ThreeGeneric(
                            int,
                            Qx2(str),
                            double
                        )
                    },
                    makeList(size, { rdConfig.resetRandomState() }) {
                        ThreeGeneric(
                            Qx6(int),
                            Qx4(str),
                            Qx2(double)
                        )
                    },

                    makeList(size, { rdConfig.resetRandomState() }) { double },
                    makeList(size, { rdConfig.resetRandomState() }) { Qx2(float) },
                    makeList(size, { rdConfig.resetRandomState() }) { Qx2(Qx4(str)) },
                    makeList(size, { rdConfig.resetRandomState() }) { TwoGeneric(int, str) },
                    makeList(size, { rdConfig.resetRandomState() }) { TwoGeneric(Qx2(int), str) },
                    makeList(size, { rdConfig.resetRandomState() }) { TwoGeneric(Qx2(int), Qx4(str)) },
                    makeList(size, { rdConfig.resetRandomState() }) { ThreeGeneric(int, str, double) },
                    makeList(size, { rdConfig.resetRandomState() }) {
                        ThreeGeneric(
                            int,
                            Qx2(str),
                            double
                        )
                    },
                    makeList(size, { rdConfig.resetRandomState() }) {
                        ThreeGeneric(
                            Qx6(int),
                            Qx4(str),
                            Qx2(double)
                        )
                    },
                )
            }
        }
    }

    @Test
    fun `list in type param - 2 nested`() {
        testGeneratedCodeUsingStandardPlugin(
            """
                $imports

                data class QxC<T1:Any>(override val data:T1):WithData

                fun runTest():TestOutput {
                    return withTestOutput {
                        putData(random<QxC<List<List<Double>>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<List<List<Qx2<Float>>>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<List<List<Qx2<Qx4<String>>>>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<List<List<TwoGeneric<Int,String>>>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<List<List<TwoGeneric<Qx2<Int>,String>>>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<List<List<TwoGeneric<Qx2<Int>,Qx4<String>>>>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<List<List<ThreeGeneric<Int,String,Double>>>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<List<List<ThreeGeneric<Int,Qx2<String>,Double>>>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<List<List<ThreeGeneric<Qx6<Int>,Qx4<String>,Qx2<Double>>>>>>(randomConfig=TestRandomConfig()))

                        putData(random<QxC<List<List<Double>>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<List<List<Qx2<Float>>>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<List<List<Qx2<Qx4<String>>>>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<List<List<TwoGeneric<Int,String>>>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<List<List<TwoGeneric<Qx2<Int>,String>>>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<List<List<TwoGeneric<Qx2<Int>,Qx4<String>>>>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<List<List<ThreeGeneric<Int,String,Double>>>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<List<List<ThreeGeneric<Int,Qx2<String>,Double>>>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<List<List<ThreeGeneric<Qx6<Int>,Qx4<String>,Qx2<Double>>>>>>(randomConfig=TestRandomConfig()))
                    }
                }
            """,
        ) {
            testCompilation = { result, _ ->
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                val objectList = result.executeRunTestFunction().getObjs()

                objectList shouldBe listOf(
                    makeList(size, { rdConfig.resetRandomState() }) { List(size) { double } },
                    makeList(size, { rdConfig.resetRandomState() }) { List(size) { Qx2(float) } },
                    makeList(size, { rdConfig.resetRandomState() }) { List(size) { Qx2(Qx4(str)) } },
                    makeList(size, { rdConfig.resetRandomState() }) { List(size) { TwoGeneric(int, str) } },
                    makeList(size, { rdConfig.resetRandomState() }) { List(size) { TwoGeneric(Qx2(int), str) } },
                    makeList(size, { rdConfig.resetRandomState() }) { List(size) { TwoGeneric(Qx2(int), Qx4(str)) } },
                    makeList(size, { rdConfig.resetRandomState() }) { List(size) { ThreeGeneric(int, str, double) } },
                    makeList(size, { rdConfig.resetRandomState() }) {
                        List(size) {
                            ThreeGeneric(
                                int,
                                Qx2(str),
                                double
                            )
                        }
                    },
                    makeList(size, { rdConfig.resetRandomState() }) {
                        List(size) {
                            ThreeGeneric(
                                Qx6(int),
                                Qx4(str),
                                Qx2(double)
                            )
                        }
                    },

                    makeList(size, { rdConfig.resetRandomState() }) { List(size) { double } },
                    makeList(size, { rdConfig.resetRandomState() }) { List(size) { Qx2(float) } },
                    makeList(size, { rdConfig.resetRandomState() }) { List(size) { Qx2(Qx4(str)) } },
                    makeList(size, { rdConfig.resetRandomState() }) { List(size) { TwoGeneric(int, str) } },
                    makeList(size, { rdConfig.resetRandomState() }) { List(size) { TwoGeneric(Qx2(int), str) } },
                    makeList(size, { rdConfig.resetRandomState() }) { List(size) { TwoGeneric(Qx2(int), Qx4(str)) } },
                    makeList(size, { rdConfig.resetRandomState() }) { List(size) { ThreeGeneric(int, str, double) } },
                    makeList(size, { rdConfig.resetRandomState() }) {
                        List(size) {
                            ThreeGeneric(
                                int,
                                Qx2(str),
                                double
                            )
                        }
                    },
                    makeList(size, { rdConfig.resetRandomState() }) {
                        List(size) {
                            ThreeGeneric(
                                Qx6(int),
                                Qx4(str),
                                Qx2(double)
                            )
                        }
                    },
                )
            }
        }
    }


    @Test
    fun `list in value param with 3 nested list`() {
        testGeneratedCodeUsingStandardPlugin(
            """
                $imports

                data class QxC<T1>(override val data:List<List<List<T1>>>):WithData

                fun runTest():TestOutput {
                    return withTestOutput {
                        putData(random<QxC<Double>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<Qx2<Float>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<Qx2<Qx4<String>>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<TwoGeneric<Int,String>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<TwoGeneric<Qx2<Int>,String>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<TwoGeneric<Qx2<Int>,Qx4<String>>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<ThreeGeneric<Int,String,Double>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<ThreeGeneric<Int,Qx2<String>,Double>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<ThreeGeneric<Qx6<Int>,Qx4<String>,Qx2<Double>>>>(randomConfig=TestRandomConfig()))

                        putData(random<QxC<Double>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<Qx2<Float>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<Qx2<Qx4<String>>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<TwoGeneric<Int,String>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<TwoGeneric<Qx2<Int>,String>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<TwoGeneric<Qx2<Int>,Qx4<String>>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<ThreeGeneric<Int,String,Double>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<ThreeGeneric<Int,Qx2<String>,Double>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<ThreeGeneric<Qx6<Int>,Qx4<String>,Qx2<Double>>>>(randomConfig=TestRandomConfig()))
                    }
                }
            """,
        ) {
            testCompilation = { result, _ ->
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                val objectList = result.executeRunTestFunction().getObjs()

                objectList shouldBe listOf(
                    makeList(size, { rdConfig.resetRandomState() }) { List(size) { List(size) { double } } },
                    makeList(size, { rdConfig.resetRandomState() }) { List(size) { List(size) { Qx2(float) } } },
                    makeList(size, { rdConfig.resetRandomState() }) { List(size) { List(size) { Qx2(Qx4(str)) } } },
                    makeList(size, { rdConfig.resetRandomState() }) {
                        List(size) {
                            List(size) {
                                TwoGeneric(
                                    int,
                                    str
                                )
                            }
                        }
                    },
                    makeList(size, { rdConfig.resetRandomState() }) {
                        List(size) {
                            List(size) {
                                TwoGeneric(
                                    Qx2(int),
                                    str
                                )
                            }
                        }
                    },
                    makeList(size, { rdConfig.resetRandomState() }) {
                        List(size) {
                            List(size) {
                                TwoGeneric(
                                    Qx2(int),
                                    Qx4(str)
                                )
                            }
                        }
                    },
                    makeList(size, { rdConfig.resetRandomState() }) {
                        List(size) {
                            List(size) {
                                ThreeGeneric(
                                    int,
                                    str,
                                    double
                                )
                            }
                        }
                    },
                    makeList(size, { rdConfig.resetRandomState() }) {
                        List(size) {
                            List(size) {
                                ThreeGeneric(
                                    int,
                                    Qx2(str),
                                    double
                                )
                            }
                        }
                    },
                    makeList(size, { rdConfig.resetRandomState() }) {
                        List(size) {
                            List(size) {
                                ThreeGeneric(
                                    Qx6(int),
                                    Qx4(str),
                                    Qx2(double)
                                )
                            }
                        }
                    },

                    makeList(size, { rdConfig.resetRandomState() }) { List(size) { List(size) { double } } },
                    makeList(size, { rdConfig.resetRandomState() }) { List(size) { List(size) { Qx2(float) } } },
                    makeList(size, { rdConfig.resetRandomState() }) { List(size) { List(size) { Qx2(Qx4(str)) } } },
                    makeList(size, { rdConfig.resetRandomState() }) {
                        List(size) {
                            List(size) {
                                TwoGeneric(
                                    int,
                                    str
                                )
                            }
                        }
                    },
                    makeList(size, { rdConfig.resetRandomState() }) {
                        List(size) {
                            List(size) {
                                TwoGeneric(
                                    Qx2(int),
                                    str
                                )
                            }
                        }
                    },
                    makeList(size, { rdConfig.resetRandomState() }) {
                        List(size) {
                            List(size) {
                                TwoGeneric(
                                    Qx2(int),
                                    Qx4(str)
                                )
                            }
                        }
                    },
                    makeList(size, { rdConfig.resetRandomState() }) {
                        List(size) {
                            List(size) {
                                ThreeGeneric(
                                    int,
                                    str,
                                    double
                                )
                            }
                        }
                    },
                    makeList(size, { rdConfig.resetRandomState() }) {
                        List(size) {
                            List(size) {
                                ThreeGeneric(
                                    int,
                                    Qx2(str),
                                    double
                                )
                            }
                        }
                    },
                    makeList(size, { rdConfig.resetRandomState() }) {
                        List(size) {
                            List(size) {
                                ThreeGeneric(
                                    Qx6(int),
                                    Qx4(str),
                                    Qx2(double)
                                )
                            }
                        }
                    },
                )
            }
        }
    }

    /**
     * something like this: random<Int>() ~> param:List<Int>
     */
    @Test
    fun `list in value param with 2 nested list`() {
        testGeneratedCodeUsingStandardPlugin(
            """
                $imports

                data class QxC<T1>(override val data:List<List<T1>>):WithData

                fun runTest():TestOutput {
                    return withTestOutput {
                        putData(random<QxC<Double>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<Qx2<Float>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<Qx2<Qx4<String>>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<TwoGeneric<Int,String>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<TwoGeneric<Qx2<Int>,String>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<TwoGeneric<Qx2<Int>,Qx4<String>>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<ThreeGeneric<Int,String,Double>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<ThreeGeneric<Int,Qx2<String>,Double>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<ThreeGeneric<Qx6<Int>,Qx4<String>,Qx2<Double>>>>(randomConfig=TestRandomConfig()))

                        putData(random<QxC<Double>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<Qx2<Float>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<Qx2<Qx4<String>>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<TwoGeneric<Int,String>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<TwoGeneric<Qx2<Int>,String>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<TwoGeneric<Qx2<Int>,Qx4<String>>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<ThreeGeneric<Int,String,Double>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<ThreeGeneric<Int,Qx2<String>,Double>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<ThreeGeneric<Qx6<Int>,Qx4<String>,Qx2<Double>>>>(randomConfig=TestRandomConfig()))
                    }
                }
            """,
        ) {
            testCompilation = { result, _ ->
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                val objectList = result.executeRunTestFunction().getObjs()

                objectList shouldBe listOf(
                    makeList(size, { rdConfig.resetRandomState() }) { List(size) { double } },
                    makeList(size, { rdConfig.resetRandomState() }) { List(size) { Qx2(float) } },
                    makeList(size, { rdConfig.resetRandomState() }) { List(size) { Qx2(Qx4(str)) } },
                    makeList(size, { rdConfig.resetRandomState() }) { List(size) { TwoGeneric(int, str) } },
                    makeList(size, { rdConfig.resetRandomState() }) { List(size) { TwoGeneric(Qx2(int), str) } },
                    makeList(size, { rdConfig.resetRandomState() }) { List(size) { TwoGeneric(Qx2(int), Qx4(str)) } },
                    makeList(size, { rdConfig.resetRandomState() }) { List(size) { ThreeGeneric(int, str, double) } },
                    makeList(size, { rdConfig.resetRandomState() }) {
                        List(size) {
                            ThreeGeneric(
                                int,
                                Qx2(str),
                                double
                            )
                        }
                    },
                    makeList(size, { rdConfig.resetRandomState() }) {
                        List(size) {
                            ThreeGeneric(
                                Qx6(int),
                                Qx4(str),
                                Qx2(double)
                            )
                        }
                    },

                    makeList(size, { rdConfig.resetRandomState() }) { List(size) { double } },
                    makeList(size, { rdConfig.resetRandomState() }) { List(size) { Qx2(float) } },
                    makeList(size, { rdConfig.resetRandomState() }) { List(size) { Qx2(Qx4(str)) } },
                    makeList(size, { rdConfig.resetRandomState() }) { List(size) { TwoGeneric(int, str) } },
                    makeList(size, { rdConfig.resetRandomState() }) { List(size) { TwoGeneric(Qx2(int), str) } },
                    makeList(size, { rdConfig.resetRandomState() }) { List(size) { TwoGeneric(Qx2(int), Qx4(str)) } },
                    makeList(size, { rdConfig.resetRandomState() }) { List(size) { ThreeGeneric(int, str, double) } },
                    makeList(size, { rdConfig.resetRandomState() }) {
                        List(size) {
                            ThreeGeneric(
                                int,
                                Qx2(str),
                                double
                            )
                        }
                    },
                    makeList(size, { rdConfig.resetRandomState() }) {
                        List(size) {
                            ThreeGeneric(
                                Qx6(int),
                                Qx4(str),
                                Qx2(double)
                            )
                        }
                    },
                )
            }
        }
    }

    /**
     * something like this: random<Int>() ~> param:List<Int>
     */
    @Test
    fun `list in value param`() {
        testGeneratedCodeUsingStandardPlugin(
            """
                $imports
                data class QxC<T1:Any>(override val data:List<T1>):WithData

                fun runTest():TestOutput {
                    return withTestOutput{
                        putData(random<QxC<Int>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<Qx2<Float>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<Qx2<Qx4<String>>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<TwoGeneric<Int,String>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<TwoGeneric<Qx2<Int>,String>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<TwoGeneric<Qx2<Int>,Qx4<String>>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<ThreeGeneric<Int,String,Double>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<ThreeGeneric<Int,Qx2<String>,Double>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<ThreeGeneric<Qx6<Int>,Qx4<String>,Qx2<Double>>>>(randomConfig=TestRandomConfig()))
                        val rdConfig = TestRandomConfig()
                        putData(random<QxC<Int>>(randomConfig=rdConfig))
                        putData(random<QxC<Qx2<Float>>>(randomConfig=rdConfig))
                        putData(random<QxC<Qx2<Qx4<String>>>>(randomConfig=rdConfig))
                        putData(random<QxC<TwoGeneric<Int,String>>>(randomConfig=rdConfig))
                        putData(random<QxC<TwoGeneric<Qx2<Int>,String>>>(randomConfig=rdConfig))
                        putData(random<QxC<TwoGeneric<Qx2<Int>,Qx4<String>>>>(randomConfig=rdConfig))
                        putData(random<QxC<ThreeGeneric<Int,String,Double>>>(randomConfig=rdConfig))
                        putData(random<QxC<ThreeGeneric<Int,Qx2<String>,Double>>>(randomConfig=rdConfig))
                        putData(random<QxC<ThreeGeneric<Qx6<Int>,Qx4<String>,Qx2<Double>>>>(randomConfig=rdConfig))
                    }
                }
            """,
        ) {
            testCompilation = { result, _ ->
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                val objectList = result.executeRunTestFunction().getObjs()

                val e1 = listOf(
                    makeList(size, { rdConfig.resetRandomState() }) { int },
                    makeList(size, { rdConfig.resetRandomState() }) { Qx2(float) },
                    makeList(size, { rdConfig.resetRandomState() }) { Qx2(Qx4(str)) },
                    makeList(size, { rdConfig.resetRandomState() }) { TwoGeneric(int, str) },
                    makeList(size, { rdConfig.resetRandomState() }) { TwoGeneric(Qx2(int), str) },
                    makeList(size, { rdConfig.resetRandomState() }) { TwoGeneric(Qx2(int), Qx4(str)) },
                    makeList(size, { rdConfig.resetRandomState() }) { ThreeGeneric(int, str, double) },
                    makeList(size, { rdConfig.resetRandomState() }) { ThreeGeneric(int, Qx2(str), double) },
                    makeList(size, { rdConfig.resetRandomState() }) { ThreeGeneric(Qx6(int), Qx4(str), Qx2(double)) },
                )
                rdConfig.resetRandomState()
                val e2:List<List<Any>> = listOf(
                    List(size) { int },
                    List(size) { Qx2(float) },
                    List(size) { Qx2(Qx4(str)) },
                    List(size) { TwoGeneric(int, str) },
                    List(size) { TwoGeneric(Qx2(int), str) },
                    List(size) { TwoGeneric(Qx2(int), Qx4(str)) },
                    List(size) { ThreeGeneric(int, str, double) },
                    List(size) { ThreeGeneric(int, Qx2(str), double) },
                    List(size) { ThreeGeneric(Qx6(int), Qx4(str), Qx2(double)) },
                )

                val expectation = e1 + e2
                objectList shouldBe expectation
            }
        }
    }

    @Test
    fun `list in value param with custom randomizer`() {
        testGeneratedCodeUsingStandardPlugin(
            """
                $imports

                data class QxC<T1:Any>(override val data:List<T1>):WithData

                fun runTest():TestOutput {
                    return withTestOutput{
                        putData(random<QxC<Int>>(randomConfig=TestRandomConfig(), randomizers = {
                             constant(123)
                        }))
                        putData(random<QxC<Int>>(randomConfig=TestRandomConfig(), randomizers = {
                             constant(listOf(1))
                        }))
                        putData(random<QxC<Qx2<Float>>>(
                            randomConfig=TestRandomConfig(), 
                            randomizers = {
                                factory{999f}
                            }
                        ))
                        putData(random<QxC<Qx2<Float>>>(
                            randomConfig=TestRandomConfig(), 
                            randomizers = {
                                constant(Qx2(222f))
                            }
                        ))
                        putData(random<QxC<Qx2<Float>>>(
                            randomConfig=TestRandomConfig(), 
                            randomizers = {
                                constant{ listOf(Qx2(1f)) }
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
                    makeList(size, { rdConfig.resetRandomState() }) { 123 },
                    listOf(1),
                    makeList(size, { rdConfig.resetRandomState() }) {
                        Qx2(999f)
                    },
                    makeList(size, { rdConfig.resetRandomState() }) {
                        Qx2(222f)
                    },
                    listOf(Qx2(1f)),
                )
            }
        }
    }

    @Test
    fun `list in type param with custom randomizer`() {
        testGeneratedCodeUsingStandardPlugin(
            """
                $imports

                data class QxC<T1:Any>(override val data:T1):WithData

                fun runTest():TestOutput {
                    return withTestOutput {
                        putData(random<QxC<List<List<Double>>>>(
                            randomConfig=TestRandomConfig(), 
                            randomizers = {
                                constant{listOf((listOf(123.0)))}
                            }
                        ))
                        putData(random<QxC<List<Qx2<Float>>>>(
                            randomConfig=TestRandomConfig(), 
                            randomizers = {
                                constant{listOf(Qx2(123f),Qx2(222f))}
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
                    listOf(listOf(123.0)),
                    listOf(
                        Qx2(123f),
                        Qx2(222f),
                    )
                )
            }
        }
    }

    private fun <T> makeList(size: Int, sideEffect: () -> Unit, makeElement: () -> T): List<T> {
        sideEffect()
        return List(size) { makeElement() }
    }
}
