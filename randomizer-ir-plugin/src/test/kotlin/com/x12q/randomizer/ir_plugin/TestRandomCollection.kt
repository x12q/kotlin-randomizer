package com.x12q.randomizer.ir_plugin

import com.tschuchort.compiletesting.KotlinCompilation
import com.x12q.randomizer.ir_plugin.mock_objects.LegalRandomConfigObject
import com.x12q.randomizer.test.util.assertions.runRunTest
import com.x12q.randomizer.test.util.test_code.TestImportsBuilder
import io.kotest.matchers.shouldBe
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import kotlin.test.Test


@OptIn(ExperimentalCompilerApi::class)
class TestRandomCollection {

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

    val size = LegalRandomConfigObject.randomCollectionSize()
    val int = LegalRandomConfigObject.nextInt()
    val float = LegalRandomConfigObject.nextFloat()
    val str = LegalRandomConfigObject.nextStringUUID()
    val double = LegalRandomConfigObject.nextDouble()
    val short = LegalRandomConfigObject.nextShort()

    @Test
    fun `list from type param - 3 nested`() {
        testGeneratedCodeUsingStandardPlugin(
            """
                $imports

                @Randomizable(randomConfig = LegalRandomConfigObject::class)
                data class QxC<T1:Any>(override val data:T1):WithData

                fun runTest():TestOutput {
                    return withTestOutput {
                        putData(QxC.random<List<List<List<Double>>>>())
                        putData(QxC.random<List<List<List<Qx2<Float>>>>>())
                        putData(QxC.random<List<List<List<Qx2<Qx4<String>>>>>>())
                        putData(QxC.random<List<List<List<TwoGeneric<Int, String>>>>>())
                        putData(QxC.random<List<List<List<TwoGeneric<Qx2<Int>, String>>>>>())
                        putData(QxC.random<List<List<List<TwoGeneric<Qx2<Int>, Qx4<String>>>>>>())
                        putData(QxC.random<List<List<List<ThreeGeneric<Int, String, Double>>>>>())
                        putData(QxC.random<List<List<List<ThreeGeneric<Int, Qx2<String>, Double>>>>>())
                        putData(QxC.random<List<List<List<ThreeGeneric<Qx6<Int>, Qx4<String>, Qx2<Double>>>>>>())
                    }
                }
            """,
        ) {
            testCompilation = { result, _ ->
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                val objectList = result.runRunTest().getObjs()

                objectList shouldBe listOf(
                    List(size) { List(size) { List(size) { double } } },
                    List(size) { List(size) { List(size) { Qx2(float) } } },
                    List(size) { List(size) { List(size) { Qx2(Qx4(str)) } } },
                    List(size) { List(size) { List(size) { TwoGeneric(int, str) } } },
                    List(size) { List(size) { List(size) { TwoGeneric(Qx2(int), str) } } },
                    List(size) { List(size) { List(size) { TwoGeneric(Qx2(int), Qx4(str)) } } },
                    List(size) { List(size) { List(size) { ThreeGeneric(int, str, double) } } },
                    List(size) { List(size) { List(size) { ThreeGeneric(int, Qx2(str), double) } } },
                    List(size) { List(size) { List(size) { ThreeGeneric(Qx6(int), Qx4(str), Qx2(double)) } } },
                )
            }
        }
    }


