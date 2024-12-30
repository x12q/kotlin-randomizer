package com.x12q.kotlin.randomizer.ir_plugin

import com.tschuchort.compiletesting.KotlinCompilation
import com.x12q.kotlin.randomizer.ir_plugin.mock_objects.AlwaysTrueRandomConfig
import com.x12q.kotlin.randomizer.ir_plugin.mock_objects.LegalRandomConfigObject
import com.x12q.kotlin.randomizer.ir_plugin.mock_objects.RandomConfigForTest
import com.x12q.kotlin.randomizer.test.util.assertions.executeRunTestFunction
import com.x12q.kotlin.randomizer.test.util.test_code.TestImportsBuilder
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import kotlin.test.Test


@OptIn(ExperimentalCompilerApi::class)
class TestRandomGenericProperty {

    data class Qx<T1>(val i: T1?)
    data class Qx2<Q2T>(val paramOfQ2: Q2T)
    data class Qx4<Q4T>(val paramOfQ4: Q4T)
    data class Qx6<H>(val paramOfQ6: H)
    data class Qx3<T1, T2, T3>(val i1: T1, val i2: T2, val i3: T3)
    data class Qx3Swapped<T1, T2, T3>(val i2: T2, val i1: T1, val i3: T3)
    data class TwoGeneric<G1, G2>(val g1: G1, val g2: G2)
    data class ThreeGeneric<G1, G2, G3>(val g1: G1, val g2: G2, val g3: G3)
    data class QxList<TL>(val listT: List<TL>)

    private val imports = TestImportsBuilder.stdImport
        .import(Qx::class)
        .import(Qx2::class)
        .import(Qx3::class)
        .import(Qx4::class)
        .import(Qx6::class)
        .import(TwoGeneric::class)
        .import(ThreeGeneric::class)
        .import(QxList::class)
        .import(Qx3Swapped::class)

    val size = LegalRandomConfigObject.randomCollectionSize()
    val int = LegalRandomConfigObject.nextInt()
    val float = LegalRandomConfigObject.nextFloat()
    val str = LegalRandomConfigObject.nextString()
    val double = LegalRandomConfigObject.nextDouble()
    val short = LegalRandomConfigObject.nextShort()


    /**
     * A test in which a class is used multiple times in a generic declaration and nest itself in that.
     */
    @Test
    fun `repeat multi generic with NO randomizers lambda`() {
        testGeneratedCodeUsingStandardPlugin(
            """
                $imports

                data class QxC<T1:Any>(override val data:T1):WithData

                fun runTest():TestOutput {
                    return withTestOutput{
                        putData(random<QxC<Qx2<Qx2<String>>>>(randomConfig=LegalRandomConfigObject))
                        // putData(random<QxC<Qx2<Qx2<Qx2<String>>>>>(randomConfig=LegalRandomConfigObject))
                        // putData(random<QxC<Qx2<Qx2<Qx2<Qx4<Qx2<String>>>>>>>(randomConfig=LegalRandomConfigObject))
                    }
                }
            """,
        ) {
            testCompilation = { result, _ ->
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                val objectList = result.executeRunTestFunction().getObjs()
                objectList shouldBe listOf(
                    Qx2(Qx2(LegalRandomConfigObject.nextString())),
                    // Qx2(Qx2(Qx2(LegalRandomConfigObject.nextString()))),
                    // Qx2(Qx2(Qx2(Qx4(Qx2(LegalRandomConfigObject.nextString()))))),
                )
            }
        }
    }

    @Test
    fun `multi generic with NO randomizers lambda`() {
        testGeneratedCodeUsingStandardPlugin(
            """
                $imports

                data class QxC<T1:Any>(override val data:T1):WithData

                fun runTest():TestOutput {
                    return withTestOutput{
                        putData(random<QxC<Qx2<Qx2<String>>>>(randomConfig=LegalRandomConfigObject))
                        putData(random<QxC<TwoGeneric<Qx2<Int>,Qx4<String>>>>(randomConfig=LegalRandomConfigObject))
                        putData(random<QxC<ThreeGeneric<Qx2<Int>,Qx4<String>,Float>>>(randomConfig=LegalRandomConfigObject))
                        putData(random<QxC<ThreeGeneric<Double,String,Qx4<Short>>>>(randomConfig=LegalRandomConfigObject))
                        putData(random<QxC<ThreeGeneric<Qx2<Int>,Qx4<String>,Qx4<Qx2<Qx4<Int>>>>>>(randomConfig=LegalRandomConfigObject))
                        putData(random<QxC<TwoGeneric<Double,String>>>(randomConfig=LegalRandomConfigObject))
                        
                        
                    }
                }
            """,
        ) {
            testCompilation = { result, _ ->
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                val objectList = result.executeRunTestFunction().getObjs()
                objectList shouldBe listOf(
                    Qx2(Qx2(LegalRandomConfigObject.nextString())),
                    TwoGeneric(Qx2(int), Qx4(LegalRandomConfigObject.nextString())),
                    ThreeGeneric(Qx2(int), Qx4(str), float),
                    ThreeGeneric(double, str, Qx4(LegalRandomConfigObject.nextShort())),
                    ThreeGeneric(Qx2(int), Qx4(str), Qx4(Qx2(Qx4(int)))),
                    TwoGeneric(double, str),
                )
            }
        }
    }

