package com.x12q.randomizer

import com.x12q.randomizer.randomizer.RDClassData
import com.x12q.randomizer.randomizer.ClassRandomizer
import com.x12q.randomizer.randomizer.ParameterRandomizer
import com.x12q.randomizer.randomizer.RandomizerCollection
import com.x12q.randomizer.test_util.TestSamples
import com.x12q.randomizer.test_util.TestSamples.Class1
import com.x12q.randomizer.test_util.TestSamples.Class2
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.shouldBe
import io.mockk.spyk
import kotlin.reflect.KParameter
import kotlin.test.BeforeTest
import kotlin.test.Test


class Randomizer_End_OnCollection_concrete {

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
    fun `lv1 over lv2, lv3`() {
        // for collection, level 2+3 are lv3
        val lv1Rdm = rdm.copy(
            lv1RandomizerCollection = RandomizerCollection(
                classRandomizers = mapOf(
                    RDClassData.from<A1>() to A1.Randomizer1(),
                ),
                parameterRandomizers = emptyMap()
            )
        )

        (lv1Rdm.random(RDClassData.from<List<A1>>()) as List<A1>).also {
            it.shouldNotBeEmpty()
            it.first() shouldBe A1.Randomizer1().random()
        }
    }

    @Test
    fun `lv1 over lv4`() {
        // for collection, level 2+3 are lv3
        val lv1Rdm = rdm.copy(
            lv1RandomizerCollection = RandomizerCollection(
                classRandomizers = mapOf(
                    RDClassData.from<A3>() to A3.Rdm1(),
                ),
                parameterRandomizers = emptyMap()
            )
        )

        (lv1Rdm.random(RDClassData.from<List<A3>>()) as List<A3>).also {
            it.shouldNotBeEmpty()
            it.first() shouldBe A3.Rdm1().random()
        }
    }

    @Test
    fun `lv3 over lv4`() {
        (rdm.random(RDClassData.from<List<A1>>()) as List<A1>).also {
            it.shouldNotBeEmpty()
            it.first() shouldBe A1.Randomizer3().random()
        }
    }


    abstract class A

    data class A3(
        val a: String
    ) : A() {

        class Rdm1 : ClassRandomizer<A3> {
            override val returnedInstanceData: RDClassData = RDClassData.from<A3>()

            override fun isApplicableTo(classData: RDClassData): Boolean {
                return classData == returnedInstanceData
            }

            override fun random(): A3 {
                return A3("z")
            }

        }
    }

    data class A2(
        @Randomizable(randomizer = A1.ParamRandomizer2::class)
        val a1: A1,
        val i: Int
    ) : A() {
        companion object {
            val fixed1 = A2(A1.fixed1, 1)
            val fixed2 = A2(A1.fixed2, 2)
            val fixed3 = A2(A1.fixed3, 3)
        }

        abstract class A1Randomizer0(val rt: A2) : ClassRandomizer<A2> {
            override val returnedInstanceData: RDClassData = RDClassData.from<A2>()

            override fun isApplicableTo(classData: RDClassData): Boolean {
                return classData == this.returnedInstanceData
            }

            override fun random(): A2 {
                return rt
            }

        }

        class Randomizer1 : A1Randomizer0(fixed1)
        class Randomizer2 : A1Randomizer0(fixed2)
        class Randomizer3 : A1Randomizer0(fixed3)
    }


    @Randomizable(randomizer = A1.Randomizer3::class)
    data class A1(val s: String) : A() {

        companion object {
            val fixed1 = A1("1")
            val fixed2 = A1("2")
            val fixed3 = A1("3")
        }

        abstract class A1Randomizer0(val rt: A1) : ClassRandomizer<A1> {
            override val returnedInstanceData: RDClassData = RDClassData.from<A1>()

            override fun isApplicableTo(classData: RDClassData): Boolean {
                return classData == this.returnedInstanceData
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
                enclosingClassData: RDClassData
            ): Boolean {
                return parameterClassData == this.paramClassData
            }

            override fun random(
                parameterClassData: RDClassData,
                parameter: KParameter,
                enclosingClassData: RDClassData
            ): A1 {
                return rt
            }
        }

        class ParamRandomizer1 : A1ParamRandomizer0(fixed1)
        class ParamRandomizer2 : A1ParamRandomizer0(fixed2)
        class ParamRandomizer3 : A1ParamRandomizer0(fixed3)
    }


}