    @Test
    fun `list from type param - 2 nested`() {
        testGeneratedCodeUsingStandardPlugin(
            """
                $imports

                @Randomizable(randomConfig = LegalRandomConfigObject::class)
                data class QxC<T1:Any>(override val data:T1):WithData

                fun runTest():TestOutput {
                    return withTestOutput {
                        putData(QxC.random<List<List<Double>>>())
                        putData(QxC.random<List<List<Qx2<Float>>>>())
                        putData(QxC.random<List<List<Qx2<Qx4<String>>>>>())
                        putData(QxC.random<List<List<TwoGeneric<Int,String>>>>())
                        putData(QxC.random<List<List<TwoGeneric<Qx2<Int>,String>>>>())
                        putData(QxC.random<List<List<TwoGeneric<Qx2<Int>,Qx4<String>>>>>())
                        putData(QxC.random<List<List<ThreeGeneric<Int,String,Double>>>>())
                        putData(QxC.random<List<List<ThreeGeneric<Int,Qx2<String>,Double>>>>())
                        putData(QxC.random<List<List<ThreeGeneric<Qx6<Int>,Qx4<String>,Qx2<Double>>>>>())
                    }
                }
            """,
        ) {
            testCompilation = { result, _ ->
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                val objectList = result.runRunTest().getObjs()

                objectList shouldBe listOf(
                    List(size) { List(size) { double } },
                    List(size) { List(size) { Qx2(float) } },
                    List(size) { List(size) { Qx2(Qx4(str)) } },
                    List(size) { List(size) { TwoGeneric(int, str) } },
                    List(size) { List(size) { TwoGeneric(Qx2(int), str) } },
                    List(size) { List(size) { TwoGeneric(Qx2(int), Qx4(str)) } },
                    List(size) { List(size) { ThreeGeneric(int, str, double) } },
                    List(size) { List(size) { ThreeGeneric(int, Qx2(str), double) } },
                    List(size) { List(size) { ThreeGeneric(Qx6(int), Qx4(str), Qx2(double)) } },
                )
            }
        }
    }


    @Test
    fun `list param with 3 nested list`() {
        testGeneratedCodeUsingStandardPlugin(
            """
                $imports

                @Randomizable(randomConfig = LegalRandomConfigObject::class)
                data class QxC<T1>(override val data:List<List<List<T1>>>):WithData

                fun runTest():TestOutput {
                    return withTestOutput {
                        putData(QxC.random<Double>())
                        putData(QxC.random<Qx2<Float>>())
                        putData(QxC.random<Qx2<Qx4<String>>>())
                        putData(QxC.random<TwoGeneric<Int,String>>())
                        putData(QxC.random<TwoGeneric<Qx2<Int>,String>>())
                        putData(QxC.random<TwoGeneric<Qx2<Int>,Qx4<String>>>())
                        putData(QxC.random<ThreeGeneric<Int,String,Double>>())
                        putData(QxC.random<ThreeGeneric<Int,Qx2<String>,Double>>())
                        putData(QxC.random<ThreeGeneric<Qx6<Int>,Qx4<String>,Qx2<Double>>>())
                    }
                }
            """,
        ) {
            testCompilation = { result, _ ->
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                val objectList = result.runRunTest().getObjs()

                objectList shouldBe listOf(
                    List(size) { List(size) { List(size) { double } } },
                    List(size) { List(size) { List(size) { Qx2(float) } } },
                    List(size) { List(size) { List(size) { Qx2(Qx4(str)) } } },
                    List(size) { List(size) { List(size) { TwoGeneric(int, str) } } },
                    List(size) { List(size) { List(size) { TwoGeneric(Qx2(int), str) } } },
                    List(size) { List(size) { List(size) { TwoGeneric(Qx2(int), Qx4(str)) } } },
                    List(size) { List(size) { List(size) { ThreeGeneric(int, str, double) } } },
                    List(size) { List(size) { List(size) { ThreeGeneric(int, Qx2(str), double) } } },
                    List(size) { List(size) { List(size) { ThreeGeneric(Qx6(int), Qx4(str), Qx2(double)) } } },
                )
            }
        }
    }

