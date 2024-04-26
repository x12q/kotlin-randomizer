package com.x12q.randomizer

import com.x12q.randomizer.RandomizerEnd_OnParam_concrete.A1
import com.x12q.randomizer.randomizer.RDClassData
import com.x12q.randomizer.randomizer.RandomizerCollection
import com.x12q.randomizer.randomizer.ClassRandomizer
import com.x12q.randomizer.randomizer.ParameterRandomizer
import com.x12q.randomizer.test.TestAnnotation
import com.x12q.randomizer.test.TestSamples
import com.x12q.randomizer.test.TestSamples.Class1
import com.x12q.randomizer.test.TestSamples.Class2
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.spyk
import kotlin.reflect.KParameter
import kotlin.test.BeforeTest
import kotlin.test.Test


class RandomizerEnd_OnParam_generic : TestAnnotation() {

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
    fun lv4(){
        (rdm.random(RDClassData.from<C1<D1>>()) as C1<D1>).t.shouldBeInstanceOf<D1>()
    }

    @Test
    fun lv1() {
        val lv1Rdm = rdm.copy(
            lv1RandomizerCollection = RandomizerCollection(
                classRandomizers = mapOf(
                    RDClassData.from<A1>() to A1.Randomizer1(),
                ),
                parameterRandomizers = emptyMap()
            )
        )

        (lv1Rdm.random(RDClassData.from<C1<A1>>()) as C1<A1>).t shouldBe A1.Randomizer1().random()
    }

    @Test
    fun `lv1 over lv2`(){
        val lv1Randomizer = rdm.copy(
            lv1RandomizerCollection = RandomizerCollection(
                classRandomizers = mapOf(
                    RDClassData.from<A1>() to A1.Randomizer1()
                ),
                parameterRandomizers = emptyMap()
            )
        )

        (lv1Randomizer.random(RDClassData.from<C2<A1>>()) as C2<A1>).also {
            it.a1 shouldBe A1.Randomizer1().random()
        }
    }

    @Test
    fun `lv1 over lv3`(){
        (rdm.random(RDClassData.from<C3<A1>>()) as C3<A1>).also {
            it.a1 shouldBe A1.Randomizer3().random()
        }
    }

    @Test
    fun `lv1 over lv4`(){
        shouldNotThrow<Exception> {
            rdm.random(RDClassData.from<C3<D1>>())
        }
    }

    @Test
    fun `lv1 over lv2, lv3, lv4`(){
        val lv1Randomizer = rdm.copy(
            lv1RandomizerCollection = RandomizerCollection(
                classRandomizers = mapOf(
                    RDClassData.from<A1>() to A1.Randomizer1()
                ),
                parameterRandomizers = emptyMap()
            )
        )

        (lv1Randomizer.random(RDClassData.from<C2<A1>>()) as C2<A1>).a1 shouldBe A1.Randomizer1().random()
    }

    @Test
    fun `lv2 over lv3`(){
        (rdm.random(RDClassData.from<C2<A1>>()) as C2<A1>).a1 shouldBe A1.Randomizer2().random()
    }

    @Test
    fun `lv2 over lv4`(){
        (rdm.random(RDClassData.from<C4<E2>>()) as C4<E2>).a1 shouldBe E2.E2Randomizer2().random()
    }

    @Test
    fun `lv3 over lv4`(){
        (rdm.random(RDClassData.from<C3<A1>>()) as C3<A1>).a1 shouldBe A1.Randomizer3().random()
    }

    class C4<T>(
        @Randomizable(randomizer = E2.E2Randomizer2::class)
        val a1:T,
    )

    class C3<T>(
        val a1:T,
    )

    class C2<T>(
        @Randomizable(randomizer = A1.ParamRandomizer2::class)
        val a1:T,
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
            override val targetClassData: RDClassData = RDClassData.from<D1>()

            override fun isApplicable(classData: RDClassData): Boolean {
                return classData == targetClassData
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
            override val targetClassData: RDClassData = RDClassData.from<E3>()

            override fun isApplicable(classData: RDClassData): Boolean {
                return classData == targetClassData
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
            override val targetClassData: RDClassData = RDClassData.from<E2>()

            override fun isApplicable(classData: RDClassData): Boolean {
                return classData == targetClassData
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
            override val targetClassData: RDClassData = RDClassData.from<E1>()

            override fun isApplicable(classData: RDClassData): Boolean {
                return classData == targetClassData
            }

            override fun random(): E1 {
                return e1
            }

        }
    }



    data class B1(
        @Randomizable(randomizer = A1.ParamRandomizer2::class)
        val A: A,
    ) {
        companion object {

            val fixed1 = B1(
                A = A1(s = "a1"),
            )

            val fixed2 = B1(
                A = A2(
                    a1 = A1("a2"),
                    a1_2 = A1("a2_2"),
                    i = 2
                ),
            )

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


    @Randomizable(randomizer = A1.Randomizer3::class)
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

        abstract class A1ParamRandomizer0(
            val classRandomizer: ClassRandomizer<A1>
        ) : ParameterRandomizer<A1> {
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
                return classRandomizer.random()
            }
        }

        class ParamRandomizer1 : A1ParamRandomizer0(Randomizer1())
        class ParamRandomizer2 : A1ParamRandomizer0(Randomizer2())
        class ParamRandomizer3 : A1ParamRandomizer0(Randomizer3())
    }


}
