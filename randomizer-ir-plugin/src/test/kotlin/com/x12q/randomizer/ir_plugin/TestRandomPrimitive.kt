package com.x12q.randomizer.ir_plugin

import com.tschuchort.compiletesting.KotlinCompilation
import com.x12q.randomizer.ir_plugin.mock_objects.LegalRandomConfigObject
import com.x12q.randomizer.ir_plugin.mock_objects.NonNullRandomConfig
import com.x12q.randomizer.ir_plugin.mock_objects.TestRandomConfig
import com.x12q.randomizer.lib.RandomConfigImp
import com.x12q.randomizer.lib.randomizer.ConstantRandomizer
import com.x12q.randomizer.test.util.assertions.executeRunTestFunction
import com.x12q.randomizer.test.util.test_code.TestImportsBuilder
import io.kotest.matchers.shouldBe
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import kotlin.test.Test


@OptIn(ExperimentalCompilerApi::class)
class TestRandomPrimitive {
    data class PrimitivesContainer(
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
                ${TestImportsBuilder.stdImport.import(PrimitivesContainer::class)}

                fun runTest():TestOutput{
                    return withTestOutput{
                        putData(Q123Data.random(LegalRandomConfigObject))
                    }
                }

                @Randomizable
                data class Q123Data(override val data:PrimitivesContainer):WithData
            """
            ,
            fileName = "main.kt"
        ) {
            testCompilation = { result, _ ->
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                result.executeRunTestFunction { output ->
                    output.getObjs() shouldBe listOf(
                        PrimitivesContainer(
                            LegalRandomConfigObject.nextBoolean(),
                            LegalRandomConfigObject.nextInt(),
                            LegalRandomConfigObject.nextLong(),
                            LegalRandomConfigObject.nextFloat(),
                            LegalRandomConfigObject.nextDouble(),
                            LegalRandomConfigObject.nextByte(),
                            LegalRandomConfigObject.nextChar(),
                            LegalRandomConfigObject.nextShort(),
                            LegalRandomConfigObject.nextString(),
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
                ${TestImportsBuilder.stdImport.import(UInt::class).import(UPrimitivies::class)}

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
                result.executeRunTestFunction { output->
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
                ${TestImportsBuilder.stdImport.import(NullablePrimitives::class)}

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
                result.executeRunTestFunction { output->
                        output.getObjs() shouldBe   listOf(
                        NullablePrimitives(null,null,null,null,null,null,null,null,null,null,null,null,),
                       NullablePrimitives(
                           int = NonNullRandomConfig.nextInt(),
                           boolean = NonNullRandomConfig.nextBoolean(),
                           long = NonNullRandomConfig.nextLong(),
                           float = NonNullRandomConfig.nextFloat(),
                           double = NonNullRandomConfig.nextDouble(),
                           byte = NonNullRandomConfig.nextByte(),
                           char = NonNullRandomConfig.nextChar(),
                           short = NonNullRandomConfig.nextShort(),
                           string = NonNullRandomConfig.nextString(),
                           number = NonNullRandomConfig.nextNumber(),
                           unit = NonNullRandomConfig.nextUnit(),
                           any = NonNullRandomConfig.nextAny()
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
                ${TestImportsBuilder.stdImport}

                fun main(){
                    println(Q123.random())
                    println(Q123.random(${TestImportsBuilder.stdImport.nameOf(RandomConfigImp::class)}.default))
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
                
                ${TestImportsBuilder.stdImport}

                fun main(){
                    println(Q123.random())
                    println(Q123.random(${TestImportsBuilder.stdImport.nameOf(RandomConfigImp::class)}.default))
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
                ${TestImportsBuilder.stdImport.import(NullableUPrim::class)}

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
                result.executeRunTestFunction { output->
                    output.getObjs() shouldBe listOf(
                        NullableUPrim(null,null,null,null),
                        NullableUPrim(
                            NonNullRandomConfig.nextUInt(),
                            NonNullRandomConfig.nextULong(),
                            NonNullRandomConfig.nextUByte(),
                            NonNullRandomConfig.nextUShort(),

                        ),

                    )
                }
            }
        }
    }


    data class PrimitivesContainer_WithNullable(
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

        val intN: Int?,
//        val booleanN: Boolean?,
//        val longN: Long?,
//        val floatN: Float?,
//        val doubleN: Double?,
//        val byteN: Byte?,
//        val charN: Char?,
//        val shortN: Short?,
//        val stringN: String?,
//        val numberN: Number?,
//        val unitN: Unit?,
//        val anyN: Any?,
    )


    /**
     * This test generating nullable primitive with custom "randomizers"
     */
    @Test
    fun `nullable primitive with custom randomizers`() {

        val imports = TestImportsBuilder.stdImport.import(PrimitivesContainer::class).import(
            PrimitivesContainer_WithNullable::class)
        testGeneratedCodeUsingStandardPlugin(
            """
               $imports

                fun runTest():TestOutput{
                    return withTestOutput{
                        putData(QxC.random{
                            add(ConstantRandomizer.of(true))
                            add(ConstantRandomizer.of(123))
                            add(ConstantRandomizer.of(321L))
                            add(ConstantRandomizer.of(543f))
                            add(ConstantRandomizer.of(89.78))
                            add(ConstantRandomizer.of(123.toByte()))
                            add(ConstantRandomizer.of('z'))
                            add(ConstantRandomizer.of(88.toShort()))
                            add(ConstantRandomizer.of("qwezxc123"))
                            add(ConstantRandomizer.of<Number>(44.123))
                            add(ConstantRandomizer.of(Unit))
                            add(ConstantRandomizer.of<Any>("mmmm"))
                        })
                    }
                }
                @Randomizable(randomConfig = TestRandomConfig::class)
                data class QxC(override val data:PrimitivesContainer_WithNullable):WithData
            """,
        ) {

            testCompilation = { result, testStream ->
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                result.executeRunTestFunction {
                    val c = TestRandomConfig()
                    val expectation = PrimitivesContainer_WithNullable(
                        boolean = true,
                        int = 123,
                        long = 321L,
                        float = 543f,
                        double = 89.78,
                        byte = 123.toByte(),
                        char = 'z',
                        short = 88.toShort(),
                        string = "qwezxc123",
                        number = 44.123,
                        unit = Unit,
                        any = "mmmm",
                        intN = if(c.nextBoolean()){
                            123
                        }else{
                            null
                        }
                    )
                    it.getObjs() shouldBe listOf(
                        expectation
                    )
                }
            }
        }
    }
}
