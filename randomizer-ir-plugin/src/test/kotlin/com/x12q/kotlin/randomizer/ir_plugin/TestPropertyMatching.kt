package com.x12q.kotlin.randomizer.ir_plugin

import com.tschuchort.compiletesting.KotlinCompilation
import com.x12q.kotlin.randomizer.ir_plugin.mock_objects.RandomConfigForTest
import com.x12q.kotlin.randomizer.ir_plugin.mock_objects.TestRandomConfig
import com.x12q.kotlin.randomizer.ir_plugin.mock_objects.TestRandomConfigWithRandomizableCandidateIndex
import com.x12q.kotlin.randomizer.lib.randomizer.ClassRandomizer
import com.x12q.kotlin.randomizer.test_utils.WithData
import com.x12q.kotlin.randomizer.test_utils.assertions.executeRunTestFunction
import com.x12q.kotlin.randomizer.test_utils.test_code.TestImportsBuilder
import io.kotest.matchers.shouldBe
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import kotlin.reflect.KProperty1
import kotlin.reflect.KProperty2
import kotlin.test.*

class TestPropertyMatching {

    class ABC(
        val numberInt1: Int,
        val numberInt2: Int,
    )

    data class QxC<K_Q : Any>(override val data: K_Q) : WithData

    private val imports = TestImportsBuilder.stdImport
        .import(ABC::class)
        .import(QxC::class)

    @Test
    fun qwe(){

    }


    @OptIn(ExperimentalCompilerApi::class)
    @Test
    fun testMatchingProperty(){
        testGeneratedCodeUsingStandardPlugin(
            """
                $imports

                fun runTest():TestOutput {
                    return withTestOutput {
                        putData(random<QxC<ABC>>(randomConfig=TestRandomConfig(), randomizers = {
                           factory(ABC::numberInt1){
                               123
                           }
                        }))
                        putData(random<QxC<ABC>>(randomConfig=TestRandomConfig(), randomizers = {
                            factory(ABC::numberInt2){
                                -345
                            }
                        }))
                    }
                }
            """,
        ) {
            testCompilation = { result, _ ->
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK

                val objectList = result.executeRunTestFunction().getObjs()
                objectList shouldBe listOf(
                    ABC(123, TestRandomConfig().nextInt()),
                    ABC(TestRandomConfig().nextInt(), -345),
                )
            }
        }
    }
}
