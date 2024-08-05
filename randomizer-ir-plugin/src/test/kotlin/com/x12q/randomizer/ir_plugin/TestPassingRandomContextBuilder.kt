package com.x12q.randomizer.ir_plugin

import com.tschuchort.compiletesting.KotlinCompilation
import com.x12q.randomizer.lib.FactoryClassRandomizer
import com.x12q.randomizer.lib.RandomizerCollectionImp
import com.x12q.randomizer.test.util.assertions.runRunTest
import com.x12q.randomizer.test.util.test_code.TestImportsBuilder
import io.kotest.matchers.shouldBe
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import kotlin.test.Test


@OptIn(ExperimentalCompilerApi::class)
class TestPassingRandomContextBuilder {

    data class Dt(val i:Int)

    @Test
    fun `pass randomizers config function`() {

        val imports = TestImportsBuilder.stdImport.import(Dt::class)
        testGeneratedCodeUsingStandardPlugin(
            """
               $imports

                fun runTest():TestOutput{
                    return withTestOutput{
                        putData(QxC.random{
                            add(${imports.nameOf(FactoryClassRandomizer::class)}<Dt>({Dt(-999)},Dt::class))
                        })
                    }
                }
                @Randomizable
                data class QxC(override val data:Dt):WithData{
                    companion object{
                        fun m2():M2{
                            val collection = ${imports.nameOf(RandomizerCollectionImp::class)}(emptyMap())                           
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
                result.runRunTest{
                    it.getObjs() shouldBe listOf(
                        Dt(-999)
                    )
                }
            }
        }
    }

    @Test
    fun `random context overriding default randomizer`() {

        val imports = TestImportsBuilder.stdImport.import(Dt::class)
        testGeneratedCodeUsingStandardPlugin(
            """
               $imports

                fun runTest():TestOutput{
                    return withTestOutput{
                        putData(QxC.random{
                            add(${imports.nameOf(FactoryClassRandomizer::class)}<Int>({222},Int::class))
                        })
                    }
                }
                @Randomizable
                data class QxC(override val data:Dt):WithData
            """,
        ) {
            testCompilation = { result, testStream ->
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                result.runRunTest{
                    it.getObjs() shouldBe listOf(
                        Dt(222)
                    )
                }
            }
        }
    }

}
