package com.x12q.randomizer

import com.x12q.randomizer.randomizer.RDClassData
import com.x12q.randomizer.randomizer.RandomizerCollection
import com.x12q.randomizer.randomizer.class_randomizer.ClassRandomizer
import com.x12q.randomizer.randomizer.parameter.ParameterRandomizer
import com.x12q.randomizer.test.TestAnnotation
import com.x12q.randomizer.test.TestSamples
import com.x12q.randomizer.test.TestSamples.Class1
import com.x12q.randomizer.test.TestSamples.Class2
import io.kotest.matchers.shouldBe
import io.mockk.spyk
import kotlin.reflect.KParameter
import kotlin.test.BeforeTest
import kotlin.test.Test


class Randomizer_End_OnParam : TestAnnotation() {

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

    @Test
    fun `lv1 overriding all other randomizer`() {
        test("lv1 overriding lv2 on param A1 in A2") {
            val lv1Randomizer = rdm.copy(
                lv1RandomizerCollection = RandomizerCollection(
                    classRandomizers = mapOf(
                        RDClassData.from<A1>() to A1.Randomizer1(),
                    ),
                    parameterRandomizers = emptyMap()
                )
            )
            (lv1Randomizer.random(RDClassData.from<A2>()) as A2).a1 shouldBe A1.Randomizer1().random()
        }
    }

    @Test
    fun `lv1 over lv2 on abstract param`(){
        test("lv1 overriding lv2 on param A in B1") {
            val lv1Randomizer = rdm.copy(
                lv1RandomizerCollection = RandomizerCollection(
                    classRandomizers = mapOf(
                        RDClassData.from<A1>() to A1.Randomizer1(),
                    ),
                    parameterRandomizers = emptyMap()
                )
            )
            (lv1Randomizer.random(RDClassData.from<B1>()) as B1).a shouldBe A1.Randomizer1().random()
        }
    }

    @Test
    fun `lv2 over lv4 on abstract param`(){
        test("lv1 overriding lv2 on param A in B1") {
            (rdm.random(RDClassData.from<B1>()) as B1).a shouldBe A1.fixed2
        }
    }


    data class B1(
        @Randomizable(paramRandomizer = A1.ParamRandomizer2::class)
        val a: A
    ) {
        companion object {

            val fixed1 = B1(A1("a1"))
            val fixed2 = B1(A2(A1("a2"), 2))

            abstract class BRandomizer0(val rt: B1) : ClassRandomizer<B1> {
                override val targetClassData: RDClassData = RDClassData.from<B1>()

                override fun isApplicable(classData: RDClassData): Boolean {
                    return classData == this.targetClassData
                }

                override fun random(): B1 {
                    return rt
                }
            }

            class BRandomizer1() : BRandomizer0(fixed1)
            class BRandomizer2() : BRandomizer0(fixed2)
        }
    }

    abstract class A

    data class A2(
        @Randomizable(paramRandomizer = A1.ParamRandomizer2::class)
        val a1: A1,
        val i: Int
    ) : A() {
        companion object {
            val fixed1 = A2(A1.fixed1, 1)
            val fixed2 = A2(A1.fixed2, 2)
            val fixed3 = A2(A1.fixed3, 3)
        }

        abstract class A1Randomizer0(val rt: A2) : ClassRandomizer<A2> {
            override val targetClassData: RDClassData = RDClassData.from<A2>()

            override fun isApplicable(classData: RDClassData): Boolean {
                return classData == this.targetClassData
            }

            override fun random(): A2 {
                return rt
            }

        }

        class Randomizer1 : A1Randomizer0(fixed1)
        class Randomizer2 : A1Randomizer0(fixed2)
        class Randomizer3 : A1Randomizer0(fixed3)
    }


    @Randomizable(classRandomizer = A1.Randomizer3::class)
    data class A1(val s: String) : A() {

        companion object {
            val fixed1 = A1("1")
            val fixed2 = A1("2")
            val fixed3 = A1("3")
        }

        abstract class A1Randomizer0(val rt: A1) : ClassRandomizer<A1> {
            override val targetClassData: RDClassData = RDClassData.from<A1>()

            override fun isApplicable(classData: RDClassData): Boolean {
                return classData == this.targetClassData
            }

            override fun random(): A1 {
                return rt
            }

        }


        class Randomizer1 : A1Randomizer0(fixed1)
        class Randomizer2 : A1Randomizer0(fixed2)
        class Randomizer3 : A1Randomizer0(fixed3)

        abstract class A1ParamRandomizer0(val rt: A1) : ParameterRandomizer<A1> {
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

        class ParamRandomizer1 : A1ParamRandomizer0(fixed1)
        class ParamRandomizer2 : A1ParamRandomizer0(fixed2)
        class ParamRandomizer3 : A1ParamRandomizer0(fixed3)
    }


}
