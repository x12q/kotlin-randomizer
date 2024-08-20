package com.x12q.randomizer.ir_plugin

import com.tschuchort.compiletesting.KotlinCompilation
import com.x12q.randomizer.ir_plugin.mock_objects.LegalRandomConfigObject
import com.x12q.randomizer.test.util.assertions.runRunTest
import com.x12q.randomizer.test.util.test_code.TestImportsBuilder
import io.kotest.matchers.shouldBe
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import kotlin.test.Ignore
import kotlin.test.Test

@OptIn(ExperimentalCompilerApi::class)
class TestRandomCollection {

    data class Qx2(val intList: List<Int>)

    @Test
    @Ignore
    fun `concrete list`() {

        testGeneratedCodeUsingStandardPlugin(
            """
                ${TestImportsBuilder.stdImport.import(Qx2::class)}

                fun runTest():TestOutput{
                    return withTestOutput{
//                        putData(QxC.random(randomizers = {
//                            add(ConstantClassRandomizer(listOf(1,2,3),List::class))
//                        }))
                    }
                }
                @Randomizable(randomConfig = LegalRandomConfigObject::class)
                data class QxC(override val data:Qx2):WithData
            """,
        ) {
            testCompilation = { result, _ ->
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                result.runRunTest().getObjs()
            }
        }
    }
}