    @Test
    fun `multi generic with empty randomizers `() {
        testGeneratedCodeUsingStandardPlugin(
            """
                $imports

                data class QxC<T1:Any>(override val data:T1):WithData

                fun runTest():TestOutput {
                    return withTestOutput{
                        putData(random<QxC<TwoGeneric<Qx2<Int>,Qx4<String>>>>(randomConfig=LegalRandomConfigObject,randomizers={}))
                        putData(random<QxC<ThreeGeneric<Qx2<Int>,Qx4<String>,Float>>>(randomConfig=LegalRandomConfigObject,randomizers={}))
                        putData(random<QxC<ThreeGeneric<Double,String,Qx4<Short>>>>(randomConfig=LegalRandomConfigObject,randomizers={}))
                        putData(random<QxC<ThreeGeneric<Qx2<Int>,Qx4<String>,Qx4<Qx2<Qx4<Int>>>>>>(randomConfig=LegalRandomConfigObject,randomizers={}))
                        putData(random<QxC<TwoGeneric<Double,String>>>(randomConfig=LegalRandomConfigObject,randomizers={}))
                        
                    }
                }
            """,
        ) {
            testCompilation = { result, _ ->
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                val objectList = result.executeRunTestFunction().getObjs()

                objectList shouldBe listOf(
                    TwoGeneric(Qx2(int), Qx4(str)),
                    ThreeGeneric(Qx2(int), Qx4(str), float),
                    ThreeGeneric(double, str, Qx4(short)),
                    ThreeGeneric(Qx2(int), Qx4(str), Qx4(Qx2(Qx4(int)))),
                    TwoGeneric(double, str),
                )
            }
        }
    }

    @Test
    fun `one generic WITH NO custom randomizers`() {
        testGeneratedCodeUsingStandardPlugin(
            """
                $imports

                data class QxC<T1:Any>(override val data:T1):WithData

                fun runTest():TestOutput {
                    return withTestOutput{
                        putData(random<QxC<Qx2<Qx4<Int>>>>(randomConfig=LegalRandomConfigObject))
                        putData(random<QxC<Qx2<Qx4<Float>>>>(randomConfig=LegalRandomConfigObject))
                        putData(random<QxC<Qx2<Qx4<String>>>>(randomConfig=LegalRandomConfigObject))
                        putData(random<QxC<Qx2<Int>>>(randomConfig=LegalRandomConfigObject, randomizers={}))
                        putData(random<QxC<Qx2<Boolean>>>(randomConfig=LegalRandomConfigObject, randomizers={}))
                        
                    }
                }
            """,
        ) {
            testCompilation = { result, _ ->
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                val objectList = result.executeRunTestFunction().getObjs()

                objectList shouldBe listOf(
                    Qx2(Qx4(LegalRandomConfigObject.nextInt())),
                    Qx2(Qx4(LegalRandomConfigObject.nextFloat())),
                    Qx2(Qx4(LegalRandomConfigObject.nextString())),
                    Qx2(LegalRandomConfigObject.nextInt()),
                    Qx2(LegalRandomConfigObject.nextBoolean()),
                )
            }
        }
    }


    @Test
    fun `one generic with empty randomizers `() {
        testGeneratedCodeUsingStandardPlugin(
            """
                $imports

                data class QxC<T1:Any>(override val data:T1):WithData

                fun runTest():TestOutput {
                    return withTestOutput{
                        putData(random<QxC<Qx2<Qx4<Int>>>>(randomConfig=LegalRandomConfigObject, randomizers={}))
                        putData(random<QxC<Qx2<Qx4<Float>>>>(randomConfig=LegalRandomConfigObject, randomizers={}))
                        putData(random<QxC<Qx2<Qx4<String>>>>(randomConfig=LegalRandomConfigObject, randomizers={}))
                        putData(random<QxC<Qx2<Int>>>(randomConfig=LegalRandomConfigObject, randomizers={}))
                        putData(random<QxC<Qx2<Boolean>>>(randomConfig=LegalRandomConfigObject, randomizers={}))
                    }
                }
            """,
        ) {
            testCompilation = { result, _ ->
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                val objectList = result.executeRunTestFunction().getObjs()

                objectList shouldBe listOf(
                    Qx2(Qx4(LegalRandomConfigObject.nextInt())),
                    Qx2(Qx4(LegalRandomConfigObject.nextFloat())),
                    Qx2(Qx4(LegalRandomConfigObject.nextString())),
                    Qx2(LegalRandomConfigObject.nextInt()),
                    Qx2(LegalRandomConfigObject.nextBoolean()),
                )
            }
        }
    }


