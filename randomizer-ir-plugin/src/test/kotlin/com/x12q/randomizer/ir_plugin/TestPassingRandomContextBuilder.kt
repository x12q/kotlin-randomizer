package com.x12q.randomizer.ir_plugin

import com.tschuchort.compiletesting.KotlinCompilation
import com.x12q.randomizer.ir_plugin.mock_objects.AlwaysTrueRandomConfig
import com.x12q.randomizer.lib.ConstantClassRandomizer
import com.x12q.randomizer.lib.FactoryClassRandomizer
import com.x12q.randomizer.lib.RandomizerCollectionImp
import com.x12q.randomizer.lib.RandomContextBuilder
import com.x12q.randomizer.test.util.assertions.runRunTest
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
                            add(${imports.nameOf(FactoryClassRandomizer::class)}<Dt>({Dt(this.randomConfig.nextInt())},Dt::class))
                        })
                    }
                }
                @Randomizable(randomConfig = AlwaysTrueRandomConfig::class)
                data class QxC(override val data:Dt):WithData
            """,
        ) {
            testCompilation = { result, testStream ->
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                result.runRunTest {
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
                            add(${imports.nameOf(FactoryClassRandomizer::class)}<Dt>({Dt(-999)},Dt::class))
                        })
                    }
                }
                @Randomizable
                data class QxC(override val data:Dt):WithData
            """,
        ) {
            testCompilation = { result, testStream ->
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                result.runRunTest {
                    it.getObjs() shouldBe listOf(
                        Dt(-999)
                    )
                }
            }
        }
    }

    val qweqwe: RandomContextBuilder.() -> Unit = {
        add(ConstantClassRandomizer(true, Boolean::class))
        add(ConstantClassRandomizer(123, Int::class))
        add(ConstantClassRandomizer(321L, Long::class))
        add(ConstantClassRandomizer(543f, Float::class))
        add(ConstantClassRandomizer(89.78, Double::class))
        add(ConstantClassRandomizer(123.toByte(), Byte::class))
        add(ConstantClassRandomizer('z', Char::class))
        add(ConstantClassRandomizer(88.toShort(), Short::class))
        add(ConstantClassRandomizer("qwezxc123", String::class))
        add(ConstantClassRandomizer(44.123, Number::class))
        add(ConstantClassRandomizer(Unit, Unit::class))
        add(ConstantClassRandomizer("mmmm", Any::class))
    }

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

//        val intN: Int?,
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
            true,
            123,
            321L,
            543f,
            89.78,
            123.toByte(),
            'z',
            88.toShort(),
            "qwezxc123",
            44.123,
            Unit,
            "mmmm"
        )


        val imports = TestImportsBuilder.stdImport.import(PrimitivesContainer::class)
        testGeneratedCodeUsingStandardPlugin(
            """
               $imports

                fun runTest():TestOutput{
                    return withTestOutput{
                        putData(QxC.random{
                            add(ConstantClassRandomizer(true, Boolean::class))
                            add(ConstantClassRandomizer(123, Int::class))
                            add(ConstantClassRandomizer(321L, Long::class))
                            add(ConstantClassRandomizer(543f, Float::class))
                            add(ConstantClassRandomizer(89.78, Double::class))
                            add(ConstantClassRandomizer(123.toByte(), Byte::class))
                            add(ConstantClassRandomizer('z', Char::class))
                            add(ConstantClassRandomizer(88.toShort(), Short::class))
                            add(ConstantClassRandomizer("qwezxc123", String::class))
                            add(ConstantClassRandomizer(44.123, Number::class))
                            add(ConstantClassRandomizer(Unit, Unit::class))
                            add(ConstantClassRandomizer("mmmm", Any::class))
                        })
                    }
                }
                @Randomizable
                data class QxC(override val data:PrimitivesContainer):WithData
            """,
        ) {
            testCompilation = { result, testStream ->
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                result.runRunTest {
                    it.getObjs() shouldBe listOf(
                        expectation
                    )
                }
            }
        }
    }

}
