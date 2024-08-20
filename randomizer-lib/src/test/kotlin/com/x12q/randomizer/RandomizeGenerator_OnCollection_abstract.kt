package com.x12q.randomizer

import com.x12q.randomizer.annotations.Randomizable
import com.x12q.randomizer.randomizer.*
import com.x12q.randomizer.test_util.TestSamples
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.shouldBe
import kotlin.test.BeforeTest
import kotlin.test.Test

/**
 * Test randomizing List
 */
class RandomizeGenerator_OnCollection_abstract {

    lateinit var rdm: RandomGenerator

    @BeforeTest
    fun bt() {
        rdm = TestSamples.comp.randomizer()
    }

    @Test
    fun `lv1 over lv2, lv3, lv4`() {

        // for collection, level 2+3 are lv3
        val lv1Rdm = rdm.copy(
            lv1RandomizerCollection = RandomizerCollection(
                classRandomizers = mapOf(
                    RDClassData.from<E1>() to listOf(E1.E1Randomizer1()),
                ),
                parameterRandomizers = emptyMap()
            )
        )

        (lv1Rdm.random(RDClassData.from<List<E>>()) as List<E>).also {
            it.shouldNotBeEmpty()
            it.first() shouldBe E1.E1Randomizer1().random()
        }
    }

    @Test
    fun `lv1 over lv4`() {
        shouldThrow<Exception> {
            rdm.random(RDClassData.from<List<A>>())
        }

        val lv1Rdm = rdm.copy(
            lv1RandomizerCollection = RandomizerCollection(
                classRandomizers = mapOf(
                    RDClassData.from<A1>() to listOf(A1.Randomizer1()),
                ),
                parameterRandomizers = emptyMap()
            )
        )

        (lv1Rdm.random(RDClassData.from<List<A>>()) as List<A>).also {
            it.shouldNotBeEmpty()
            it.first() shouldBe A1.Randomizer1().random()
        }
    }

    @Randomizable(randomizer = E3.E3Randomizer3::class)
    interface E


    @Randomizable(randomizer = D1.D1Randomizer3::class) //level3
    interface D

    class D1(val d: String) : D {
        companion object {
            val d1_3 = D1("d1_3")
        }

        class D1Randomizer3 : ClassRandomizer<D1> {
            override val returnedInstanceData: RDClassData = RDClassData.from<D1>()

            override fun isApplicableTo(classData: RDClassData): Boolean {
                return classData == returnedInstanceData
            }

            override fun random(): D1 {
                return d1_3
            }
        }
    }


    class E3(val d: String) : E {
        companion object {
            val e3 = E3("e3")
        }

        class E3Randomizer3 : ClassRandomizer<E3> {
            override val returnedInstanceData: RDClassData = RDClassData.from<E3>()

            override fun isApplicableTo(classData: RDClassData): Boolean {
                return classData == returnedInstanceData
            }

            override fun random(): E3 {
                return e3
            }

        }
    }

    class E1(val d: String) : E {
        companion object {
            val e1 = E1("e1")
        }

        class E1Randomizer1 : ClassRandomizer<E1> {
            override val returnedInstanceData: RDClassData = RDClassData.from<E1>()

            override fun isApplicableTo(classData: RDClassData): Boolean {
                return classData == returnedInstanceData
            }

            override fun random(): E1 {
                return e1
            }

        }

    }

    abstract class A

    @Randomizable(randomizer = A1.Randomizer3::class)
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
