package com.x12q.kotlin.randomizer.ir_plugin

import com.tschuchort.compiletesting.KotlinCompilation
import com.x12q.kotlin.randomizer.lib.annotations.Randomizable
import com.x12q.kotlin.randomizer.test.util.WithData
import com.x12q.kotlin.randomizer.test.util.assertions.executeRunTestFunction
import com.x12q.kotlin.randomizer.test.util.test_code.TestImportsBuilder
import io.kotest.matchers.shouldBe
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import kotlin.test.Test


@OptIn(ExperimentalCompilerApi::class)
class TestRandomizingInterface {

    @Randomizable(
        candidates = [PlainImplementation_2::class],
    )
    interface PlainInterface {
        val i:Int
        val d:Double
    }

    class PlainImplementation_1(override val i: Int, override val d: Double) : PlainInterface

    object PlainImplementation_2: PlainInterface{
        override val d: Double = 222.0
        override val i = 100
    }

    interface GenericInterface<T1,T2>{
        val t1:T1
        val t2:T2
    }

    class GenericImplementation_1<G0>(override val t1: Int, override val t2: G0) : GenericInterface<Int,G0>{

    }

    private val imports = TestImportsBuilder.stdImport
        .import(QxC::class)
        .import(PlainInterface::class)
        .import(GenericInterface::class)

    data class QxC<K_Q:Any>(override val data:K_Q):WithData

    /**
     * Test passing generic param from "random" function to generic with a property.
     * The generic type is further passed down to deeper layer within the class of that property.
     */
    @Test
    fun `generate random plain interface`() {
        testGeneratedCodeUsingStandardPlugin(
            """
                $imports

                fun runTest():TestOutput {
                    return withTestOutput {
                        putData(random<QxC<PlainInterface>>(randomConfig=TestRandomConfig()))
                    }
                }
            """,
        ) {
            testCompilation = { result, _ ->
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                val objectList = result.executeRunTestFunction().getObjs()
                val ax = objectList.first()
                ax shouldBe PlainImplementation_2
            }
        }
    }

}
