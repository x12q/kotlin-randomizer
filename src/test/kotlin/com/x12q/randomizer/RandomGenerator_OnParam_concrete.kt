package com.x12q.randomizer

import com.x12q.randomizer.randomizer.*
import com.x12q.randomizer.test_util.TestSamples
import com.x12q.randomizer.test_util.TestSamples.Class1
import com.x12q.randomizer.test_util.TestSamples.Class2
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.spyk
import kotlin.reflect.KParameter
import kotlin.test.BeforeTest
import kotlin.test.Test


class RandomGenerator_OnParam_concrete {

    lateinit var rdm0: RandomGenerator
    lateinit var rdm: RandomGenerator

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
    fun `lv1 over lv2`() {
        // lv1 = provided in lv1 collection
        // lv2 = param randomizer
        // lv3 = class randomizer
        // lv4 = default randomizer
        val lv1Randomizer = rdm.copy(
            lv1RandomizerCollection = RandomizerCollection(
                classRandomizers = mapOf(
                    RDClassData.from<E1>() to listOf(E1.E1Randomizer1())
                ),
                parameterRandomizers = emptyMap()
            )
        )

        (lv1Randomizer.random(RDClassData.from<B3>()) as B3).e shouldBe E1.E1Randomizer1().random()

    }

    @Test
    fun `lv1 over lv3`() {
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
        (lv1Randomizer.random(RDClassData.from<B2>()) as B2).a shouldBe A1.Randomizer1().random()
    }

    @Test
    fun `lv1 over lv4`() {
        val lv1Randomizer = rdm.copy(
            lv1RandomizerCollection = RandomizerCollection(
                classRandomizers = mapOf(
                    RDClassData.from<E1>() to listOf(E1.E1Randomizer1())
                ),
                parameterRandomizers = emptyMap()
            )
        )

        // B6, E1
        (lv1Randomizer.random(RDClassData.from<B6>()) as B6).e shouldBe E1.E1Randomizer1().random()
    }

    @Test
    fun `lv1 over lv2, lv3, lv4`() {
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
        (lv1Randomizer.random(RDClassData.from<A2>()) as A2).a1 shouldBe A1.Randomizer1().random()
    }


    @Test
    fun `lv2 over lv3`() {
        // lv1 = provided in lv1 collection
        // lv2 = param randomizer
        // lv3 = class randomizer
        // lv4 = default randomizer
        (rdm.random(RDClassData.from<B4>()) as B4).also {
            it.a1 shouldBe A1.Randomizer2().random()
            it.a2 shouldBe A1.Randomizer2().random()
        }
    }

    @Test
    fun `lv2 over lv4`() {
        // lv1 = provided in lv1 collection
        // lv2 = param randomizer
        // lv3 = class randomizer
        // lv4 = default randomizer
        (rdm.random(RDClassData.from<B5>()) as B5).e2 shouldBe E2.E2Randomizer2().random()
    }

    @Test
    fun `lv2 over lv3, lv4`() {
        // lv1 = provided in lv1 collection
        // lv2 = param randomizer
        // lv3 = class randomizer
        // lv4 = default randomizer
        (rdm.random(RDClassData.from<A2>()) as A2).a1 shouldBe A1.Randomizer2().random()
    }


    @Test
    fun `lv3 over lv4`() {
        (rdm.random(RDClassData.from<A2>()) as A2).a1_2 shouldBe A1.Randomizer3().random()
    }

    @Test
    fun lv4() {
        rdm.random(RDClassData.from<D1>()).shouldBeInstanceOf<D1>()
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

    class E2(val d: String) : E {
        companion object {
            val e2 = E2("e2")
        }

        class E2Randomizer2 : ClassRandomizer<E2> {
            override val returnedInstanceData: RDClassData = RDClassData.from<E2>()

            override fun isApplicableTo(classData: RDClassData): Boolean {
                return classData == returnedInstanceData
            }

            override fun random(): E2 {
                return e2
            }

        }
    }

    class E1(val d: String) : E {
        companion object {
            val e1 = E1("e1")
            val e2 = E1("e2")
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

        class E1Randomizer2 : ClassRandomizer<E1> {
            override val returnedInstanceData: RDClassData = RDClassData.from<E1>()

            override fun isApplicableTo(classData: RDClassData): Boolean {
                return classData == returnedInstanceData
            }

            override fun random(): E1 {
                return e2
            }

        }
    }

    class B6(val e: E1)

    class B5(
        @Randomizable(randomizer = E2.E2Randomizer2::class)
        val e2: E2
    )

    class B4(
        @Randomizable(randomizer = A1.ParamRandomizer2::class)
        val a1: A1,
        @Randomizable(randomizer = A1.Randomizer2::class)
        val a2: A1
    )

    class B3(
        @Randomizable(E1.E1Randomizer2::class)
        val e: E1
    )

    class B2(
        val a: A1
    )

    abstract class A

    data class A2(
        @Randomizable(randomizer = A1.ParamRandomizer2::class)
        val a1: A1,
        val a1_2: A1,
        val i: Int
    ) : A() {
        companion object {
            val fixed1 = A2(A1.fixed1, A1.fixed1, 1)
            val fixed2 = A2(A1.fixed2, A1.fixed2, 2)
            val fixed3 = A2(A1.fixed3, A1.fixed3, 3)
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

        abstract class A1ParamRandomizer0(
            val classRandomizer: ClassRandomizer<A1>
        ) : ParameterRandomizer<A1> {
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
                return classRandomizer.random()
            }
        }

        class ParamRandomizer1 : A1ParamRandomizer0(Randomizer1())
        class ParamRandomizer2 : A1ParamRandomizer0(Randomizer2())
        class ParamRandomizer3 : A1ParamRandomizer0(Randomizer3())
    }


}
