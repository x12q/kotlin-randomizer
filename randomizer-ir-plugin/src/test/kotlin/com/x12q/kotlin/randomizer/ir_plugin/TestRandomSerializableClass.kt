package com.x12q.kotlin.randomizer.ir_plugin

import com.tschuchort.compiletesting.KotlinCompilation
import com.x12q.kotlin.randomizer.ir_plugin.mock_objects.AlwaysTrueRandomConfig
import com.x12q.kotlin.randomizer.ir_plugin.mock_objects.LegalRandomConfigObject
import com.x12q.kotlin.randomizer.test_utils.WithData
import com.x12q.kotlin.randomizer.test_utils.assertions.executeRunTestFunction
import com.x12q.kotlin.randomizer.test_utils.test_code.TestImportsBuilder
import io.kotest.matchers.shouldBe
import kotlinx.serialization.Serializable
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import kotlin.test.Test


@OptIn(ExperimentalCompilerApi::class)
class TestRandomSerializableClass {


    @Serializable
    data class ABC(val str: String, val i: Int)

    data class Q<T : Any>(
        override val data: T
    ) : WithData

    val imports = TestImportsBuilder.stdImport
        .import(Q::class)
        .import(ABC::class)


    @Test
    fun `randomize serializable z`() {
        testGeneratedCodeUsingStandardPlugin(
            """
                $imports

                fun runTest():TestOutput{
                    return withTestOutput{
                        val v1 = random<ABC>(randomConfig=AlwaysFalseRandomConfig)
                        putData(Q(v1))
                    }
                }
            """,
            fileName = "main.kt"
        ) {
            testCompilation = { result, _ ->
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                result.executeRunTestFunction { output ->
                    output.getObjs().toSet() shouldBe listOf(
                        ABC(
                            str = AlwaysTrueRandomConfig.nextString(),
                            i = AlwaysTrueRandomConfig.nextInt()
                        )
                    )
                }
            }
        }
    }
}