    /**
     * something like this: random<Int>() ~> param:List<Int>
     */
    @Test
    fun `list param with 2 nested list`() {
        testGeneratedCodeUsingStandardPlugin(
            """
                $imports

                @Randomizable(randomConfig = LegalRandomConfigObject::class)
                data class QxC<T1>(override val data:List<List<T1>>):WithData

                fun runTest():TestOutput {
                    return withTestOutput {
                        putData(QxC.random<Double>())
                        putData(QxC.random<Qx2<Float>>())
                        putData(QxC.random<Qx2<Qx4<String>>>())
                        putData(QxC.random<TwoGeneric<Int,String>>())
                        putData(QxC.random<TwoGeneric<Qx2<Int>,String>>())
                        putData(QxC.random<TwoGeneric<Qx2<Int>,Qx4<String>>>())
                        putData(QxC.random<ThreeGeneric<Int,String,Double>>())
                        putData(QxC.random<ThreeGeneric<Int,Qx2<String>,Double>>())
                        putData(QxC.random<ThreeGeneric<Qx6<Int>,Qx4<String>,Qx2<Double>>>())
                    }
                }
            """,
        ) {
            testCompilation = { result, _ ->
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                val objectList = result.runRunTest().getObjs()

                objectList shouldBe listOf(
                    List(size) { List(size) { double } },
                    List(size) { List(size) { Qx2(float) } },
                    List(size) { List(size) { Qx2(Qx4(str)) } },
                    List(size) { List(size) { TwoGeneric(int, str) } },
                    List(size) { List(size) { TwoGeneric(Qx2(int), str) } },
                    List(size) { List(size) { TwoGeneric(Qx2(int), Qx4(str)) } },
                    List(size) { List(size) { ThreeGeneric(int, str, double) } },
                    List(size) { List(size) { ThreeGeneric(int, Qx2(str), double) } },
                    List(size) { List(size) { ThreeGeneric(Qx6(int), Qx4(str), Qx2(double)) } },
                )
            }
        }
    }

    /**
     * something like this: random<Int>() ~> param:List<Int>
     */
    @Test
    fun `randomize generic List of primitive with element type provided in type param`() {
        testGeneratedCodeUsingStandardPlugin(
            """
                $imports

                @Randomizable(randomConfig = LegalRandomConfigObject::class)
                data class QxC<T1:Any>(override val data:List<T1>):WithData

                fun runTest():TestOutput {
                    return withTestOutput{
                        putData(QxC.random<Int>())
                        putData(QxC.random<Qx2<Float>>())
                        putData(QxC.random<Qx2<Qx4<String>>>())
                        putData(QxC.random<TwoGeneric<Int,String>>())
                        putData(QxC.random<TwoGeneric<Qx2<Int>,String>>())
                        putData(QxC.random<TwoGeneric<Qx2<Int>,Qx4<String>>>())
                        putData(QxC.random<ThreeGeneric<Int,String,Double>>())
                        putData(QxC.random<ThreeGeneric<Int,Qx2<String>,Double>>())
                        putData(QxC.random<ThreeGeneric<Qx6<Int>,Qx4<String>,Qx2<Double>>>())
                    }
                }
            """,
        ) {
            testCompilation = { result, _ ->
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                val objectList = result.runRunTest().getObjs()

                objectList shouldBe listOf(
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
            }
        }
    }

    /**
     * Something like this: random<List<Int>>() ~> param:T
     */
    @Test
    fun `randomize generic primitive list with the whole list type provided in type param`() {
        testGeneratedCodeUsingStandardPlugin(
            """
                $imports

                @Randomizable(randomConfig = LegalRandomConfigObject::class)
                data class QxC<T1:Any>(override val data:T1):WithData

                @Randomizable(randomConfig = LegalRandomConfigObject::class)
                data class QxC2<E1,E2>(override val data:TwoGeneric<E1,E2>):WithData
                
                fun runTest():TestOutput {
                    return withTestOutput{
                        putData(QxC.random<List<Float>>())
                        putData(QxC.random<List<Qx2<Double>>>())
                        putData(QxC2.random<List<Float>, List<String>>())
                        putData(QxC2.random<List<Qx2<String>>, List<List<Short>>>())
                    }
                }
            """,
        ) {
            testCompilation = { result, _ ->
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                val objectList = result.runRunTest().getObjs()
                objectList shouldBe listOf(
                    List(size) { float },
                    List(size) { Qx2(double) },
                    TwoGeneric(List(size) { float }, List(size) { str }),
                    TwoGeneric(List(size) { Qx2(str) }, List(size) { List(size) { short } })
                )
            }
        }
    }

}
