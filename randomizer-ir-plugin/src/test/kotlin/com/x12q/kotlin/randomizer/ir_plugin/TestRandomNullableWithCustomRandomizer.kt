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
        val boolean: Boolean?,
        val int: Int?,
        val long: Long?,
        val float: Float?,
        val double: Double?,
        val byte: Byte?,
        val char: Char?,
        val short: Short?,
        val string: String?,
        val number: Number?,
        val unit: Unit?,
        val any: Any?,
    )

    data class ABC(val str: String,  val i:Int)

    data class DtContainer2(
        val d: ABC?
    )

    data class Q<T:Any>(
        override val data:T
    ):WithData

    val imports = TestImportsBuilder.stdImport
        .import(Q::class)
        .import(DtContainer1::class)
        .import(DtContainer2::class)
        // .import(UInt::class)
        .import(ABC::class)

    @Test
    fun `randomize nullable primitive with non-null custom randomizer`() {
        testGeneratedCodeUsingStandardPlugin(
            """
                $imports

                fun runTest():TestOutput{
                    return withTestOutput{
                        val v= random<DtContainer1>(randomConfig=LegalRandomConfigObject, randomizers = {
                            constant<Boolean?>(true)
                            constant<Int?>(123)
                            constant<Long?>(6L)
                            constant<Float?>(543f)
                            constant<Double?>(89.78)
                            constant<Byte?>(123.toByte())
                            constant<Char?>('z')
                            constant<Short?>(88.toShort())
                            constant<String?>("qwezxc123")
                            constant<Number?>(44.123)
                            constant<Unit?>(Unit)
                            constant<Any?>("mmmm")
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
                            boolean = true,
                            int = 123,
                            long =  6L,
                            float = 543f,
                            double = 89.78,
                            byte = 123.toByte(),
                            char = 'z',
                            short = 88.toShort(),
                            string = "qwezxc123",
                            number = 44.123,
                            unit = Unit,
                            any = "mmmm",
                        )
                    )
                }
            }
        }
    }

    @Test
    fun `randomize nullable primitive with null custom randomizer`() {
        testGeneratedCodeUsingStandardPlugin(
            """
                $imports

                fun runTest():TestOutput{
                    return withTestOutput{
                        val v= random<DtContainer1>(randomConfig=LegalRandomConfigObject, randomizers = {
                            constant<Boolean?>(null)
                            constant<Int?>(null)
                            constant<Long?>(null)
                            constant<Float?>(null)
                            constant<Double?>(null)
                            constant<Byte?>(null)
                            constant<Char?>(null)
                            constant<Short?>(null)
                            constant<String?>(null)
                            constant<Number?>(null)
                            constant<Unit?>(null)
                            constant<Any?>(null)
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
                            boolean = null,
                            int = null,
                            long =  null,
                            float = null,
                            double = null,
                            byte = null,
                            char = null,
                            short = null,
                            string = null,
                            number = null,
                            unit = null,
                            any = null,
                        )
                    )
                }
            }
        }
    }

    @Test
    fun `randomize nullable complex class with non-null custom randomizer`() {
        testGeneratedCodeUsingStandardPlugin(
            """
                $imports

                fun runTest():TestOutput{
                    return withTestOutput{
                        val v= random<DtContainer2>(randomConfig=LegalRandomConfigObject, randomizers = {
                            constant<ABC?>(ABC("abc",123))
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
                        DtContainer2(ABC("abc",123))
                    )
                }
            }
        }
    }

    @Test
    fun `randomize nullable complex class with null custom randomizer`() {
        testGeneratedCodeUsingStandardPlugin(
            """
                $imports

                fun runTest():TestOutput{
                    return withTestOutput{
                        val v= random<DtContainer2>(randomConfig=LegalRandomConfigObject, randomizers = {
                            constant<ABC?>(null)
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
                        DtContainer2(null)
                    )
                }
            }
        }
    }
}
