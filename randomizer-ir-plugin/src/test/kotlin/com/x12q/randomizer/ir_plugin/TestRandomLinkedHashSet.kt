package com.x12q.randomizer.ir_plugin

import com.tschuchort.compiletesting.KotlinCompilation
import com.x12q.randomizer.ir_plugin.mock_objects.TestRandomConfig
import com.x12q.randomizer.lib.RandomContext
import com.x12q.randomizer.lib.RandomContextBuilderImp
import com.x12q.randomizer.lib.randomizer.factoryRandomizer
import com.x12q.randomizer.test.util.assertions.executeRunTestFunction
import com.x12q.randomizer.test.util.makeList
import com.x12q.randomizer.test.util.test_code.TestImportsBuilder
import io.kotest.matchers.shouldBe
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import kotlin.test.BeforeTest
import kotlin.test.Test


@OptIn(ExperimentalCompilerApi::class)
class TestRandomLinkedHashSet {

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
                    
                        putData(random<QxC<LinkedHashSet<LinkedHashSet<LinkedHashSet<Double>>>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<LinkedHashSet<LinkedHashSet<LinkedHashSet<Qx2<Float>>>>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<LinkedHashSet<LinkedHashSet<LinkedHashSet<Qx2<Qx4<String>>>>>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<LinkedHashSet<LinkedHashSet<LinkedHashSet<TwoGeneric<Int, String>>>>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<LinkedHashSet<LinkedHashSet<LinkedHashSet<TwoGeneric<Qx2<Int>, String>>>>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<LinkedHashSet<LinkedHashSet<LinkedHashSet<TwoGeneric<Qx2<Int>, Qx4<String>>>>>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<LinkedHashSet<LinkedHashSet<LinkedHashSet<ThreeGeneric<Int, String, Double>>>>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<LinkedHashSet<LinkedHashSet<LinkedHashSet<ThreeGeneric<Int, Qx2<String>, Double>>>>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<LinkedHashSet<LinkedHashSet<LinkedHashSet<ThreeGeneric<Qx6<Int>, Qx4<String>, Qx2<Double>>>>>>>(randomConfig=TestRandomConfig()))

