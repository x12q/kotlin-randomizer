package com.x12q.randomizer.ir_plugin

import com.tschuchort.compiletesting.KotlinCompilation
import com.x12q.randomizer.ir_plugin.mock_objects.AlwaysTrueRandomConfig
import com.x12q.randomizer.lib.randomizer.ConstantClassRandomizer
import com.x12q.randomizer.lib.randomizer.FactoryClassRandomizer
import com.x12q.randomizer.test.util.WithData
import com.x12q.randomizer.test.util.assertions.runRunTest
import com.x12q.randomizer.test.util.test_code.ImportData
import io.kotest.matchers.shouldBe
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.util.dump
import kotlin.test.Test


@OptIn(ExperimentalCompilerApi::class)
class TestPassingRandomizerBuilder {

    data class Dt(val i:Int)

    @Test
    fun `pass randomizers config function`() {


        testGeneratedCodeUsingStandardPlugin(
            """
               ${ImportData.stdImport.import(Dt::class)}
                
                fun runTest():TestOutput{
                    return withTestOutput{
                        putData(QxC.random{
                            add(FactoryClassRandomizer<Dt>({Dt(-999)},Dt::class))
                        })
                    }
                }
                @Randomizable
                data class QxC(override val data:Dt):WithData{
                    companion object{
                        fun m2():M2{
                            val collection = ClassRandomizerCollectionImp(emptyList())                           
                            return M2(
                                b2 = collection.random<B2>() ?: B2(i = collection.random<Int>() ?: 123),
                            )
                        }
                    }
                }
                data class B2(
                    val i:Int
                )
                
                data class M2(
                    val b2:B2,
                )
            """,
        ) {
            testCompilation = { result, testStream ->
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                val l = result.runRunTest().getObjs()
//                println(l)
                l shouldBe listOf(
                   Dt(-999)
                )
            }
        }
    }
}
