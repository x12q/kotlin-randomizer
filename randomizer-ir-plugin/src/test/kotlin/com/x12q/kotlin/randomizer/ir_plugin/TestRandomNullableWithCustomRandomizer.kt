package com.x12q.kotlin.randomizer.ir_plugin

import com.tschuchort.compiletesting.KotlinCompilation
import com.x12q.kotlin.randomizer.ir_plugin.mock_objects.LegalRandomConfigObject
import com.x12q.kotlin.randomizer.test_utils.WithData
import com.x12q.kotlin.randomizer.test_utils.assertions.executeRunTestFunction
import com.x12q.kotlin.randomizer.test_utils.test_code.TestImportsBuilder
import io.kotest.matchers.shouldBe
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import kotlin.test.Test


@OptIn(ExperimentalCompilerApi::class)
class TestRandomNullableWithCustomRandomizer {


    data class DtContainer1(
        val boolean: Boolean,
        val int: Int?,
        val long: Long?,
        // val float: Float,
        // val double: Double,
        // val byte: Byte,
        // val char: Char,
        // val short: Short,
        // val string: String,
        // val number: Number,
        // val unit: Unit,
        // val any: Any,
    )


    data class DtContainer2(
        val d: DtContainer1?
    )

    data class Q<T:Any>(
        override val data:T
    ):WithData

    val imports = TestImportsBuilder.stdImport
        .import(Q::class)
        .import(DtContainer1::class)
        .import(UInt::class)

    @Test
    fun `randomize nullable primitive with custom randomizer`() {
        testGeneratedCodeUsingStandardPlugin(
            """
                $imports

                fun runTest():TestOutput{
                    return withTestOutput{
                        val v= random<DtContainer1>(randomConfig=LegalRandomConfigObject, randomizers = {
                            constant<Int?>(123)
                            constant<Long?>(null)
                        })
                        putData(Q(v))
                    }
                }
            """
            ,
            fileName = "main.kt"
        ) {
            testCompilation = { result, _ ->
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                result.executeRunTestFunction { output ->
                    output.getObjs() shouldBe listOf(
                        DtContainer1(
                            boolean = LegalRandomConfigObject.nextBoolean(),
                            int = 123,
                            long =  null
                        )
                    )
                }
            }
        }
    }

    @Test
    fun `randomize nullable data class with custom randomizer`() {
        testGeneratedCodeUsingStandardPlugin(
            """
                $imports

                fun runTest():TestOutput{
                    return withTestOutput{
                        val v= random<DtContainer2>(randomConfig=LegalRandomConfigObject, randomizers = {
                            constant<Int?>(123)
                        })
                        putData(Q(v))
                    }
                }
            """
            ,
            fileName = "main.kt"
        ) {
            testCompilation = { result, _ ->
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                result.executeRunTestFunction { output ->
                    // output.getObjs() shouldBe listOf(
                        // DtContainer1(123)
                    // )
                }
            }
        }
    }
}
