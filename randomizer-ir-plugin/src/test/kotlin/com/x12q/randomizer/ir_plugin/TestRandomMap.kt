package com.x12q.randomizer.ir_plugin

import com.tschuchort.compiletesting.KotlinCompilation
import com.x12q.randomizer.ir_plugin.mock_objects.TestRandomConfig
import com.x12q.randomizer.test.util.assertions.runRunTest
import com.x12q.randomizer.test.util.test_code.TestImportsBuilder
import io.kotest.matchers.shouldBe
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
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

    private val rdc = TestRandomConfig()
    fun nextSize(): Int = rdc.randomCollectionSize()
    val mapSize = nextSize()
    fun nextInt(): Int = rdc.nextInt()
    fun nextFloat(): Float = rdc.nextFloat()
    fun nextStr(): String = rdc.nextString()
    fun nextDouble(): Double = rdc.nextDouble()
    fun nextShort(): Short = rdc.nextShort()

    @Test
    fun `map param`() {
        testGeneratedCodeUsingStandardPlugin(
            """
                $imports

                @Randomizable(randomConfig = LegalRandomConfigObject::class)
                data class QxC<K,V>(override val data:Map<K,V>):WithData

                fun runTest():TestOutput {
                    return withTestOutput{
                        putData(QxC.random<Int,Double>())
                        // putData(QxC.random<Qx2<Float>>())
                        // putData(QxC.random<Qx2<Qx4<String>>>())
                        // putData(QxC.random<TwoGeneric<Int,String>>())
                        // putData(QxC.random<TwoGeneric<Qx2<Int>,String>>())
                        // putData(QxC.random<TwoGeneric<Qx2<Int>,Qx4<String>>>())
                        // putData(QxC.random<ThreeGeneric<Int,String,Double>>())
                        // putData(QxC.random<ThreeGeneric<Int,Qx2<String>,Double>>())
                        // putData(QxC.random<ThreeGeneric<Qx6<Int>,Qx4<String>,Qx2<Double>>>())
                    }
                }
            """,
        ) {
            testCompilation = { result, _ ->
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                val objectList = result.runRunTest().getObjs()
                objectList shouldBe listOf(
                    buildMap {
                        for (x in 1..mapSize) {
                            put(nextInt(), nextDouble())
                        }
                        rdc.reset()
                    },
                )
            }
        }
    }
}
