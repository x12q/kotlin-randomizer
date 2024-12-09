package com.x12q.randomizer.ir_plugin

import com.tschuchort.compiletesting.KotlinCompilation
import com.x12q.randomizer.ir_plugin.mock_objects.AlwaysTrueRandomConfig
import com.x12q.randomizer.test.util.assertions.executeRunTestFunction
import com.x12q.randomizer.test.util.test_code.TestImportsBuilder
import io.kotest.matchers.shouldBe
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import kotlin.test.Test


@OptIn(ExperimentalCompilerApi::class)
class TestPassingRandomContextBuilder {

    data class Dt(val i: Int)

    @Test
    fun `access RandomConfig from RandomContextBuilder`() {

        val imports = TestImportsBuilder.stdImport.import(Dt::class)
        testGeneratedCodeUsingStandardPlugin(
            """
               $imports

                fun runTest():TestOutput{
                    return withTestOutput{
                        putData(QxC.random{
                            add(FactoryClassRandomizer.of{Dt(this.randomConfig.nextInt())})
                        })
                    }
                }
                @Randomizable(randomConfig = AlwaysTrueRandomConfig::class)
                data class QxC(override val data:Dt):WithData
            """,
        ) {
            testCompilation = { result, testStream ->
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                result.executeRunTestFunction {
                    it.getObjs() shouldBe listOf(
                        Dt(AlwaysTrueRandomConfig.nextInt())
                    )
                }
            }
        }
    }
    @Test
    fun `pass randomizers config function`() {

        val imports = TestImportsBuilder.stdImport.import(Dt::class)
        testGeneratedCodeUsingStandardPlugin(
            """
               $imports

                fun runTest():TestOutput{
                    return withTestOutput{
                        putData(QxC.random{
                            add(FactoryClassRandomizer.of({Dt(-999)}))
                        })
                    }
                }
                @Randomizable
                data class QxC(override val data:Dt):WithData
            """,
        ) {
            testCompilation = { result, testStream ->
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                result.executeRunTestFunction {
                    it.getObjs() shouldBe listOf(Dt(-999))
                }
            }
        }
    }

    // val qweqwe: RandomContextBuilder.() -> Unit = {
    //     add(ConstantClassRandomizer(true, TypeKey.of<Boolean>()))
    //     add(ConstantClassRandomizer(123, TypeKey.of<Int>()))
    //     add(ConstantClassRandomizer(321L, TypeKey.of<Long>()))
    //     add(ConstantClassRandomizer(543f, TypeKey.of<Float>()))
    //     add(ConstantClassRandomizer(89.78, TypeKey.of<Double>()))
    //     add(ConstantClassRandomizer(123.toByte(), TypeKey.of<Byte>()))
    //     add(ConstantClassRandomizer('z', TypeKey.of<Char>()))
    //     add(ConstantClassRandomizer(88.toShort(), TypeKey.of<Short>()))
    //     add(ConstantClassRandomizer("qwezxc123", TypeKey.of<String>()))
    //     add(ConstantClassRandomizer(44.123, TypeKey.of<Number>()))
    //     add(ConstantClassRandomizer(Unit, TypeKey.of<Unit>()))
    //     add(ConstantClassRandomizer("mmmm", TypeKey.of<Any>()))
    // }

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

       // val intN: Int?,
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


    @Test
    fun `random context overriding default randomizer`() {

        val expectation = PrimitivesContainer(
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
        )


        val imports = TestImportsBuilder.stdImport.import(PrimitivesContainer::class)
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
                @Randomizable
                data class QxC(override val data:PrimitivesContainer):WithData
            """,
        ) {
            testCompilation = { result, testStream ->
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                result.executeRunTestFunction {
                    it.getObjs() shouldBe listOf(
                        expectation
                    )
                }
            }
        }
    }
}
