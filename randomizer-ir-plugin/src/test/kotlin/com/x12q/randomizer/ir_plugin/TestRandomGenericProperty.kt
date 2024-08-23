package com.x12q.randomizer.ir_plugin

import com.tschuchort.compiletesting.KotlinCompilation
import com.x12q.randomizer.ir_plugin.mock_objects.AlwaysTrueRandomConfig
import com.x12q.randomizer.ir_plugin.mock_objects.LegalRandomConfigObject
import com.x12q.randomizer.ir_plugin.mock_objects.RandomConfigForTest
import com.x12q.randomizer.lib.ConstantClassRandomizer
import com.x12q.randomizer.lib.constantRandomizer
import com.x12q.randomizer.test.util.assertions.runRunTest
import com.x12q.randomizer.test.util.test_code.TestImportsBuilder
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import kotlin.test.Test


@OptIn(ExperimentalCompilerApi::class)
class TestRandomGenericProperty {

    data class Qx<T1>(val i: T1?)
    data class Qx2<Z>(val paramOfQ2: Z)
    data class Qx4<M>(val paramOfQ4: M)
    data class Qx6<H>(val paramOfQ6: H)
    data class TwoGeneric<G1, G2>(val g1: G1, val g2: G2)

    private val imports = TestImportsBuilder.stdImport
        .import(Qx::class)
        .import(Qx2::class)
        .import(Qx3::class)
        .import(Qx4::class)
        .import(Qx6::class)
        .import(TwoGeneric::class)

    @Test
    fun `complex class as generic WITHOUT custom randomizer`() {
        testGeneratedCodeUsingStandardPlugin(
            """
                $imports

                @Randomizable(randomConfig = LegalRandomConfigObject::class)
                data class QxC<T1:Any>(override val data:T1):WithData

                fun runTest():TestOutput{
                    return withTestOutput{
                        putData(QxC.random<Qx2<Int>>())
                    }
                }
            """,
        ) {
            testCompilation = { result, _ ->
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                val objectList = result.runRunTest().getObjs()
                objectList.size shouldBe 1
                objectList[0].shouldBeInstanceOf<Qx2<Int>>()
            }
        }
    }


    @Test
    fun `complex class as generic with custom randomizer 2`() {
        testGeneratedCodeUsingStandardPlugin(
            """
               $imports
                @Randomizable
                data class Qx2x<Z>(val paramOfQ2x: Z)

                @Randomizable(randomConfig = LegalRandomConfigObject::class)
                data class QxC<T1:Any>(override val data:T1):WithData

                fun runTest():TestOutput{
                    return withTestOutput{
                        putData(QxC.random<Qx2x<Int>>(randomizers = {
                            val rdm = constantRandomizer(Qx2x.random<Int>(randomConfig))
                            add(rdm)
                        }))
                    }
                }
            """,
        ) {
            testCompilation = { result, _ ->
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                val objectList = result.runRunTest().getObjs()
                objectList.toString() shouldBe "[Qx2x(paramOfQ2x=3)]"
            }
        }
    }

    @Test
    fun `complex class as generic with custom randomizer`() {
        testGeneratedCodeUsingStandardPlugin(
            """
                $imports

                @Randomizable
                data class Qx2x<Z>(val paramOfQ2x: Z)

                @Randomizable(randomConfig = LegalRandomConfigObject::class)
                data class QxC<T1:Any>(override val data:T1):WithData

                fun runTest():TestOutput{
                    return withTestOutput{
                        putData(QxC.random<Qx2x<Int>>(randomizers={
                            constant(Qx2x.random<Int>(randomConfig))
                        }))
                    }
                }
            """,
        ) {
            testCompilation = { result, _ ->
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                val objectList = result.runRunTest().getObjs()
                objectList.toString() shouldBe "[Qx2x(paramOfQ2x=3)]"
            }
        }
    }


    @Test
    fun `3 layers of multiple nested generic`() {
        testGeneratedCodeUsingStandardPlugin(
            """
               $imports
                @Randomizable
                data class QxC<T1,T2>(override val data: TwoGeneric<Qx2<Qx4<T1>>,Qx4<Qx6<T2>>>):WithData

                fun runTest():TestOutput{
                    return withTestOutput{
                        putData(QxC.random<Int,Float>(randomizers = {
                            constant(123)
                            constant(-9.45f)
                        }))
                        putData(QxC.random<Int,Float>())
                    }
                }
            """,
        ) {
            testCompilation = { result, _ ->
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                val objectList = result.runRunTest().getObjs()
                objectList.size shouldBe 2
                objectList[0] shouldBe TwoGeneric(
                    g1 = Qx2(Qx4(123)),
                    g2 = Qx4(Qx6(-9.45f)),
                )
                objectList[1].shouldBeInstanceOf<TwoGeneric<Qx2<Qx4<Int>>,Qx4<Qx6<Float>>>>()
            }
        }
    }