                        putData(random<QxC<LinkedHashSet<LinkedHashSet<LinkedHashSet<Double>>>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<LinkedHashSet<LinkedHashSet<LinkedHashSet<Qx2<Float>>>>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<LinkedHashSet<LinkedHashSet<LinkedHashSet<Qx2<Qx4<String>>>>>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<LinkedHashSet<LinkedHashSet<LinkedHashSet<TwoGeneric<Int, String>>>>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<LinkedHashSet<LinkedHashSet<LinkedHashSet<TwoGeneric<Qx2<Int>, String>>>>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<LinkedHashSet<LinkedHashSet<LinkedHashSet<TwoGeneric<Qx2<Int>, Qx4<String>>>>>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<LinkedHashSet<LinkedHashSet<LinkedHashSet<ThreeGeneric<Int, String, Double>>>>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<LinkedHashSet<LinkedHashSet<LinkedHashSet<ThreeGeneric<Int, Qx2<String>, Double>>>>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<LinkedHashSet<LinkedHashSet<LinkedHashSet<ThreeGeneric<Qx6<Int>, Qx4<String>, Qx2<Double>>>>>>>(randomConfig=TestRandomConfig()))
                    }
                }
            """,
        ) {
            testCompilation = { result, _ ->
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                val objectList = result.executeRunTestFunction().getObjs()

                objectList shouldBe listOf(
                    makeList(size,{rdConfig.resetRandomState()}) { List(size) { List(size) { double }.toLinkedHashSet() }.toLinkedHashSet() }.toLinkedHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { List(size) { List(size) { Qx2(float) }.toLinkedHashSet() }.toLinkedHashSet() }.toLinkedHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { List(size) { List(size) { Qx2(Qx4(str)) }.toLinkedHashSet() }.toLinkedHashSet() }.toLinkedHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { List(size) { List(size) { TwoGeneric(int, str) }.toLinkedHashSet() }.toLinkedHashSet() }.toLinkedHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { List(size) { List(size) { TwoGeneric(Qx2(int), str) }.toLinkedHashSet() }.toLinkedHashSet() }.toLinkedHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { List(size) { List(size) { TwoGeneric(Qx2(int), Qx4(str)) }.toLinkedHashSet() }.toLinkedHashSet() }.toLinkedHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { List(size) { List(size) { ThreeGeneric(int, str, double) }.toLinkedHashSet() }.toLinkedHashSet() }.toLinkedHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { List(size) { List(size) { ThreeGeneric(int, Qx2(str), double) }.toLinkedHashSet() }.toLinkedHashSet() }.toLinkedHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { List(size) { List(size) { ThreeGeneric(Qx6(int), Qx4(str), Qx2(double)) }.toLinkedHashSet() }.toLinkedHashSet() }.toLinkedHashSet(),

                    makeList(size,{rdConfig.resetRandomState()}) { List(size) { List(size) { double }.toLinkedHashSet() }.toLinkedHashSet() }.toLinkedHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { List(size) { List(size) { Qx2(float) }.toLinkedHashSet() }.toLinkedHashSet() }.toLinkedHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { List(size) { List(size) { Qx2(Qx4(str)) }.toLinkedHashSet() }.toLinkedHashSet() }.toLinkedHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { List(size) { List(size) { TwoGeneric(int, str) }.toLinkedHashSet() }.toLinkedHashSet() }.toLinkedHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { List(size) { List(size) { TwoGeneric(Qx2(int), str) }.toLinkedHashSet() }.toLinkedHashSet() }.toLinkedHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { List(size) { List(size) { TwoGeneric(Qx2(int), Qx4(str)) }.toLinkedHashSet() }.toLinkedHashSet() }.toLinkedHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { List(size) { List(size) { ThreeGeneric(int, str, double) }.toLinkedHashSet() }.toLinkedHashSet() }.toLinkedHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { List(size) { List(size) { ThreeGeneric(int, Qx2(str), double) }.toLinkedHashSet() }.toLinkedHashSet() }.toLinkedHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { List(size) { List(size) { ThreeGeneric(Qx6(int), Qx4(str), Qx2(double)) }.toLinkedHashSet() }.toLinkedHashSet() }.toLinkedHashSet(),
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
                        putData(random<QxC<LinkedHashSet<LinkedHashSet<LinkedHashSet<Double>>>>>(
                            randomConfig=TestRandomConfig(), 
                            randomizers = {
                                constant{333.222}
                            }
                        ))
                        putData(random<QxC<LinkedHashSet<LinkedHashSet<LinkedHashSet<Double>>>>>(
                            randomConfig=TestRandomConfig(), 
                            randomizers = {
                                constant{linkedSetOf(linkedSetOf(linkedSetOf(1.0),linkedSetOf(2.0)),linkedSetOf(linkedSetOf(3.0),linkedSetOf(4.0)))}
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
                    makeList(size,{rdConfig.resetRandomState()}) { List(size) { List(size) { 333.222 }.toLinkedHashSet() }.toLinkedHashSet() }.toLinkedHashSet(),
                    linkedSetOf(linkedSetOf(linkedSetOf(1.0),linkedSetOf(2.0)),linkedSetOf(linkedSetOf(3.0),linkedSetOf(4.0)))
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
                        putData(random<QxC<LinkedHashSet<LinkedHashSet<Double>>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<LinkedHashSet<LinkedHashSet<Qx2<Float>>>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<LinkedHashSet<LinkedHashSet<Qx2<Qx4<String>>>>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<LinkedHashSet<LinkedHashSet<TwoGeneric<Int,String>>>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<LinkedHashSet<LinkedHashSet<TwoGeneric<Qx2<Int>,String>>>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<LinkedHashSet<LinkedHashSet<TwoGeneric<Qx2<Int>,Qx4<String>>>>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<LinkedHashSet<LinkedHashSet<ThreeGeneric<Int,String,Double>>>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<LinkedHashSet<LinkedHashSet<ThreeGeneric<Int,Qx2<String>,Double>>>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<LinkedHashSet<LinkedHashSet<ThreeGeneric<Qx6<Int>,Qx4<String>,Qx2<Double>>>>>>(randomConfig=TestRandomConfig()))

                        putData(random<QxC<LinkedHashSet<LinkedHashSet<Double>>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<LinkedHashSet<LinkedHashSet<Qx2<Float>>>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<LinkedHashSet<LinkedHashSet<Qx2<Qx4<String>>>>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<LinkedHashSet<LinkedHashSet<TwoGeneric<Int,String>>>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<LinkedHashSet<LinkedHashSet<TwoGeneric<Qx2<Int>,String>>>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<LinkedHashSet<LinkedHashSet<TwoGeneric<Qx2<Int>,Qx4<String>>>>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<LinkedHashSet<LinkedHashSet<ThreeGeneric<Int,String,Double>>>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<LinkedHashSet<LinkedHashSet<ThreeGeneric<Int,Qx2<String>,Double>>>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<LinkedHashSet<LinkedHashSet<ThreeGeneric<Qx6<Int>,Qx4<String>,Qx2<Double>>>>>>(randomConfig=TestRandomConfig()))
                    }
                }
            """,
        ) {
            testCompilation = { result, _ ->
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                val objectList = result.executeRunTestFunction().getObjs()

                objectList shouldBe listOf(
                    makeList(size,{rdConfig.resetRandomState()}) { List(size) { double }.toLinkedHashSet() }.toLinkedHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { List(size) { Qx2(float) }.toLinkedHashSet() }.toLinkedHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { List(size) { Qx2(Qx4(str)) }.toLinkedHashSet() }.toLinkedHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { List(size) { TwoGeneric(int, str) }.toLinkedHashSet() }.toLinkedHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { List(size) { TwoGeneric(Qx2(int), str) }.toLinkedHashSet() }.toLinkedHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { List(size) { TwoGeneric(Qx2(int), Qx4(str)) }.toLinkedHashSet() }.toLinkedHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { List(size) { ThreeGeneric(int, str, double) }.toLinkedHashSet() }.toLinkedHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { List(size) { ThreeGeneric(int, Qx2(str), double) }.toLinkedHashSet() }.toLinkedHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { List(size) { ThreeGeneric(Qx6(int), Qx4(str), Qx2(double)) }.toLinkedHashSet() }.toLinkedHashSet(),

                    makeList(size,{rdConfig.resetRandomState()}) { List(size) { double }.toLinkedHashSet() }.toLinkedHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { List(size) { Qx2(float) }.toLinkedHashSet() }.toLinkedHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { List(size) { Qx2(Qx4(str)) }.toLinkedHashSet() }.toLinkedHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { List(size) { TwoGeneric(int, str) }.toLinkedHashSet() }.toLinkedHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { List(size) { TwoGeneric(Qx2(int), str) }.toLinkedHashSet() }.toLinkedHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { List(size) { TwoGeneric(Qx2(int), Qx4(str)) }.toLinkedHashSet() }.toLinkedHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { List(size) { ThreeGeneric(int, str, double) }.toLinkedHashSet() }.toLinkedHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { List(size) { ThreeGeneric(int, Qx2(str), double) }.toLinkedHashSet() }.toLinkedHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { List(size) { ThreeGeneric(Qx6(int), Qx4(str), Qx2(double)) }.toLinkedHashSet() }.toLinkedHashSet(),
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
                        putData(random<QxC<LinkedHashSet<LinkedHashSet<Double>>>>(
                            randomConfig=TestRandomConfig(), 
                            randomizers = {
                                constant{1.0}
                            }
                        ))
                        putData(random<QxC<LinkedHashSet<LinkedHashSet<Double>>>>(
                            randomConfig=TestRandomConfig(), 
                            randomizers = {
                                constant{linkedSetOf(linkedSetOf(1.0),linkedSetOf(2.0))}
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
                    makeList(size,{rdConfig.resetRandomState()}) { List(size) { 1.0 }.toLinkedHashSet() }.toLinkedHashSet(),
                    linkedSetOf(linkedSetOf(1.0),linkedSetOf(2.0)),
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
                        putData(random<QxC<LinkedHashSet<Double>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<LinkedHashSet<Qx2<Float>>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<LinkedHashSet<Qx2<Qx4<String>>>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<LinkedHashSet<TwoGeneric<Int, String>>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<LinkedHashSet<TwoGeneric<Qx2<Int>, String>>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<LinkedHashSet<TwoGeneric<Qx2<Int>, Qx4<String>>>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<LinkedHashSet<ThreeGeneric<Int, String, Double>>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<LinkedHashSet<ThreeGeneric<Int, Qx2<String>, Double>>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<LinkedHashSet<ThreeGeneric<Qx6<Int>, Qx4<String>, Qx2<Double>>>>>(randomConfig=TestRandomConfig()))
                        
                        putData(random<QxC<LinkedHashSet<Double>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<LinkedHashSet<Qx2<Float>>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<LinkedHashSet<Qx2<Qx4<String>>>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<LinkedHashSet<TwoGeneric<Int, String>>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<LinkedHashSet<TwoGeneric<Qx2<Int>, String>>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<LinkedHashSet<TwoGeneric<Qx2<Int>, Qx4<String>>>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<LinkedHashSet<ThreeGeneric<Int, String, Double>>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<LinkedHashSet<ThreeGeneric<Int, Qx2<String>, Double>>>>(randomConfig=TestRandomConfig()))
                        putData(random<QxC<LinkedHashSet<ThreeGeneric<Qx6<Int>, Qx4<String>, Qx2<Double>>>>>(randomConfig=TestRandomConfig()))
                    }
                }
            """,
        ) {
            testCompilation = { result, _ ->
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                val objectList = result.executeRunTestFunction().getObjs()

                objectList shouldBe listOf(
                    makeList(size,{rdConfig.resetRandomState()}) { double }.toLinkedHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { Qx2(float) }.toLinkedHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { Qx2(Qx4(str)) }.toLinkedHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { TwoGeneric(int, str) }.toLinkedHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { TwoGeneric(Qx2(int), str) }.toLinkedHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { TwoGeneric(Qx2(int), Qx4(str)) }.toLinkedHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { ThreeGeneric(int, str, double) }.toLinkedHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { ThreeGeneric(int, Qx2(str), double) }.toLinkedHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { ThreeGeneric(Qx6(int), Qx4(str), Qx2(double)) }.toLinkedHashSet(),

                    makeList(size,{rdConfig.resetRandomState()}) { double }.toLinkedHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { Qx2(float) }.toLinkedHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { Qx2(Qx4(str)) }.toLinkedHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { TwoGeneric(int, str) }.toLinkedHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { TwoGeneric(Qx2(int), str) }.toLinkedHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { TwoGeneric(Qx2(int), Qx4(str)) }.toLinkedHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { ThreeGeneric(int, str, double) }.toLinkedHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { ThreeGeneric(int, Qx2(str), double) }.toLinkedHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { ThreeGeneric(Qx6(int), Qx4(str), Qx2(double)) }.toLinkedHashSet(),

                    )
            }
        }
    }


    @Test
    fun `set in value param - 3 nested layers`() {
        testGeneratedCodeUsingStandardPlugin(
            """
                $imports

                
                data class QxC<T1>(override val data:LinkedHashSet<LinkedHashSet<LinkedHashSet<T1>>>):WithData

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
                    makeList(size,{rdConfig.resetRandomState()}) { List(size) { List(size) { double }.toLinkedHashSet() }.toLinkedHashSet() }.toLinkedHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { List(size) { List(size) { Qx2(float) }.toLinkedHashSet() }.toLinkedHashSet() }.toLinkedHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { List(size) { List(size) { Qx2(Qx4(str)) }.toLinkedHashSet() }.toLinkedHashSet() }.toLinkedHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { List(size) { List(size) { TwoGeneric(int, str) }.toLinkedHashSet() }.toLinkedHashSet() }.toLinkedHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { List(size) { List(size) { TwoGeneric(Qx2(int), str) }.toLinkedHashSet() }.toLinkedHashSet() }.toLinkedHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { List(size) { List(size) { TwoGeneric(Qx2(int), Qx4(str)) }.toLinkedHashSet() }.toLinkedHashSet() }.toLinkedHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { List(size) { List(size) { ThreeGeneric(int, str, double) }.toLinkedHashSet() }.toLinkedHashSet() }.toLinkedHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { List(size) { List(size) { ThreeGeneric(int, Qx2(str), double) }.toLinkedHashSet() }.toLinkedHashSet() }.toLinkedHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { List(size) { List(size) { ThreeGeneric(Qx6(int), Qx4(str), Qx2(double)) }.toLinkedHashSet() }.toLinkedHashSet() }.toLinkedHashSet(),

                    makeList(size,{rdConfig.resetRandomState()}) { List(size) { List(size) { double }.toLinkedHashSet() }.toLinkedHashSet() }.toLinkedHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { List(size) { List(size) { Qx2(float) }.toLinkedHashSet() }.toLinkedHashSet() }.toLinkedHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { List(size) { List(size) { Qx2(Qx4(str)) }.toLinkedHashSet() }.toLinkedHashSet() }.toLinkedHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { List(size) { List(size) { TwoGeneric(int, str) }.toLinkedHashSet() }.toLinkedHashSet() }.toLinkedHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { List(size) { List(size) { TwoGeneric(Qx2(int), str) }.toLinkedHashSet() }.toLinkedHashSet() }.toLinkedHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { List(size) { List(size) { TwoGeneric(Qx2(int), Qx4(str)) }.toLinkedHashSet() }.toLinkedHashSet() }.toLinkedHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { List(size) { List(size) { ThreeGeneric(int, str, double) }.toLinkedHashSet() }.toLinkedHashSet() }.toLinkedHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { List(size) { List(size) { ThreeGeneric(int, Qx2(str), double) }.toLinkedHashSet() }.toLinkedHashSet() }.toLinkedHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { List(size) { List(size) { ThreeGeneric(Qx6(int), Qx4(str), Qx2(double)) }.toLinkedHashSet() }.toLinkedHashSet() }.toLinkedHashSet(),
                )
            }
        }
    }

    @Test
    fun `set in value param - 3 nested layer - with custom randomizer`() {
        testGeneratedCodeUsingStandardPlugin(
            """
                $imports

                
                data class QxC<T1>(override val data:LinkedHashSet<LinkedHashSet<LinkedHashSet<T1>>>):WithData

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
                                constant(linkedSetOf(linkedSetOf(linkedSetOf(333.1111))))
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
                    makeList(size,{rdConfig.resetRandomState()}) { List(size) { List(size) { 123.222 }.toLinkedHashSet() }.toLinkedHashSet() }.toLinkedHashSet(),
                    linkedSetOf(linkedSetOf(linkedSetOf(333.1111))),
                )
            }
        }
    }

    @Test
    fun `set in value param - 2 nested layers`() {
        testGeneratedCodeUsingStandardPlugin(
            """
                $imports

                
                data class QxC<T1>(override val data:LinkedHashSet<LinkedHashSet<T1>>):WithData

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
                    makeList(size,{rdConfig.resetRandomState()}) { List(size) { double }.toLinkedHashSet() }.toLinkedHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { List(size) { Qx2(float) }.toLinkedHashSet() }.toLinkedHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { List(size) { Qx2(Qx4(str)) }.toLinkedHashSet() }.toLinkedHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { List(size) { TwoGeneric(int, str) }.toLinkedHashSet() }.toLinkedHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { List(size) { TwoGeneric(Qx2(int), str) }.toLinkedHashSet() }.toLinkedHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { List(size) { TwoGeneric(Qx2(int), Qx4(str)) }.toLinkedHashSet() }.toLinkedHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { List(size) { ThreeGeneric(int, str, double) }.toLinkedHashSet() }.toLinkedHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { List(size) { ThreeGeneric(int, Qx2(str), double) }.toLinkedHashSet() }.toLinkedHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { List(size) { ThreeGeneric(Qx6(int), Qx4(str), Qx2(double)) }.toLinkedHashSet() }.toLinkedHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { List(size) { double }.toLinkedHashSet() }.toLinkedHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { List(size) { Qx2(float) }.toLinkedHashSet() }.toLinkedHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { List(size) { Qx2(Qx4(str)) }.toLinkedHashSet() }.toLinkedHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { List(size) { TwoGeneric(int, str) }.toLinkedHashSet() }.toLinkedHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { List(size) { TwoGeneric(Qx2(int), str) }.toLinkedHashSet() }.toLinkedHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { List(size) { TwoGeneric(Qx2(int), Qx4(str)) }.toLinkedHashSet() }.toLinkedHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { List(size) { ThreeGeneric(int, str, double) }.toLinkedHashSet() }.toLinkedHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { List(size) { ThreeGeneric(int, Qx2(str), double) }.toLinkedHashSet() }.toLinkedHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { List(size) { ThreeGeneric(Qx6(int), Qx4(str), Qx2(double)) }.toLinkedHashSet() }.toLinkedHashSet(),
                )
            }
        }
    }

    @Test
    fun `set in value param - 2 nested layers - with custom randomizer`() {
        testGeneratedCodeUsingStandardPlugin(
            """
                $imports

                
                data class QxC<T1>(override val data:LinkedHashSet<LinkedHashSet<T1>>):WithData

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
                                constant(linkedSetOf(linkedSetOf(1.0),linkedSetOf(2.0,3.0)))
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
                    makeList(size,{rdConfig.resetRandomState()}) { List(size) { 3.11 }.toLinkedHashSet() }.toLinkedHashSet(),
                    linkedSetOf(linkedSetOf(1.0),linkedSetOf(2.0,3.0)),
                )
            }
        }
    }

    /**
     * something like this: random<Int>>(randomConfig=TestRandomConfig()) ~> param:List<Int>
     */
    @Test
    fun `set in value param`() {
        testGeneratedCodeUsingStandardPlugin(
            """
                $imports

                
                data class QxC<T1:Any>(override val data:LinkedHashSet<T1>):WithData

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
                    makeList(size,{rdConfig.resetRandomState()}) { int }.toLinkedHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { Qx2(float) }.toLinkedHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { Qx2(Qx4(str)) }.toLinkedHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { TwoGeneric(int, str) }.toLinkedHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { TwoGeneric(Qx2(int), str) }.toLinkedHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { TwoGeneric(Qx2(int), Qx4(str)) }.toLinkedHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { ThreeGeneric(int, str, double) }.toLinkedHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { ThreeGeneric(int, Qx2(str), double) }.toLinkedHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { ThreeGeneric(Qx6(int), Qx4(str), Qx2(double)) }.toLinkedHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { int }.toLinkedHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { Qx2(float) }.toLinkedHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { Qx2(Qx4(str)) }.toLinkedHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { TwoGeneric(int, str) }.toLinkedHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { TwoGeneric(Qx2(int), str) }.toLinkedHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { TwoGeneric(Qx2(int), Qx4(str)) }.toLinkedHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { ThreeGeneric(int, str, double) }.toLinkedHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { ThreeGeneric(int, Qx2(str), double) }.toLinkedHashSet(),
                    makeList(size,{rdConfig.resetRandomState()}) { ThreeGeneric(Qx6(int), Qx4(str), Qx2(double)) }.toLinkedHashSet(),
                )
            }
        }
    }

    @Test
    fun `set in value param - with custom randomizer`() {
        testGeneratedCodeUsingStandardPlugin(
            """
                $imports

                
                data class QxC<T1:Any>(override val data:LinkedHashSet<T1>):WithData

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
                                constant<LinkedHashSet<Int>>{LinkedHashSet(setOf(1,2,3))}
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
                    makeList(size,{rdConfig.resetRandomState()}) { 3 }.toLinkedHashSet(),
                    linkedSetOf(1,2,3),
                    makeList(size,{rdConfig.resetRandomState()}) { Qx2(12f) }.toLinkedHashSet(),
                )
            }
        }
    }


    private fun <T>  Iterable<T>.toLinkedHashSet(): LinkedHashSet<T> {
        return LinkedHashSet(this.toSet())
    }
}
