package com.x12q.randomizer.ir_plugin

import com.tschuchort.compiletesting.KotlinCompilation
import com.x12q.randomizer.ir_plugin.mock_objects.TestRandomConfig
import com.x12q.randomizer.lib.RandomContext
import com.x12q.randomizer.lib.RandomContextBuilderImp
import com.x12q.randomizer.lib.randomizer.factoryRandomizer
import com.x12q.randomizer.test.util.assertions.executeRunTestFunction
import com.x12q.randomizer.test.util.test_code.TestImportsBuilder
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import kotlin.test.BeforeTest
import kotlin.test.Test


@OptIn(ExperimentalCompilerApi::class)
class TestTypePassing {

    data class Qx<T1>(val i: T1?)
    data class Qx2<Q2T>(val paramOfQ2: Q2T)
    data class Qx4<Q4T>(val paramOfQ4: Q4T)
    data class Qx6<H>(val paramOfQ6: H)
    data class Qx3<T1, T2, T3>(val i1: T1, val i2: T2, val i3: T3)
    data class TwoGeneric<G1, G2>(val g1: G1, val g2: G2)
    data class ThreeGeneric<G1, G2, G3>(val g1: G1, val g2: G2, val g3: G3)
    data class QxArray<TL>(val listT: Array<TL>)
    data class HI(val i: Int)

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

    // ###

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

    // ###

    private val imports = TestImportsBuilder.stdImport
        .import(Cx::class)
        .import(Cx2::class)
        .import(Ax::class)
        .import(Ax2::class)
        .import(Bx::class)
        .import(Bx2::class)
        .import(Qx::class)
        .import(Qx2::class)
        .import(Qx3::class)
        .import(Qx4::class)
        .import(Qx6::class)
        .import(TwoGeneric::class)
        .import(ThreeGeneric::class)
        .import(QxArray::class)
        .import(HI::class)

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

    /**
     * Test passing generic param from "random" function to generic with a property.
     * The generic type is further passed down to deeper layer within the class of that property.
     */
    @Test
    fun `test passing generic to property`() {
        testGeneratedCodeUsingStandardPlugin(
            """
                $imports

                @Randomizable(randomConfig = TestRandomConfig::class)
                data class QxC<K_Q>(override val data:Ax<K_Q>):WithData

                fun runTest():TestOutput {
                    return withTestOutput {
                        putData(QxC.random<Double>())
                    }
                }
            """,
        ) {
            testCompilation = { result, _ ->
                1 shouldBe 2
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

    /**
     * Similar to [`test passing generic to property`], but with slightly different class structure.
     * Test passing generic param from "random" function to generic with a property.
     * The generic type is further passed down to deeper layer within the class of that property.
     */
    @Test
    fun `test passing generic to property 2`() {
        testGeneratedCodeUsingStandardPlugin(
            """
                $imports

                @Randomizable(randomConfig = TestRandomConfig::class)
                data class QxC2<K_Q2>(override val data:Ax2<K_Q2>):WithData

                fun runTest():TestOutput {
                    return withTestOutput {
                        putData(QxC2.random<Double>())
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
}
