package com.x12q.randomizer

import com.github.michaelbull.result.Ok
import com.x12q.randomizer.randomizer.RDClassData
import com.x12q.randomizer.randomizer.RandomizerCollection
import com.x12q.randomizer.randomizer.ClassRandomizer
import com.x12q.randomizer.randomizer.ParameterRandomizer
import com.x12q.randomizer.test.TestAnnotation
import com.x12q.randomizer.test.TestSamples
import com.x12q.randomizer.test.TestSamples.Class1
import com.x12q.randomizer.test.TestSamples.Class2
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.spyk
import kotlin.reflect.KParameter
import kotlin.test.BeforeTest
import kotlin.test.Test


class Randomizer_End_Default: TestAnnotation() {

    lateinit var rdm0: RandomizerEnd
    lateinit var rdm: RandomizerEnd

    val spyParamRdm = spyk(Class1.tm12FixedRandomizer)
    val classRdm = Class2.classFixedRandomizer

    @BeforeTest
    fun bt() {
        rdm0 = TestSamples.comp.randomizer()
        rdm = rdm0.copy(
            lv1RandomizerCollection = rdm0.lv1RandomizerCollection
                .addParamRandomizer(spyParamRdm)
                .addRandomizers(classRdm)
        )
    }

    /**
     * Verify that a custom rdm is used to generate Class1.tm12
     */
    @Test
    fun randomConstructorParameterRs() {
        val p0 = rdm0.randomConstructorParameterRs(
            Class1.tm12KParam,
            Class1.dt
        )

        val rs = rdm.randomConstructorParameterRs(
            Class1.tm12KParam,
            Class1.dt
        )
        test{
            rs shouldNotBe p0

            rs shouldBe Ok(
                spyParamRdm.random(
                    RDClassData.from<String>(),
                    Class1.tm12KParam,
                    RDClassData.from<Class1>(),
                )
            )

            rdm.randomConstructorParameter(
                Class1.tm12KParam,
                Class1.dt
            ) shouldBe rs.component1()
        }
    }

    /**
     * Verify that custom class randomizer was used instead of the default one.
     */
    @Test
    fun random(){
        val p0 = rdm0.random(Class2.dt)
        val p1 = rdm.random(Class2.dt)
        test{
            p1 shouldNotBe p0
            p1 shouldBe classRdm.random()
        }
    }

    data class B1(
        @Randomizable(randomizer = A1.Randomizer2::class)
        val a:A
    ){
        companion object{

//            val fixed1 = B1(A1("a1"))
//            val fixed2 = B1(A2(A1("a2"),2))
//
//            abstract class BRandomizer0(val rt:B1): ClassRandomizer<B1>{
//                override val targetClassData: RDClassData = RDClassData.from<B1>()
//
//                override fun isApplicable(classData: RDClassData): Boolean {
//                    return classData == this.targetClassData
//                }
//
//                override fun random(): B1 {
//                    return rt
//                }
//            }
//
//            class BRandomizer1():BRandomizer0(fixed1)
//            class BRandomizer2():BRandomizer0(fixed2)
        }
    }

    abstract class A

    data class A2(
        @Randomizable(randomizer = A1.ParamRandomizer2::class)
        val a1:A1,
        val i:Int
    ):A(){
        companion object{
            val fixed1 = A2(A1.fixed1,1)
            val fixed2 = A2(A1.fixed2,2)
            val fixed3 = A2(A1.fixed3,3)
        }

        abstract class A1Randomizer0(val rt:A2): ClassRandomizer<A2> {
            override val targetClassData: RDClassData = RDClassData.from<A2>()

            override fun isApplicable(classData: RDClassData): Boolean {
                return classData == this.targetClassData
            }

            override fun random(): A2 {
                return rt
            }

        }
        class Randomizer1:A1Randomizer0(fixed1)
        class Randomizer2: A1Randomizer0(fixed2)
        class Randomizer3: A1Randomizer0(fixed3)
    }


    @Randomizable(randomizer = A1.Randomizer3::class)
    data class A1(val s:String):A(){

        companion object{
            val fixed1 = A1("1")
            val fixed2 = A1("2")
            val fixed3 = A1("3")
        }

        abstract class A1Randomizer0(val rt:A1): ClassRandomizer<A1> {
            override val targetClassData: RDClassData = RDClassData.from<A1>()

            override fun isApplicable(classData: RDClassData): Boolean {
                return classData == this.targetClassData
            }

            override fun random(): A1 {
                return rt
            }

        }


        class Randomizer1:A1Randomizer0(fixed1)
        class Randomizer2: A1Randomizer0(fixed2)
        class Randomizer3: A1Randomizer0(fixed3)

        abstract class A1ParamRandomizer0 (val rt:A1): ParameterRandomizer<A1> {
            override val paramClassData: RDClassData = RDClassData.from<A1>()

            override fun isApplicableTo(
                parameterClassData: RDClassData,
                parameter: KParameter,
                parentClassData: RDClassData
            ): Boolean {
                return parameterClassData == this.paramClassData
            }

            override fun random(
                parameterClassData: RDClassData,
                parameter: KParameter,
                parentClassData: RDClassData
            ): A1 {
                return rt
            }
        }

        class ParamRandomizer1:A1ParamRandomizer0(fixed1)
        class ParamRandomizer2: A1ParamRandomizer0(fixed2)
        class ParamRandomizer3: A1ParamRandomizer0(fixed3)
    }


}
