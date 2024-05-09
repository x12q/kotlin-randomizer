package com.x12q.randomizer

import com.x12q.randomizer.lookup_node.RDClassData
import com.x12q.randomizer.randomizer.*
import com.x12q.randomizer.test_util.TestSamples
import io.kotest.matchers.shouldBe
import kotlin.reflect.KParameter
import kotlin.test.BeforeTest
import kotlin.test.Test


class RandomizeGenerator_class {

    lateinit var rdm: RandomGenerator

    @BeforeTest
    fun bt() {
        rdm = TestSamples.comp.randomizer()
    }


    @Test
    fun `lv1 over lv3, lv4`() {
        // lv1 = provided in lv1 collection
        // lv2 = param randomizer
        // lv3 = class randomizer
        // lv4 = default randomizer
        val lv1Randomizer = rdm.copy(
            lv1RandomizerCollection = RandomizerCollection(
                classRandomizers = mapOf(
                    RDClassData.from<A1>() to listOf(A1.Randomizer1()),
                ),
                parameterRandomizers = emptyMap()
            )
        )

        lv1Randomizer.random(RDClassData.from<A1>(), lv2RandomizerClassLz = lazy{A1.Randomizer2()}, rdChain = null, typeFinder = null, enclosingClass = null) shouldBe A1.Randomizer1()
            .random()
    }

    @Test
    fun `lv3 over level 4`() {
        // lv1 = provided in lv1 collection
        // lv2 = param randomizer
        // lv3 = class randomizer
        // lv4 = default randomizer

        rdm.random(RDClassData.from<A1>()) shouldBe A1.Randomizer3().random()

    }

    abstract class A

    data class A2(
        @Randomizable(randomizer = A1.ParamRandomizer2::class)
        val a1: A1,
        val i: Int
    ) : A()

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
                paramInfo: ParamInfo
            ): Boolean {
                return paramInfo.paramClassData == this.paramClassData
            }

            override fun random(
                parameterClassData: RDClassData,
                parameter: KParameter,
                enclosingClassData: RDClassData
            ): A1 {
                return rt
            }
        }

        class ParamRandomizer2 : A1ParamRandomizer0(fixed2)
    }


}
