package com.x12q.kotlin.randomizer.ir_plugin

import com.tschuchort.compiletesting.KotlinCompilation
import com.x12q.kotlin.randomizer.ir_plugin.mock_objects.TestRandomConfig
import com.x12q.kotlin.randomizer.lib.RandomContext
import com.x12q.kotlin.randomizer.lib.RandomContextBuilderImp
import com.x12q.kotlin.randomizer.lib.randomizer.factoryRandomizer
import com.x12q.kotlin.randomizer.test_utils.assertions.executeRunTestFunction
import com.x12q.kotlin.randomizer.test_utils.makeList
import com.x12q.kotlin.randomizer.test_utils.test_code.TestImportsBuilder
import io.kotest.matchers.shouldBe
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import kotlin.test.BeforeTest
import kotlin.test.Test


@OptIn(ExperimentalCompilerApi::class)
class TestRandomHashSet {

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

    val size get()= rdContext.randomCollectionSize()
    val int get()= rdContext.nextInt()
    val boolean get()= rdContext.nextInt()
    val float get()= rdContext.nextFloat()
    val str get()= rdContext.nextString()
    val double get()= rdContext.nextDouble()
    val short get()= rdContext.nextShort()


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
                    
                        putData(random<QxC<HashSet<HashSet<HashSet<Double>>>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<HashSet<HashSet<HashSet<Qx2<Float>>>>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<HashSet<HashSet<HashSet<Qx2<Qx4<String>>>>>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<HashSet<HashSet<HashSet<TwoGeneric<Int, String>>>>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<HashSet<HashSet<HashSet<TwoGeneric<Qx2<Int>, String>>>>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<HashSet<HashSet<HashSet<TwoGeneric<Qx2<Int>, Qx4<String>>>>>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<HashSet<HashSet<HashSet<ThreeGeneric<Int, String, Double>>>>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<HashSet<HashSet<HashSet<ThreeGeneric<Int, Qx2<String>, Double>>>>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<HashSet<HashSet<HashSet<ThreeGeneric<Qx6<Int>, Qx4<String>, Qx2<Double>>>>>>>(randomConfig=TestRandomConfig()))

