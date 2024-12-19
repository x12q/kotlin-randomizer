package com.x12q.randomizer.ir_plugin

import com.tschuchort.compiletesting.KotlinCompilation
import com.x12q.randomizer.ir_plugin.mock_objects.TestRandomConfig
import com.x12q.randomizer.test.util.WithData
import com.x12q.randomizer.test.util.assertions.executeRunTestFunction
import com.x12q.randomizer.test.util.test_code.TestImportsBuilder
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import kotlin.test.Test


@OptIn(ExperimentalCompilerApi::class)
class TestTypePassing {

    data class Qx<T1>(val i: T1?)
    // data class Qx2<Q2T>(val paramOfQ2: Q2T)
    // data class Qx4<Q4T>(val paramOfQ4: Q4T)
    // data class Qx6<H>(val paramOfQ6: H)
    // data class Qx3<T1, T2, T3>(val i1: T1, val i2: T2, val i3: T3)
    // data class TwoGeneric<G1, G2>(val g1: G1, val g2: G2)
    // data class ThreeGeneric<G1, G2, G3>(val g1: G1, val g2: G2, val g3: G3)
    // data class QxArray<TL>(val listT: Array<TL>)
    // data class HI(val i: Int)

    // ###

    data class Ax<A_K>(
        val bx: Bx<Float, A_K>,
        val ak: A_K,
    )

    data class Bx<B_V, B_M>(
        val cx: Cx<B_M, Int>,
        val q: B_V
    )

    data class Cx<C_T, C_M>(
        val ct: C_T,
        val cm: C_M
    )

    private val imports = TestImportsBuilder.stdImport
        .import(Cx::class)
        .import(Ax::class)
        .import(Bx::class)
        .import(Qx::class)
        .import(QxC::class)

    data class QxC<K_Q>(override val data:Ax<K_Q>):WithData

    /**
     * Test passing generic param from "random" function to generic with a property.
     * The generic type is further passed down to deeper layer within the class of that property.
     */
    @Test
    fun `test passing generic to property`() {
        testGeneratedCodeUsingStandardPlugin(
            """
                $imports

                fun runTest():TestOutput {
                    return withTestOutput {
                        putData(random<QxC<Double>>(randomConfig=TestRandomConfig()))
                    }
                }
            """,
        ) {
            testCompilation = { result, _ ->
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                val objectList = result.executeRunTestFunction().getObjs()
                val ax = objectList.first()
                ax.shouldBeInstanceOf<Ax<Double>>()
                ax.ak.shouldBeInstanceOf<Double>()

                val bx = ax.bx
                bx.shouldBeInstanceOf<Bx<Float,Double>>()
                bx.q.shouldBeInstanceOf<Float>()

                val cx = bx.cx
                cx.shouldBeInstanceOf<Cx<Double,Int>>()
                cx.ct.shouldBeInstanceOf<Double>()
                cx.cm.shouldBeInstanceOf<Int>()
            }
        }
    }

    data class Ax2<A2_K>(
        val bx2: Bx2<Float, A2_K>,
        val ak: A2_K,
    )

    data class Bx2<B2_V, B2_M>(
        val cx2: Cx2<B2_V>,
        val m: B2_M
    )

    data class Cx2<C2_T>(
        val ct: C2_T,
    )

    val imports2 = imports
        .import(Cx2::class)
        .import(Ax2::class)
        .import(Bx2::class)
        .import(QxC2::class)

    data class QxC2<K_Q2>(override val data:Ax2<K_Q2>):WithData
    /**
     * Similar to [`test passing generic to property`], but with slightly different class structure.
     * Test passing generic param from "random" function to generic with a property.
     * The generic type is further passed down to deeper layer within the class of that property.
     */
    @Test
    fun `test passing generic to property 2`() {
        testGeneratedCodeUsingStandardPlugin(
            """
                $imports2
                fun runTest():TestOutput {
                    return withTestOutput {
                        putData(random<QxC2<Double>>())
                    }
                }
            """,
        ) {
            testCompilation = { result, _ ->
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                val objectList = result.executeRunTestFunction().getObjs()

                val ax = objectList.first()
                ax.shouldBeInstanceOf<Ax2<Double>>()
                ax.ak.shouldBeInstanceOf<Double>()

                val bx2 = ax.bx2
                bx2.shouldBeInstanceOf<Bx2<Float,Double>>()
                bx2.m.shouldBeInstanceOf<Double>()

                val cx2 = bx2.cx2
                cx2.shouldBeInstanceOf<Cx2<Float>>()
                cx2.ct.shouldBeInstanceOf<Float>()
            }
        }
    }


    data class Ax3<A3_T>(
        val l:List<A3_T>,
        val bx3:Bx3<Int,A3_T>,
    )

    data class Bx3<B3_K,B3_v>(
        val bx3Map:Map<B3_K,B3_v>,
        val cx3: Cx3<B3_v>,
    )

    data class Cx3<C3_T>(val set:Set<C3_T>)


    val imports3 = imports2
        .import(Ax3::class)
        .import(Bx3::class)
        .import(Cx3::class)
        .import(QxC3::class)

    data class QxC3<K_Q3>(override val data:Ax3<K_Q3>):WithData

    @Test
    fun `test passing generic to collections nested in properties`() {
        testGeneratedCodeUsingStandardPlugin(
            """
                $imports3

                fun runTest():TestOutput {
                    return withTestOutput {
                        putData(random<QxC3<Double>>())
                    }
                }
            """,
        ) {
            testCompilation = { result, _ ->
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                val objectList = result.executeRunTestFunction().getObjs()

                val ax = objectList.first()
                ax.shouldBeInstanceOf<Ax3<Double>>()
                ax.l.shouldBeInstanceOf<List<Double>>()

                val bx3 = ax.bx3
                bx3.shouldBeInstanceOf<Bx3<Int,Double>>()
                bx3.bx3Map.shouldBeInstanceOf<Map<Int,Double>>()

                val cx2 = bx3.cx3
                cx2.shouldBeInstanceOf<Cx3<Double>>()
                cx2.set.shouldBeInstanceOf<Set<Double>>()
            }
        }
    }


    @Test
    fun `test passing generic to collections nested in properties - with complex elements`() {
        testGeneratedCodeUsingStandardPlugin(
            """
                $imports3

                fun runTest():TestOutput {
                    return withTestOutput {
                        putData(random<QxC3<Cx3<String>>>(randomConfig=TestRandomConfig()))
                    }
                }
            """,
        ) {
            testCompilation = { result, _ ->
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                val objectList = result.executeRunTestFunction().getObjs()

                val ax = objectList.first()
                ax.shouldBeInstanceOf<Ax3<Cx3<String>>>()
                ax.l.shouldBeInstanceOf<List<Cx3<String>>>()

                val bx3 = ax.bx3
                bx3.shouldBeInstanceOf<Bx3<Int,Cx3<String>>>()
                bx3.bx3Map.shouldBeInstanceOf<Map<Int,Cx3<String>>>()

                val cx2 = bx3.cx3
                cx2.shouldBeInstanceOf<Cx3<Cx3<String>>>()
                cx2.set.shouldBeInstanceOf<Set<Cx3<String>>>()
            }
        }
    }
}
