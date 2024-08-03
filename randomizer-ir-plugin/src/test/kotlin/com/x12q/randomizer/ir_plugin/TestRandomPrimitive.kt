package com.x12q.randomizer.ir_plugin

import com.tschuchort.compiletesting.KotlinCompilation
import com.x12q.randomizer.ir_plugin.mock_objects.LegalRandomConfigObject
import com.x12q.randomizer.ir_plugin.mock_objects.NonNullRandomConfig
import com.x12q.randomizer.test.util.assertions.runMain
import com.x12q.randomizer.test.util.assertions.runRunTest
import com.x12q.randomizer.test.util.test_code.ImportData
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.shouldBe
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import kotlin.test.Test


@OptIn(ExperimentalCompilerApi::class)
class TestRandomPrimitive {
    data class Primitives(
        val boolean: Boolean,
        val int: Int,
        val long: Long,
        val float: Float,
        val double: Double,
        val byte: Byte,
        val char: Char,
        val short: Short,
        val string: String,
        val number: Number,
        val unit: Unit,
        val any: Any,
    )

    @Test
    fun `randomize primitive parameter`() {

        testGeneratedCodeUsingStandardPlugin(
            """
                ${ImportData.stdImport.import(Primitives::class)}

                fun runTest():TestOutput{
                    return withTestOutput{
                        putData(Q123Data.random(LegalRandomConfigObject))
                    }
                }

                @Randomizable
                data class Q123Data(override val data:Primitives):WithData
            """
            ,
            fileName = "main.kt"
        ) {
            testCompilation = { result, _ ->
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                result.runRunTest { output ->
                    output.getObjs() shouldBe listOf(
                        Primitives(
                            LegalRandomConfigObject.nextBoolean(),
                            LegalRandomConfigObject.nextInt(),
                            LegalRandomConfigObject.nextLong(),
                            LegalRandomConfigObject.nextFloat(),
                            LegalRandomConfigObject.nextDouble(),
                            LegalRandomConfigObject.nextByte(),
                            LegalRandomConfigObject.nextChar(),
                            LegalRandomConfigObject.nextShort(),
                            LegalRandomConfigObject.nextStringUUID(),
                            LegalRandomConfigObject.nextNumber(),
                            Unit,
                            LegalRandomConfigObject.nextAny()
                        )
                    )
                }
            }
        }
    }
    data class UPrimitivies(
        val uint:UInt,
        val ulong:ULong,
        val ubyte:UByte,
        val ushort: UShort,
    )

    @Test
    fun `randomize primitive U parameter`() {

        testGeneratedCodeUsingStandardPlugin(
            """
                ${ImportData.stdImport.import(UInt::class).import(UPrimitivies::class)}

                fun runTest():TestOutput{
                    return withTestOutput{
                        putData(UPrimitiveData.random(LegalRandomConfigObject))
                    }
                }

                @Randomizable
                data class UPrimitiveData(override val data:UPrimitivies):WithData
            """,
            fileName = "main.kt"
        ) {
            testCompilation = { result, _ ->
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                result.runRunTest {output->
                    output.getObjs() shouldBe listOf(
                        UPrimitivies(
                            LegalRandomConfigObject.nextUInt(),
                            LegalRandomConfigObject.nextULong(),
                            LegalRandomConfigObject.nextUByte(),
                            LegalRandomConfigObject.nextUShort()
                        )
                    )
                }
            }
        }
    }


    data class NullablePrimitives(
        val int:Int?,
        val boolean: Boolean?,
        val long:Long?,
        val float:Float?,
        val double:Double?,
        val byte:Byte?,
        val char:Char?,
        val short: Short?,
        val string:String?,
        val number:Number?,
        val unit:Unit?,
        val any:Any?,
    )

    @Test
    fun `randomize nullable primitive`() {

        testGeneratedCodeUsingStandardPlugin(
            """
                ${ImportData.stdImport.import(NullablePrimitives::class)}

                fun runTest():TestOutput{
                    return withTestOutput{
                        putData(Q123.random(NullRandomConfig))
                        putData(Q123.random(NonNullRandomConfig))
                    }
                }

                @Randomizable
                data class Q123(
                   override val data:NullablePrimitives
                ):WithData
            """,
            fileName = "main.kt"
        ) {
            testCompilation = { result, _ ->
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                result.runRunTest { output->
                    output.getObjs() shouldBe   listOf(
                        NullablePrimitives(null,null,null,null,null,null,null,null,null,null,null,null,),
                        NullablePrimitives(
                            NonNullRandomConfig.nextIntOrNull(),
                            NonNullRandomConfig.nextBoolOrNull(),
                            NonNullRandomConfig.nextLongOrNull(),
                            NonNullRandomConfig.nextFloatOrNull(),
                            NonNullRandomConfig.nextDoubleOrNull(),
                            NonNullRandomConfig.nextByteOrNull(),
                            NonNullRandomConfig.nextCharOrNull(),
                            NonNullRandomConfig.nextShortOrNull(),
                            NonNullRandomConfig.nextStringUUIDOrNull(),
                            NonNullRandomConfig.nextNumberOrNull(),
                            NonNullRandomConfig.nextUnitOrNull(),
                            NonNullRandomConfig.nextAnyOrNull()
                        ),
                    )
                }
            }
        }
    }


    @Test
    fun `randomize primitive Nothing`() {

        testGeneratedCodeUsingStandardPlugin(
            """
                ${ImportData.stdImport}

                fun main(){
                    println(Q123.random())
                    println(Q123.random(DefaultRandomConfig.default))
                }

                @Randomizable
                data class Q123(
                    val nt:Nothing,
                )
            """,
            fileName = "main.kt"
        ) {
            testCompilation = { result, _ ->
                result.exitCode shouldBe KotlinCompilation.ExitCode.INTERNAL_ERROR
            }
        }
    }

    @Test
    fun `randomize primitive Nothing nullable`() {

        testGeneratedCodeUsingStandardPlugin(
            """
                import com.x12q.randomizer.lib.DefaultRandomConfig
                import com.x12q.randomizer.lib.annotations.Randomizable

                fun main(){
                    println(Q123.random())
                    println(Q123.random(DefaultRandomConfig.default))
                }

                @Randomizable
                data class Q123(
                    val nt:Nothing?,
                )
            """,
            fileName = "main.kt"
        ) {
            testCompilation = { result, _ ->
                result.exitCode shouldBe KotlinCompilation.ExitCode.INTERNAL_ERROR
            }
        }
    }

    data class NullableUPrim(
        val uint:UInt?,
        val ulong:ULong?,
        val ubyte:UByte?,
        val ushort: UShort?,
    )

    @Test
    fun `randomize nullable U primitive`() {

        testGeneratedCodeUsingStandardPlugin(
            """
                ${ImportData.stdImport.import(NullableUPrim::class)}

                fun runTest():TestOutput{
                    return withTestOutput{
                        putData(Q123.random(NullRandomConfig))
                        putData(Q123.random(NonNullRandomConfig))
                    }
                }


                @Randomizable
                data class Q123(
                   override val data:NullableUPrim
                ):WithData
            """,
            fileName = "main.kt"
        ) {
            testCompilation = { result, _ ->
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                result.runRunTest {output->
                    output.getObjs() shouldBe listOf(
                        NullableUPrim(null,null,null,null),
                        NullableUPrim(
                            NonNullRandomConfig.nextUIntOrNull(),
                            NonNullRandomConfig.nextULongOrNull(),
                            NonNullRandomConfig.nextUByteOrNull(),
                            NonNullRandomConfig.nextUShortOrNull(),

                        ),

                    )
                }
            }
        }
    }


}
