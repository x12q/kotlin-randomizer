package com.x12q.randomizer

import com.x12q.randomizer.annotations.Randomizer
import com.x12q.randomizer.randomizer.*
import com.x12q.randomizer.test_util.TestSamples
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlin.reflect.KParameter
import kotlin.test.BeforeTest
import kotlin.test.Test


class RandomGenerator_on_param_generic {

    lateinit var rdm: RandomGenerator


    @BeforeTest
    fun bt() {
        rdm = TestSamples.comp.randomizer()
    }

    @Test
    fun lv4() {
        (rdm.random(RDClassData.from<C1<D1>>()) as C1<D1>).t.shouldBeInstanceOf<D1>()
    }

    @Test
    fun lv1() {
        val lv1Rdm = rdm.copy(
            lv1RandomizerCollection = RandomizerCollection(
                classRandomizers = mapOf(
                    RDClassData.from<A1>() to listOf(A1.Randomizer1()),
                ),
                parameterRandomizers = emptyMap()
            )
        )

        (lv1Rdm.random(RDClassData.from<C1<A1>>()) as C1<A1>).t shouldBe A1.Randomizer1().random()
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

        (lv1Randomizer.random(RDClassData.from<C2<A1>>()) as C2<A1>).also {
            it.a1 shouldBe A1.Randomizer1().random()
        }
    }

    @Test
    fun `lv1 over lv3`() {
        (rdm.random(RDClassData.from<C3<A1>>()) as C3<A1>).also {
            it.a1 shouldBe A1.Randomizer3().random()
        }
    }

    @Test
    fun `lv1 over lv4`() {
        shouldNotThrow<Exception> {
            rdm.random(RDClassData.from<C3<D1>>())
        }
    }

    @Test
    fun `lv1 over lv2, lv3, lv4`() {
        val lv1Randomizer = rdm.copy(
            lv1RandomizerCollection = RandomizerCollection(
                classRandomizers = mapOf(
                    RDClassData.from<A1>() to listOf(A1.Randomizer1())
                ),
                parameterRandomizers = emptyMap()
            )
        )

        (lv1Randomizer.random(RDClassData.from<C2<A1>>()) as C2<A1>).a1 shouldBe A1.Randomizer1().random()
    }

    @Test
    fun `lv2 over lv3`() {
        (rdm.random(RDClassData.from<C2<A1>>()) as C2<A1>).a1 shouldBe A1.Randomizer2().random()
    }

    @Test
    fun `lv2 over lv4`() {
        (rdm.random(RDClassData.from<C4<E2>>()) as C4<E2>).a1 shouldBe E2.E2Randomizer2().random()
    }

    @Test
    fun `lv3 over lv4`() {
        (rdm.random(RDClassData.from<C3<A1>>()) as C3<A1>).a1 shouldBe A1.Randomizer3().random()
    }

    class C4<T>(
        @Randomizer(randomizer = E2.E2Randomizer2::class)
        val a1: T,
    )

    class C3<T>(
        val a1: T,
    )

    class C2<T>(
        @Randomizer(randomizer = A1.ParamRandomizer2::class)
        val a1: T,
    )

    class C1<T>(val t: T)

    @Randomizer(randomizer = E3.E3Randomizer3::class)
    interface E


    @Randomizer(randomizer = D1.D1Randomizer3::class) //level3
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

    abstract class A

    @Randomizer(randomizer = A1.Randomizer3::class)
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
