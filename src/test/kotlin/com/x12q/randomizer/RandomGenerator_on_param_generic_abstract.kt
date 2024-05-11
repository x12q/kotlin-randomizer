package com.x12q.randomizer

import com.x12q.randomizer.randomizer.*
import com.x12q.randomizer.test_util.TestSamples
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import kotlin.reflect.KParameter
import kotlin.test.BeforeTest
import kotlin.test.Test

/**
 * Test on generic parameter that is abstract (abstract class or interface)
 */
class RandomGenerator_on_param_generic_abstract {

    lateinit var rdm: RandomGenerator


    @BeforeTest
    fun bt() {
        rdm = TestSamples.comp.randomizer()
    }

    @Test
    fun lv4() {
        shouldThrow<IllegalArgumentException> {
            rdm.random(RDClassData.from<C1<F>>())
        }
    }

    @Test
    fun lv1() {
        val lv1Rdm = rdm.copy(
            lv1RandomizerCollection = RandomizerCollection(
                classRandomizers = mapOf(
                    RDClassData.from<E1>() to listOf(E1.E1Randomizer1()),
                ),
                parameterRandomizers = emptyMap()
            )
        )
        (lv1Rdm.random(RDClassData.from<C3<E>>()) as C3<E>).m shouldBe E1.E1Randomizer1().random()
    }

    @Test
    fun `lv1 over lv2`() {
        val lv1Randomizer = rdm.copy(
            lv1RandomizerCollection = RandomizerCollection(
                classRandomizers = mapOf(
                    RDClassData.from<A1>() to listOf(A1.Randomizer1())
                ),
                parameterRandomizers = emptyMap()
            )
        )

        (lv1Randomizer.random(RDClassData.from<C2<A>>()) as C2<A1>).also {
            it.m shouldBe A1.Randomizer1().random()
        }
    }

    @Test
    fun `lv1 over lv3`() {
        val lv1Randomizer = rdm.copy(
            lv1RandomizerCollection = RandomizerCollection(
                classRandomizers = mapOf(
                    RDClassData.from<E1>() to listOf(E1.E1Randomizer1())
                ),
                parameterRandomizers = emptyMap()
            )
        )

        (lv1Randomizer.random(
            RDClassData.from<C3<E>>()
        ) as C3<E>).m shouldBe E1.E1Randomizer1().random()
    }

    @Test
    fun `lv1 over lv4`() {

        shouldThrow<IllegalArgumentException> {
            rdm.random(RDClassData.from<C3<A>>())
        }

        val lv1Randomizer = rdm.copy(
            lv1RandomizerCollection = RandomizerCollection(
                classRandomizers = mapOf(
                    RDClassData.from<A1>() to listOf(A1.Randomizer1())
                ),
                parameterRandomizers = emptyMap()
            )
        )

        (lv1Randomizer.random(
            RDClassData.from<C3<A>>()
        ) as C3<A>).m shouldBe A1.Randomizer1().random()
    }

    @Test
    fun `lv1 over lv2, lv3, lv4`() {
        val lv1Randomizer = rdm.copy(
            lv1RandomizerCollection = RandomizerCollection(
                classRandomizers = mapOf(
                    RDClassData.from<E1>() to listOf(E1.E1Randomizer1())
                ),
                parameterRandomizers = emptyMap()
            )
        )

        (lv1Randomizer.random(RDClassData.from<B<E>>()) as B<E>).m shouldBe E1.E1Randomizer1().random()

    }

    @Test
    fun `lv2 over lv3`() {
        (rdm.random(RDClassData.from<B<E>>()) as B<E>).m shouldBe E2.E2Randomizer2().random()
    }

    @Test
    fun `lv2 over lv4`() {
        (rdm.random(RDClassData.from<C2<A>>()) as C2<A>).m shouldBe A1.Randomizer2().random()
    }

    @Test
    fun `lv2 over lv3, lv4`() {
        (rdm.random(RDClassData.from<B<E>>()) as B<E>).m shouldBe E2.E2Randomizer2().random()
    }


    @Test
    fun `lv3 over lv4`() {
        (rdm.random(RDClassData.from<C3<E>>()) as C3<E>).m shouldBe E3.E3Randomizer3().random()
    }

    class B<T>(
        @Randomizable(randomizer = E2.E2Randomizer2::class)
        val m: T
    )

    interface F

    class C3<T>(
        val m: T,
    )

    class C2<T>(
        @Randomizable(randomizer = A1.ParamRandomizer2::class)
        val m: T,
    )

    class C1<T>(val t: T)

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

        class ParamRandomizer2 : A1ParamRandomizer0(Randomizer2())
    }


}