    @Test
    fun `2 layers of nested nullable generic`() {
        testGeneratedCodeUsingStandardPlugin(
            """
                $imports

                fun runTest():TestOutput{
                    return withTestOutput{
                        putData(QxC.random<Int>())
                    }
                }
                @Randomizable(NullRandomConfig::class)
                data class QxC<T1>(override val data:Qx2<Qx<T1>>):WithData
            """,
        ) {
            testCompilation = { result, _ ->
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                val objectList = result.runRunTest().getObjs()
                objectList shouldBe listOf(
                    Qx2(Qx(null)),
                )
            }
        }
    }

    @Test
    fun `2 layers of nested generic`() {
        testGeneratedCodeUsingStandardPlugin(
            """
                $imports

                fun runTest():TestOutput{
                    return withTestOutput{
                        putData(QxC.random<Int>(randomizers = {
                            constant(123)
                        }))
                    }
                }
                @Randomizable
                data class QxC<T1>(override val data:Qx2<Qx4<T1>>):WithData
            """,
        ) {
            testCompilation = { result, _ ->
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                val objectList = result.runRunTest().getObjs()
                objectList shouldBe listOf(
                    Qx2(Qx4(123)),
                )
            }
        }
    }

    @Test
    fun `3 layers of single nested generic`() {
        testGeneratedCodeUsingStandardPlugin(
            """
                $imports

                fun runTest():TestOutput{
                    return withTestOutput{
                        putData(QxC.random<Int>(randomizers = {
                            constant(123)
                        }))
                    }
                }
                @Randomizable
                data class QxC<T1>(override val data:Qx2<Qx4<Qx6<T1>>>):WithData
            """,
        ) {
            testCompilation = { result, _ ->
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                val objectList = result.runRunTest().getObjs()
                objectList shouldBe listOf(
                    Qx2(Qx4(Qx6(123))),
                )
            }
        }
    }

    @Test
    fun `random primitive generic using default random text`() {
        testGeneratedCodeUsingStandardPlugin(
            """
                $imports

                fun runTest():TestOutput{
                    return withTestOutput{
                        putData(QxC.random<String>())
                    }
                }
                @Randomizable(randomConfig = LegalRandomConfigObject::class)
                data class QxC<T1>(override val data:Qx2<T1>):WithData
            """,
        ) {
            testCompilation = { result, _ ->
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                result.runRunTest().getObjs() shouldBe listOf(
                    Qx2(LegalRandomConfigObject.nextStringUUID()),
                )
            }
        }
    }


    @Test
    fun `custom primitive generic param`() {

        val randomFromRandomContext = -99

        testGeneratedCodeUsingStandardPlugin(
            """
                $imports

                fun runTest():TestOutput{
                    return withTestOutput{
                        putData(QxC.random<Int>(randomizers = {
                            val rdm = ConstantClassRandomizer<Int>(-99,Int::class)
                            add(rdm)
                        }))
                    }
                }
                @Randomizable(randomConfig = LegalRandomConfigObject::class)
                data class QxC<T1>(override val data:Qx2<T1>):WithData
            """,
        ) {
            testCompilation = { result, _ ->
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                result.runRunTest().getObjs() shouldBe listOf(
                    Qx2(randomFromRandomContext),
                )
            }
        }
    }

    @Test
    fun `nullable generic property`() {

        testGeneratedCodeUsingStandardPlugin(
            """
                $imports

                fun runTest():TestOutput{
                    return withTestOutput{
                        putData(QxC.random<Int>(AlwaysFalseRandomConfig, randomizers={
                            factory{randomConfig.nextInt()}
                        }))
                        putData(QxC.random<Int>(AlwaysTrueRandomConfig, randomizers={
                            factory{randomConfig.nextInt()}
                        }))
                    }
                }
                @Randomizable
                data class QxC<T1>(override val data:Qx<T1>):WithData
            """,
        ) {
            testCompilation = { result, testStream ->
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                val l = result.runRunTest().getObjs()
                l shouldBe listOf(
                    Qx<Int>(null), Qx(AlwaysTrueRandomConfig.nextInt())
                )
            }
        }
    }


