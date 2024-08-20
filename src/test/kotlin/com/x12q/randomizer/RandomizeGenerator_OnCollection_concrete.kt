package com.x12q.randomizer

import com.x12q.randomizer.annotations.Randomizer
import com.x12q.randomizer.randomizer.*
import com.x12q.randomizer.test_util.TestSamples
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.shouldBe
import kotlin.test.BeforeTest
import kotlin.test.Test


class RandomizeGenerator_OnCollection_concrete {

    lateinit var rdm: RandomGenerator

    @BeforeTest
    fun bt() {
        rdm = TestSamples.comp.randomizer()
    }

    @Test
    fun `lv1 over lv2, lv3`() {
        // for collection, level 2+3 are lv3
        val lv1Rdm = rdm.copy(
            lv1RandomizerCollection = RandomizerCollection(
                classRandomizers = mapOf(
                    RDClassData.from<A1>() to listOf(A1.Randomizer1()),
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
                    RDClassData.from<A3>() to listOf(A3.Rdm1()),
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


    @Randomizer(randomizer = A1.Randomizer3::class)
    data class A1(val s: String) : A() {

        companion object {
            val fixed1 = A1("1")
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
        class Randomizer3 : A1Randomizer0(fixed3)
    }
}
