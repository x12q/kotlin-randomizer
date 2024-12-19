package com.x12q.randomizer.ir_plugin

import com.tschuchort.compiletesting.KotlinCompilation
import com.x12q.randomizer.ir_plugin.mock_objects.LegalRandomConfigObject
import com.x12q.randomizer.ir_plugin.mock_objects.NonNullRandomConfig
import com.x12q.randomizer.ir_plugin.mock_objects.TestRandomConfig
import com.x12q.randomizer.ir_plugin.testGeneratedCodeUsingStandardPlugin
import com.x12q.randomizer.lib.RandomConfigImp
import com.x12q.randomizer.test.util.WithData
import com.x12q.randomizer.test.util.assertions.executeRunTestFunction
import com.x12q.randomizer.test.util.test_code.TestImportsBuilder
import io.kotest.matchers.shouldBe
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import kotlin.test.Test


@OptIn(ExperimentalCompilerApi::class)
class TestRandomPrimitive_d {
    data class Q1234(
        val nt:Nothing,
    )

    data class Q123Nothing(
        val nt:Nothing?,
    )

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
    data class Q123Data(override val data:PrimitivesContainer):WithData
    data class UPrimitivies(
        val uint:UInt,
        val ulong:ULong,
        val ubyte:UByte,
        val ushort: UShort,
    )

    data class UPrimitiveData(override val data:UPrimitivies):WithData

    data class Q123(
        override val data:NullablePrimitives
    ):WithData

    val imports = TestImportsBuilder.stdImport
        .import(QxC::class)
        .import(Q123::class)
        .import(Q1234::class)
        .import(PrimitivesContainer::class)
        .import(Q123Data::class)
        .import(UInt::class)
        .import(UPrimitivies::class)
        .import(UPrimitiveData::class)
        .import(Q123Nothing::class)
        .import(Q123_NullableUPrim::class)

    @Test
    fun `randomize primitive parameter`() {

        testGeneratedCodeUsingStandardPlugin(
            """
                $imports

                fun runTest():TestOutput{
                    return withTestOutput{
                        putData(random<Q123Data>(randomConfig=LegalRandomConfigObject))
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


    @Test
    fun `randomize primitive U parameter`() {

        testGeneratedCodeUsingStandardPlugin(
            """
                $imports

                fun runTest():TestOutput{
                    return withTestOutput{
                        putData(random<UPrimitiveData>(randomConfig=LegalRandomConfigObject))
                    }
                }
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
                $imports

                fun runTest():TestOutput{
                    return withTestOutput{ 
                        putData(random<Q123>(randomConfig=NullRandomConfig))
                        putData(random<Q123>(randomConfig=NonNullRandomConfig))
                    }
                }
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
                $imports

                fun main(){
                    println(random<Q1234>())
                    println(random<Q1234>(randomConfig=${TestImportsBuilder.stdImport.nameOf(RandomConfigImp::class)}.default))
                }
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
                
                $imports

                fun main(){
                    println(random<Q123Nothing>())
                    println(random<Q123Nothing>(randomConfig=${TestImportsBuilder.stdImport.nameOf(RandomConfigImp::class)}.default))
                }
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
    data class Q123_NullableUPrim(
        override val data:NullableUPrim
    ):WithData
    @Test
    fun `randomize nullable U primitive`() {

        testGeneratedCodeUsingStandardPlugin(
            """
                $imports

                fun runTest():TestOutput{
                    return withTestOutput{
                        putData(random<Q123_NullableUPrim>(randomConfig=NullRandomConfig))
                        putData(random<Q123_NullableUPrim>(randomConfig=NonNullRandomConfig))
                    }
                }
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
    data class QxC(override val data:PrimitivesContainer_WithNullable):WithData

    /**
     * This test generating nullable primitive with custom "randomizers"
     */
    @Test
    fun `nullable primitive with custom randomizers`() {

        testGeneratedCodeUsingStandardPlugin(
            """
               $imports

                fun runTest():TestOutput{
                    return withTestOutput{
                        putData(random<QxC>(randomConfig=TestRandomConfig()){
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