                        putData(random<QxC<HashSet<HashSet<HashSet<Double>>>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<HashSet<HashSet<HashSet<Qx2<Float>>>>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<HashSet<HashSet<HashSet<Qx2<Qx4<String>>>>>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<HashSet<HashSet<HashSet<TwoGeneric<Int, String>>>>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<HashSet<HashSet<HashSet<TwoGeneric<Qx2<Int>, String>>>>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<HashSet<HashSet<HashSet<TwoGeneric<Qx2<Int>, Qx4<String>>>>>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<HashSet<HashSet<HashSet<ThreeGeneric<Int, String, Double>>>>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<HashSet<HashSet<HashSet<ThreeGeneric<Int, Qx2<String>, Double>>>>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<HashSet<HashSet<HashSet<ThreeGeneric<Qx6<Int>, Qx4<String>, Qx2<Double>>>>>>>(randomConfig=TestRandomConfig()))
                    }
                }
            """,
        ) {
            testCompilation = { result, _ ->
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                val objectList = result.executeRunTestFunction().getObjs()

                objectList shouldBe listOf(
                    makeList(size,{rdConfig.resetRandomState()}) { List(size) { List(size) { double }.toHashSet() }.toHashSet() }.toHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { List(size) { List(size) { Qx2(float) }.toHashSet() }.toHashSet() }.toHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { List(size) { List(size) { Qx2(Qx4(str)) }.toHashSet() }.toHashSet() }.toHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { List(size) { List(size) { TwoGeneric(int, str) }.toHashSet() }.toHashSet() }.toHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { List(size) { List(size) { TwoGeneric(Qx2(int), str) }.toHashSet() }.toHashSet() }.toHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { List(size) { List(size) { TwoGeneric(Qx2(int), Qx4(str)) }.toHashSet() }.toHashSet() }.toHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { List(size) { List(size) { ThreeGeneric(int, str, double) }.toHashSet() }.toHashSet() }.toHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { List(size) { List(size) { ThreeGeneric(int, Qx2(str), double) }.toHashSet() }.toHashSet() }.toHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { List(size) { List(size) { ThreeGeneric(Qx6(int), Qx4(str), Qx2(double)) }.toHashSet() }.toHashSet() }.toHashSet(),

                    makeList(size,{rdConfig.resetRandomState()}) { List(size) { List(size) { double }.toHashSet() }.toHashSet() }.toHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { List(size) { List(size) { Qx2(float) }.toHashSet() }.toHashSet() }.toHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { List(size) { List(size) { Qx2(Qx4(str)) }.toHashSet() }.toHashSet() }.toHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { List(size) { List(size) { TwoGeneric(int, str) }.toHashSet() }.toHashSet() }.toHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { List(size) { List(size) { TwoGeneric(Qx2(int), str) }.toHashSet() }.toHashSet() }.toHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { List(size) { List(size) { TwoGeneric(Qx2(int), Qx4(str)) }.toHashSet() }.toHashSet() }.toHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { List(size) { List(size) { ThreeGeneric(int, str, double) }.toHashSet() }.toHashSet() }.toHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { List(size) { List(size) { ThreeGeneric(int, Qx2(str), double) }.toHashSet() }.toHashSet() }.toHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { List(size) { List(size) { ThreeGeneric(Qx6(int), Qx4(str), Qx2(double)) }.toHashSet() }.toHashSet() }.toHashSet(),
                )
            }
        }
    }

    @Test
    fun `set in type param - 3 nested - with custom randomizer`() {
        testGeneratedCodeUsingStandardPlugin(
            """
                $imports

                
                data class QxC<T1:Any>(override val data:T1):WithData

                fun runTest():TestOutput {
                    return withTestOutput {
                        putData(random<QxC<HashSet<HashSet<HashSet<Double>>>>>(
                            randomConfig=TestRandomConfig(), 
                            randomizers = {
                                constant{333.222}
                            }
                        ))
                        putData(random<QxC<HashSet<HashSet<HashSet<Double>>>>>(
                            randomConfig=TestRandomConfig(), 
                            randomizers = {
                                constant{hashSetOf(hashSetOf(hashSetOf(1.0),hashSetOf(2.0)),hashSetOf(hashSetOf(3.0),hashSetOf(4.0)))}
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
                    makeList(size,{rdConfig.resetRandomState()}) { List(size) { List(size) { 333.222 }.toHashSet() }.toHashSet() }.toHashSet(),
                    hashSetOf(hashSetOf(hashSetOf(1.0),hashSetOf(2.0)),hashSetOf(hashSetOf(3.0),hashSetOf(4.0)))
                )
            }
        }
    }


    @Test
    fun `set in type param - 2 nested`() {
        testGeneratedCodeUsingStandardPlugin(
            """
                $imports

                
                data class QxC<T1:Any>(override val data:T1):WithData

                fun runTest():TestOutput {
                    return withTestOutput {
                        putData(random<QxC<HashSet<HashSet<Double>>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<HashSet<HashSet<Qx2<Float>>>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<HashSet<HashSet<Qx2<Qx4<String>>>>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<HashSet<HashSet<TwoGeneric<Int,String>>>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<HashSet<HashSet<TwoGeneric<Qx2<Int>,String>>>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<HashSet<HashSet<TwoGeneric<Qx2<Int>,Qx4<String>>>>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<HashSet<HashSet<ThreeGeneric<Int,String,Double>>>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<HashSet<HashSet<ThreeGeneric<Int,Qx2<String>,Double>>>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<HashSet<HashSet<ThreeGeneric<Qx6<Int>,Qx4<String>,Qx2<Double>>>>>>(randomConfig=TestRandomConfig()))

                        putData(random<QxC<HashSet<HashSet<Double>>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<HashSet<HashSet<Qx2<Float>>>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<HashSet<HashSet<Qx2<Qx4<String>>>>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<HashSet<HashSet<TwoGeneric<Int,String>>>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<HashSet<HashSet<TwoGeneric<Qx2<Int>,String>>>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<HashSet<HashSet<TwoGeneric<Qx2<Int>,Qx4<String>>>>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<HashSet<HashSet<ThreeGeneric<Int,String,Double>>>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<HashSet<HashSet<ThreeGeneric<Int,Qx2<String>,Double>>>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<HashSet<HashSet<ThreeGeneric<Qx6<Int>,Qx4<String>,Qx2<Double>>>>>>(randomConfig=TestRandomConfig()))
                    }
                }
            """,
        ) {
            testCompilation = { result, _ ->
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                val objectList = result.executeRunTestFunction().getObjs()

                objectList shouldBe listOf(
                    makeList(size,{rdConfig.resetRandomState()}) { List(size) { double }.toHashSet() }.toHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { List(size) { Qx2(float) }.toHashSet() }.toHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { List(size) { Qx2(Qx4(str)) }.toHashSet() }.toHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { List(size) { TwoGeneric(int, str) }.toHashSet() }.toHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { List(size) { TwoGeneric(Qx2(int), str) }.toHashSet() }.toHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { List(size) { TwoGeneric(Qx2(int), Qx4(str)) }.toHashSet() }.toHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { List(size) { ThreeGeneric(int, str, double) }.toHashSet() }.toHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { List(size) { ThreeGeneric(int, Qx2(str), double) }.toHashSet() }.toHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { List(size) { ThreeGeneric(Qx6(int), Qx4(str), Qx2(double)) }.toHashSet() }.toHashSet(),

                    makeList(size,{rdConfig.resetRandomState()}) { List(size) { double }.toHashSet() }.toHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { List(size) { Qx2(float) }.toHashSet() }.toHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { List(size) { Qx2(Qx4(str)) }.toHashSet() }.toHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { List(size) { TwoGeneric(int, str) }.toHashSet() }.toHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { List(size) { TwoGeneric(Qx2(int), str) }.toHashSet() }.toHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { List(size) { TwoGeneric(Qx2(int), Qx4(str)) }.toHashSet() }.toHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { List(size) { ThreeGeneric(int, str, double) }.toHashSet() }.toHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { List(size) { ThreeGeneric(int, Qx2(str), double) }.toHashSet() }.toHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { List(size) { ThreeGeneric(Qx6(int), Qx4(str), Qx2(double)) }.toHashSet() }.toHashSet(),
                )
            }
        }
    }

    @Test
    fun `set in type param - 2 nested - with custom randomizer`() {
        testGeneratedCodeUsingStandardPlugin(
            """
                $imports

                
                data class QxC<T1:Any>(override val data:T1):WithData

                fun runTest():TestOutput {
                    return withTestOutput {
                        putData(random<QxC<HashSet<HashSet<Double>>>>(
                            randomConfig=TestRandomConfig(),
                            randomizers = {
                                constant{1.0}
                            }
                        ))
                        putData(random<QxC<HashSet<HashSet<Double>>>>(
                            randomConfig=TestRandomConfig(),
                            randomizers = {
                                constant{hashSetOf(hashSetOf(1.0),hashSetOf(2.0))}
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
                    makeList(size,{rdConfig.resetRandomState()}) { List(size) { 1.0 }.toHashSet() }.toHashSet(),
                    hashSetOf(hashSetOf(1.0),hashSetOf(2.0)),
                )
            }
        }
    }

    @Test
    fun `set in type param`() {
        testGeneratedCodeUsingStandardPlugin(
            """
                $imports

                
                data class QxC<T1:Any>(override val data:T1):WithData

                fun runTest():TestOutput {
                    return withTestOutput {
                        putData(random<QxC<HashSet<Double>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<HashSet<Qx2<Float>>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<HashSet<Qx2<Qx4<String>>>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<HashSet<TwoGeneric<Int, String>>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<HashSet<TwoGeneric<Qx2<Int>, String>>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<HashSet<TwoGeneric<Qx2<Int>, Qx4<String>>>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<HashSet<ThreeGeneric<Int, String, Double>>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<HashSet<ThreeGeneric<Int, Qx2<String>, Double>>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<HashSet<ThreeGeneric<Qx6<Int>, Qx4<String>, Qx2<Double>>>>>(randomConfig=TestRandomConfig()))
                        
                        putData(random<QxC<HashSet<Double>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<HashSet<Qx2<Float>>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<HashSet<Qx2<Qx4<String>>>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<HashSet<TwoGeneric<Int, String>>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<HashSet<TwoGeneric<Qx2<Int>, String>>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<HashSet<TwoGeneric<Qx2<Int>, Qx4<String>>>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<HashSet<ThreeGeneric<Int, String, Double>>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<HashSet<ThreeGeneric<Int, Qx2<String>, Double>>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<HashSet<ThreeGeneric<Qx6<Int>, Qx4<String>, Qx2<Double>>>>>(randomConfig=TestRandomConfig()))
                    }
                }
            """,
        ) {
            testCompilation = { result, _ ->
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                val objectList = result.executeRunTestFunction().getObjs()

                objectList shouldBe listOf(
                    makeList(size,{rdConfig.resetRandomState()}) { double }.toHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { Qx2(float) }.toHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { Qx2(Qx4(str)) }.toHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { TwoGeneric(int, str) }.toHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { TwoGeneric(Qx2(int), str) }.toHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { TwoGeneric(Qx2(int), Qx4(str)) }.toHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { ThreeGeneric(int, str, double) }.toHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { ThreeGeneric(int, Qx2(str), double) }.toHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { ThreeGeneric(Qx6(int), Qx4(str), Qx2(double)) }.toHashSet(),

                    makeList(size,{rdConfig.resetRandomState()}) { double }.toHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { Qx2(float) }.toHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { Qx2(Qx4(str)) }.toHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { TwoGeneric(int, str) }.toHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { TwoGeneric(Qx2(int), str) }.toHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { TwoGeneric(Qx2(int), Qx4(str)) }.toHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { ThreeGeneric(int, str, double) }.toHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { ThreeGeneric(int, Qx2(str), double) }.toHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { ThreeGeneric(Qx6(int), Qx4(str), Qx2(double)) }.toHashSet(),

                    )
            }
        }
    }


    @Test
    fun `set in value param - 3 nested layers`() {
        testGeneratedCodeUsingStandardPlugin(
            """
                $imports

                
                data class QxC<T1>(override val data:HashSet<HashSet<HashSet<T1>>>):WithData

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
                    makeList(size,{rdConfig.resetRandomState()}) { List(size) { List(size) { double }.toHashSet() }.toHashSet() }.toHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { List(size) { List(size) { Qx2(float) }.toHashSet() }.toHashSet() }.toHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { List(size) { List(size) { Qx2(Qx4(str)) }.toHashSet() }.toHashSet() }.toHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { List(size) { List(size) { TwoGeneric(int, str) }.toHashSet() }.toHashSet() }.toHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { List(size) { List(size) { TwoGeneric(Qx2(int), str) }.toHashSet() }.toHashSet() }.toHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { List(size) { List(size) { TwoGeneric(Qx2(int), Qx4(str)) }.toHashSet() }.toHashSet() }.toHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { List(size) { List(size) { ThreeGeneric(int, str, double) }.toHashSet() }.toHashSet() }.toHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { List(size) { List(size) { ThreeGeneric(int, Qx2(str), double) }.toHashSet() }.toHashSet() }.toHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { List(size) { List(size) { ThreeGeneric(Qx6(int), Qx4(str), Qx2(double)) }.toHashSet() }.toHashSet() }.toHashSet(),

                    makeList(size,{rdConfig.resetRandomState()}) { List(size) { List(size) { double }.toHashSet() }.toHashSet() }.toHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { List(size) { List(size) { Qx2(float) }.toHashSet() }.toHashSet() }.toHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { List(size) { List(size) { Qx2(Qx4(str)) }.toHashSet() }.toHashSet() }.toHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { List(size) { List(size) { TwoGeneric(int, str) }.toHashSet() }.toHashSet() }.toHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { List(size) { List(size) { TwoGeneric(Qx2(int), str) }.toHashSet() }.toHashSet() }.toHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { List(size) { List(size) { TwoGeneric(Qx2(int), Qx4(str)) }.toHashSet() }.toHashSet() }.toHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { List(size) { List(size) { ThreeGeneric(int, str, double) }.toHashSet() }.toHashSet() }.toHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { List(size) { List(size) { ThreeGeneric(int, Qx2(str), double) }.toHashSet() }.toHashSet() }.toHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { List(size) { List(size) { ThreeGeneric(Qx6(int), Qx4(str), Qx2(double)) }.toHashSet() }.toHashSet() }.toHashSet(),
                )
            }
        }
    }

    @Test
    fun `set in value param - 3 nested layer - with custom randomizer`() {
        testGeneratedCodeUsingStandardPlugin(
            """
                $imports

                
                data class QxC<T1>(override val data:HashSet<HashSet<HashSet<T1>>>):WithData

                fun runTest():TestOutput {
                    return withTestOutput {
                        putData(random<QxC<Double>>(
                            randomConfig=TestRandomConfig(), 
                            randomizers = {
                                constant(123.222)
                            }
                        ))
                        putData(random<QxC<Double>>(
                            randomConfig=TestRandomConfig(),
                            randomizers = {
                                constant(hashSetOf(hashSetOf(hashSetOf(333.1111))))
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
                    makeList(size,{rdConfig.resetRandomState()}) { List(size) { List(size) { 123.222 }.toHashSet() }.toHashSet() }.toHashSet(),
                    hashSetOf(hashSetOf(hashSetOf(333.1111))),
                )
            }
        }
    }

    /**
     * something like this: random<Int>>(randomConfig=TestRandomConfig()) ~> param:List<Int>
     */
    @Test
    fun `set in value param - 2 nested layers`() {
        testGeneratedCodeUsingStandardPlugin(
            """
                $imports

                
                data class QxC<T1>(override val data:HashSet<HashSet<T1>>):WithData

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
                    makeList(size,{rdConfig.resetRandomState()}) { List(size) { double }.toHashSet() }.toHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { List(size) { Qx2(float) }.toHashSet() }.toHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { List(size) { Qx2(Qx4(str)) }.toHashSet() }.toHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { List(size) { TwoGeneric(int, str) }.toHashSet() }.toHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { List(size) { TwoGeneric(Qx2(int), str) }.toHashSet() }.toHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { List(size) { TwoGeneric(Qx2(int), Qx4(str)) }.toHashSet() }.toHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { List(size) { ThreeGeneric(int, str, double) }.toHashSet() }.toHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { List(size) { ThreeGeneric(int, Qx2(str), double) }.toHashSet() }.toHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { List(size) { ThreeGeneric(Qx6(int), Qx4(str), Qx2(double)) }.toHashSet() }.toHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { List(size) { double }.toHashSet() }.toHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { List(size) { Qx2(float) }.toHashSet() }.toHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { List(size) { Qx2(Qx4(str)) }.toHashSet() }.toHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { List(size) { TwoGeneric(int, str) }.toHashSet() }.toHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { List(size) { TwoGeneric(Qx2(int), str) }.toHashSet() }.toHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { List(size) { TwoGeneric(Qx2(int), Qx4(str)) }.toHashSet() }.toHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { List(size) { ThreeGeneric(int, str, double) }.toHashSet() }.toHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { List(size) { ThreeGeneric(int, Qx2(str), double) }.toHashSet() }.toHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { List(size) { ThreeGeneric(Qx6(int), Qx4(str), Qx2(double)) }.toHashSet() }.toHashSet(),
                )
            }
        }
    }

    @Test
    fun `set in value param - 2 nested layers - with custom randomizer`() {
        testGeneratedCodeUsingStandardPlugin(
            """
                $imports

                
                data class QxC<T1>(override val data:HashSet<HashSet<T1>>):WithData

                fun runTest():TestOutput {
                    return withTestOutput {
                        putData(random<QxC<Double>>(
                            randomConfig=TestRandomConfig(),
                            randomizers = {
                                constant(3.11)
                            }
                        ))
                        putData(random<QxC<Double>>(
                            randomConfig=TestRandomConfig(),
                            randomizers = {
                                constant(hashSetOf(hashSetOf(1.0),hashSetOf(2.0,3.0)))
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
                    makeList(size,{rdConfig.resetRandomState()}) { List(size) { 3.11 }.toHashSet() }.toHashSet(),
                    hashSetOf(hashSetOf(1.0),hashSetOf(2.0,3.0)),
                )
            }
        }
    }

    @Test
    fun `set in value param`() {
        testGeneratedCodeUsingStandardPlugin(
            """
                $imports

                
                data class QxC<T1:Any>(override val data:HashSet<T1>):WithData

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

                        putData(random<QxC<Int>>(randomConfig=TestRandomConfig()))
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
                    makeList(size,{rdConfig.resetRandomState()}) { int }.toHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { Qx2(float) }.toHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { Qx2(Qx4(str)) }.toHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { TwoGeneric(int, str) }.toHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { TwoGeneric(Qx2(int), str) }.toHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { TwoGeneric(Qx2(int), Qx4(str)) }.toHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { ThreeGeneric(int, str, double) }.toHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { ThreeGeneric(int, Qx2(str), double) }.toHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { ThreeGeneric(Qx6(int), Qx4(str), Qx2(double)) }.toHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { int }.toHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { Qx2(float) }.toHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { Qx2(Qx4(str)) }.toHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { TwoGeneric(int, str) }.toHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { TwoGeneric(Qx2(int), str) }.toHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { TwoGeneric(Qx2(int), Qx4(str)) }.toHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { ThreeGeneric(int, str, double) }.toHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { ThreeGeneric(int, Qx2(str), double) }.toHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { ThreeGeneric(Qx6(int), Qx4(str), Qx2(double)) }.toHashSet(),
                )
            }
        }
    }

    @Test
    fun `set in value param - with custom randomizer`() {
        testGeneratedCodeUsingStandardPlugin(
            """
                $imports

                
                data class QxC<T1:Any>(override val data:HashSet<T1>):WithData

                fun runTest():TestOutput {
                    return withTestOutput{
                        putData(random<QxC<Int>>(
                            randomConfig=TestRandomConfig(),
                            randomizers = {
                                constant{3}
                            }
                        ))
                        putData(random<QxC<Int>>(
                            randomConfig=TestRandomConfig(),
                            randomizers = {
                                constant<HashSet<Int>>{HashSet(setOf(1,2,3))}
                            }
                        ))
                        putData(random<QxC<Qx2<Float>>>(
                            randomConfig=TestRandomConfig(),
                            randomizers = {
                                 constant(Qx2(12f))
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
                    makeList(size,{rdConfig.resetRandomState()}) { 3 }.toHashSet(),
                    linkedSetOf(1,2,3),
                    makeList(size,{rdConfig.resetRandomState()}) { Qx2(12f) }.toHashSet(),
                )
            }
        }
    }



    private fun <T>  Iterable<T>.toHashSet(): HashSet<T> {
        return HashSet(this.toSet())
    }
}