    /**
     * Test custom "randomizers" that calls a generated "random" function of another class.
     */
    @Test
    fun `complex class as generic with custom randomizers 2`() {
        testGeneratedCodeUsingStandardPlugin(
            """
               $imports

                data class Qx2x<Z>(val paramOfQ2x: Z)

                data class QxC<T1:Any>(override val data:T1):WithData

                fun runTest():TestOutput{
                    return withTestOutput{
                        putData(random<QxC<Qx2x<Int>>>(randomConfig = LegalRandomConfigObject, randomizers = {
                            val rdm = constantRandomizer(random<Qx2x<Int>>(randomConfig=randomConfig))
                            add(rdm)
                        }))
                    }
                }
            """,
        ) {
            testCompilation = { result, _ ->
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                val objectList = result.executeRunTestFunction().getObjs()
                objectList.toString() shouldBe "[Qx2x(paramOfQ2x=3)]"
            }
        }
    }

    @Test
    fun `complex class as generic with custom randomizer`() {
        testGeneratedCodeUsingStandardPlugin(
            """
                $imports

                data class Qx2x<Z>(val paramOfQ2x: Z)

                data class QxC<T1:Any>(override val data:T1):WithData

                fun runTest():TestOutput{
                    return withTestOutput{
                        putData(random<QxC<Qx2x<Int>>>(randomConfig=LegalRandomConfigObject ,randomizers={
                            constant(random<Qx2x<Int>>(randomConfig=randomConfig))
                        }))
                    }
                }
            """,
        ) {
            testCompilation = { result, _ ->
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                val objectList = result.executeRunTestFunction().getObjs()
                objectList.toString() shouldBe "[Qx2x(paramOfQ2x=3)]"
            }
        }
    }


    @Test
    fun `3 layers of multiple nested generic`() {
        // RandomCon
        testGeneratedCodeUsingStandardPlugin(
            """
               $imports

                data class QxC<T1,T2>(override val data: TwoGeneric<Qx2<Qx4<T1>>,Qx4<Qx6<T2>>>):WithData

                fun runTest():TestOutput{
                    return withTestOutput{
                        putData(random<QxC<Int,Float>>(randomizers = {
                            constant(123)
                            constant(-9.45f)
                        }))
                        putData(random<QxC<Int,Float>>())
                    }
                }
            """,
        ) {
            testCompilation = { result, _ ->
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                val objectList = result.executeRunTestFunction().getObjs()
                objectList.size shouldBe 2
                objectList[0] shouldBe TwoGeneric(
                    g1 = Qx2(Qx4(123)),
                    g2 = Qx4(Qx6(-9.45f)),
                )
                objectList[1].shouldBeInstanceOf<TwoGeneric<Qx2<Qx4<Int>>, Qx4<Qx6<Float>>>>()
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
                        putData(random<QxC<Int?>>(randomConfig=NullRandomConfig))
                    }
                }
                data class QxC<T1>(override val data:Qx2<Qx<T1>>):WithData
            """,
        ) {
            testCompilation = { result, _ ->
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                val objectList = result.executeRunTestFunction().getObjs()
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
                
                data class QxC<T1>(override val data:Qx2<Qx4<T1>>):WithData
                
                fun runTest():TestOutput{
                    return withTestOutput{
                        putData(random<QxC<Int>>(randomizers = {
                            constant(123)
                        }))
                    }
                }
            """,
        ) {
            testCompilation = { result, _ ->
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                val objectList = result.executeRunTestFunction().getObjs()
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
                        putData(random<QxC<Int>>(randomizers = {
                            constant(123)
                        }))
                    }
                }
                