    @Test
    fun `accessing random logic of RandomContext from generic function`() {

        testGeneratedCodeUsingStandardPlugin(
            """
                $imports

                fun runTest():TestOutput{
                    return withTestOutput{
                        putData(QxC.random<Int>(randomizers = {
                            factory{randomConfig.nextInt()}
                        }))
                        putData(QxC.random<Int>(LegalRandomConfigObject,randomizers = {
                            factory{randomConfig.nextInt()}
                        }))
                    }
                }
                @Randomizable(randomConfig = LegalRandomConfigObject::class)
                data class QxC<T1>(override val data:Qx2<T1>):WithData
            """,
        ) {
            testCompilation = { result, _ ->
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                result.runRunTest().getObjs() shouldBe listOf(
                    Qx2(LegalRandomConfigObject.nextInt()),
                    Qx2(LegalRandomConfigObject.nextInt())
                )
            }
        }
    }


    data class Qx3<T1, T2, T3>(val i1: T1, val i2: T2, val i3: T3)

    @Test
    fun `randomize 3 generic property`() {

        testGeneratedCodeUsingStandardPlugin(
            """
                $imports

                fun runTest():TestOutput{
                    return withTestOutput{
                        putData(QxC.random<Int,String,Double>(
                                randomizers = {
                                    constant(123)
                                    factory{
                                        val config = this.randomConfig
                                        val num=config.nextInt()
                                        "abc_"+num.toString()
                                    }
                                    constant(1.23)
                                }
                            )
                        )
                    }
                }
                @Randomizable(randomConfig = RandomConfigForTest::class)
                data class QxC<T1,T2,T3>(override val data:Qx3<T1,T2,T3>):WithData{
                    companion object{
                        fun q9(fn:(()->Int)?):Int{
                            val v1 = fn?.invoke()
                            val v2 = 100
                            val rt = v1 ?: v2
                            return rt
                        }
                    }
                }
            """,
        ) {
            testCompilation = { result, _ ->
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                result.runRunTest().getObjs() shouldBe listOf(
                    Qx3(123, "abc_${RandomConfigForTest.nextInt()}", 1.23)
                )
            }
        }
    }


    @Test
    fun `randomize 1 generic property`() {

        testGeneratedCodeUsingStandardPlugin(
            """
                $imports

                fun runTest():TestOutput{
                    return withTestOutput{
                        putData(QxC.random<Int>(randomizers={
                            val config=this.randomConfig
                            println(config)
                            constant(config.nextInt())
                        }))
                        putData(QxC.random<Int>(randomizers={
                            add(constantRandomizer(123))
                        }))
                    }
                }
                @Randomizable(randomConfig = RandomConfigForTest::class)
                data class QxC<T1>(override val data:Qx<T1>):WithData
            """,
        ) {
            testCompilation = { result, _ ->
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                val o = result.runRunTest().getObjs()
                o shouldBe listOf(
                    Qx(i = RandomConfigForTest.nextInt()),
                    Qx(i = 123),
                )
                println(o)
            }
        }
    }



    @Test
    fun `randomize 1 generic property with bound - ok case`() {
        testGeneratedCodeUsingStandardPlugin(
            """
                $imports

                fun runTest():TestOutput{
                    return withTestOutput{
                        putData(QxC.random<Int>(randomizers = {
                            constant(-999)
                        }))
                        putData(QxC.random<Float>(randomizers = {
                            constant(-31f)
                        }))
                    }
                }
                @Randomizable(randomConfig = RandomConfigForTest::class)
                data class QxC<T1:Number>(override val data:Qx<T1>):WithData
            """,
        ) {
            testCompilation = { result, _ ->
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                val o = result.runRunTest().getObjs()
                o shouldBe listOf(
                    Qx(i = -999),
                    Qx(i = -31f),
                )
            }
        }
    }

    @Test
    fun `randomize 1 generic property with bound - fail case`() {
        testGeneratedCodeUsingStandardPlugin(
            """
                $imports

                fun runTest():TestOutput{
                    return withTestOutput{
                        putData(QxC.random<String>(randomizers={
                            constant("zzzz")
                        }))
                    }
                }
                @Randomizable(randomConfig = RandomConfigForTest::class)
                data class QxC<T1:Number>(override val data:Qx<T1>):WithData
            """,
        ) {
            testCompilation = { result, _ ->
                result.exitCode shouldBe KotlinCompilation.ExitCode.COMPILATION_ERROR
            }
        }
    }

}