                data class QxC<T1>(override val data:Qx2<Qx4<Qx6<T1>>>):WithData
            """,
        ) {
            testCompilation = { result, _ ->
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                val objectList = result.executeRunTestFunction().getObjs()
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
                        putData(random<QxC<String>>(randomConfig=LegalRandomConfigObject))
                    }
                }
                
                data class QxC<T1>(override val data:Qx2<T1>):WithData
            """,
        ) {
            testCompilation = { result, _ ->
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                result.executeRunTestFunction().getObjs() shouldBe listOf(
                    Qx2(LegalRandomConfigObject.nextString()),
                )
            }
        }
    }


    @Test
    fun `custom primitive generic param`() {

        val customRandomInt = -99

        testGeneratedCodeUsingStandardPlugin(
            """
                $imports

                fun runTest():TestOutput{
                    return withTestOutput{
                        putData(random<QxC<Int>>(randomConfig=LegalRandomConfigObject, randomizers = {
                            val rdm = ConstantRandomizer<Int>(-99,TypeKey.of<Int>())
                            add(rdm)
                        }))
                    }
                }
                
                data class QxC<T1>(override val data:Qx2<T1>):WithData
            """,
        ) {
            testCompilation = { result, _ ->
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                result.executeRunTestFunction().getObjs() shouldBe listOf(
                    Qx2(customRandomInt),
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
                        putData(random<QxC<Int?>>(randomConfig=AlwaysFalseRandomConfig, randomizers={
                            factory{randomConfig.nextInt()}
                        }))
                        putData(random<QxC<Int?>>(randomConfig=AlwaysTrueRandomConfig, randomizers={
                            factory{randomConfig.nextInt()}
                        }))
                    }
                }
                data class QxC<T1>(override val data:Qx<T1>):WithData
            """,
        ) {
            testCompilation = { result, testStream ->
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                val l = result.executeRunTestFunction().getObjs()
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

                data class QxC<T1>(override val data:Qx2<T1>):WithData

                fun runTest():TestOutput{
                    return withTestOutput{
                        putData(random<QxC<Int>>(randomConfig=LegalRandomConfigObject, randomizers = {
                            factory{randomConfig.nextInt()}
                        }))
                        putData(random<QxC<Int>>(randomConfig=LegalRandomConfigObject, randomizers = {
                            factory{randomConfig.nextInt()}
                        }))
                    }
                }
            """,
        ) {
            testCompilation = { result, _ ->
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                result.executeRunTestFunction().getObjs() shouldBe listOf(
                    Qx2(LegalRandomConfigObject.nextInt()),
                    Qx2(LegalRandomConfigObject.nextInt())
                )
            }
        }
    }

    @Test
    fun `randomize 3 generic property`() {

        testGeneratedCodeUsingStandardPlugin(
            """
                $imports

                fun runTest():TestOutput{
                    return withTestOutput{
                        putData(random<QxC<Int,String,Double>>(
                                randomConfig=RandomConfigForTest,
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
                result.executeRunTestFunction().getObjs() shouldBe listOf(
                    Qx3(123, "abc_${RandomConfigForTest.nextInt()}", 1.23)
                )
            }
        }
    }


    @Test
    fun `randomize 3 generic property _another_`() {

        testGeneratedCodeUsingStandardPlugin(
            """
                $imports

                fun runTest():TestOutput{
                    return withTestOutput{
                        putData(random<QxC<Int,String,Double>>(
                                randomConfig=RandomConfigForTest,
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
                
                data class QxC<T1,T2,T3>(override val data:Qx3Swapped<T1,T2,T3>):WithData{
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
                result.executeRunTestFunction().getObjs() shouldBe listOf(
                    Qx3Swapped("abc_${RandomConfigForTest.nextInt()}", 123, 1.23)
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
                        putData(random<QxC<Int>>(
                            randomConfig=RandomConfigForTest,
                            randomizers={
                            val config=this.randomConfig
                            println(config)
                            constant(config.nextInt()+1)
                        }))
                        putData(random<QxC<Int>>(
                            randomConfig=RandomConfigForTest,
                            randomizers={
                            add(constantRandomizer(123))
                        }))
                    }
                }
                
                data class QxC<T1>(override val data:Qx<T1>):WithData
            """,
        ) {
            testCompilation = { result, _ ->
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                val o = result.executeRunTestFunction().getObjs()
                o shouldBe listOf(
                    Qx(i = RandomConfigForTest.nextInt() + 1),
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
                        putData(random<QxC<Int>>(randomConfig=RandomConfigForTest, randomizers = {
                            constant(-999)
                        }))
                        putData(random<QxC<Float>>(randomConfig=RandomConfigForTest, randomizers = {
                            constant(-31f)
                        }))
                    }
                }
                
                data class QxC<T1:Number>(override val data:Qx<T1>):WithData
            """,
        ) {
            testCompilation = { result, _ ->
                result.exitCode shouldBe KotlinCompilation.ExitCode.OK
                val o = result.executeRunTestFunction().getObjs()
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
                
                
                data class QxC<T1:Number>(override val data:Qx<T1>):WithData            

                fun runTest():TestOutput{
                    return withTestOutput{
                        putData(random<QxC<String>(randomConfig=RandomConfigForTest, randomizers={
                            constant("zzzz")
                        }))
                    }
                }
            """,
        ) {
            testCompilation = { result, _ ->
                result.exitCode shouldBe KotlinCompilation.ExitCode.COMPILATION_ERROR
            }
        }
    }

}
