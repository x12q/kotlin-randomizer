package com.x12q.randomizer

import com.x12q.randomizer.randomizer.ClassRandomizer
import com.x12q.randomizer.randomizer.RDClassData
import com.x12q.randomizer.randomizer.clazz.SameClassRandomizer
import com.x12q.randomizer.test_util.TestSamples
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlin.test.BeforeTest
import kotlin.test.Test

class RandomizeGenerator_Sealed_Class {
    lateinit var rdm: RandomGenerator

    @BeforeTest
    fun bt() {
        rdm = TestSamples.comp.randomizer()
    }

    sealed class S1 {
        object C1 : S1()
        object C2 : S1()
        class C3(val i: Int) : S1()
        class C4(val i: String) : S1()
        class C5<T>(val i:T):S1()
    }

    @Test
    fun `random on no annotation`(){
        shouldNotThrow<Throwable> {
            val rs = rdm.random(RDClassData.from<S1>())
            rs.shouldNotBeNull()
            rs.shouldBeInstanceOf<S1>()
        }
    }

    @Randomizable(randomizer = S2.Companion.Rdm1::class)
    sealed class S2 {
        object C1 : S2()
        object C2 : S2()
        class C3(val i: Int) : S2()

        companion object {
            val c3_1 = C3(123)
            class Rdm1(
                val r: SameClassRandomizer<S2.C3> = SameClassRandomizer<S2.C3>(
                    returnedInstanceData = RDClassData.from<S2.C3>(),
                    makeRandom = {
                        c3_1
                    }
                )
            ) : ClassRandomizer<S2.C3> by r
        }
    }

    @Test
    fun `random on sealed class with annotation`(){
        shouldNotThrow<Throwable> {
            rdm.random(RDClassData.from<S2>()) shouldBe S2.Companion.Rdm1().random()
        }
    }


    sealed class S3 {
        @Randomizable(S3.Companion.ChildrenRandomizer::class)
        class C3(val i: Int) : S3()

        companion object {
            val c3_2 = C3(321)
            class ChildrenRandomizer(
                val r: SameClassRandomizer<S3.C3> = SameClassRandomizer<S3.C3>(
                    returnedInstanceData = RDClassData.from<S3.C3>(),
                    makeRandom = {
                        c3_2
                    }
                )
            ) : ClassRandomizer<S3.C3> by r
        }
    }

    @Test
    fun `random on annotated children sealed class `(){
        shouldNotThrow<Throwable> {
            rdm.random(RDClassData.from<S3>()) shouldBe S3.Companion.ChildrenRandomizer().random()
        }
    }

    @Randomizable(randomizer = S4.Companion.ParentRandomizer::class)
    sealed class S4 {
        @Randomizable(S4.Companion.ChildrenRandomizer::class)
        data class C3(val i: Int) : S4()

        companion object {
            class ParentRandomizer(
                val r: SameClassRandomizer<S4.C3> = SameClassRandomizer<S4.C3>(
                    returnedInstanceData = RDClassData.from<S4.C3>(),
                    makeRandom = {
                        S4.C3(-999)
                    }
                )
            ) : ClassRandomizer<S4.C3> by r

            val c3_2 = C3(321)
            class ChildrenRandomizer(
                val r: SameClassRandomizer<S4.C3> = SameClassRandomizer<S4.C3>(
                    returnedInstanceData = RDClassData.from<S4.C3>(),
                    makeRandom = {
                        c3_2
                    }
                )
            ) : ClassRandomizer<S4.C3> by r
        }
    }

    @Test
    fun `random on annotated children sealed class + annotated parent sealed class`(){
        shouldNotThrow<Throwable> {
            rdm.random(RDClassData.from<S4>()) shouldBe S4.Companion.ParentRandomizer().random()
        }
    }

    @Randomizable(randomizer = S5.Companion.ParentRandomizer::class)
    sealed class S5 {
        @Randomizable(S5.Companion.ChildrenRandomizer::class)
        data class C3<T>(val i: T) : S5()

        companion object {
            class ParentRandomizer(
                val r: SameClassRandomizer<S5.C3<Int>> = SameClassRandomizer<S5.C3<Int>>(
                    returnedInstanceData = RDClassData.from<S5.C3<Int>>(),
                    makeRandom = {
                        C3(-999)
                    }
                )
            ) : ClassRandomizer<S5.C3<Int>> by r


            class ChildrenRandomizer(
                val r: SameClassRandomizer<S5.C3<Int>> = SameClassRandomizer<S5.C3<Int>>(
                    returnedInstanceData = RDClassData.from<S5.C3<Int>>(),
                    makeRandom = {
                        C3(321)
                    }
                )
            ) : ClassRandomizer<S5.C3<Int>> by r
        }
    }

    @Test
    fun `random on annotated generic children sealed class + annotated parent`(){
        rdm.random(RDClassData.from<S5>()) shouldBe S5.Companion.ParentRandomizer().random()
    }

    @Randomizable(randomizer = S6.Companion.ParentRandomizer::class)
    sealed class S6 {
        @Randomizable(S6.Companion.ChildrenRandomizer::class)
        abstract class C2: S6()
        data class C3<T>(val i: T) : C2()

        companion object {
            class ParentRandomizer(
                val r: SameClassRandomizer<S6.C3<Int>> = SameClassRandomizer<S6.C3<Int>>(
                    returnedInstanceData = RDClassData.from<S6.C3<Int>>(),
                    makeRandom = {
                        C3(-999)
                    }
                )
            ) : ClassRandomizer<S6.C3<Int>> by r


            class ChildrenRandomizer(
                val r: SameClassRandomizer<S6.C3<Int>> = SameClassRandomizer<S6.C3<Int>>(
                    returnedInstanceData = RDClassData.from<S6.C3<Int>>(),
                    makeRandom = {
                        C3(321)
                    }
                )
            ) : ClassRandomizer<S6.C3<Int>> by r
        }
    }

    @Test
    fun `random on correctly annotated abstract children`(){
        rdm.random(RDClassData.from<S6>()) shouldBe S6.Companion.ParentRandomizer().random()
    }

    sealed class S7 {
        abstract class C2: S7()
    }

    @Test
    fun `random on non-annotated abstract children`(){
        shouldThrow<Throwable> {
            rdm.random(RDClassData.from<S7>())
        }
    }

    sealed class S8 {
        @Randomizable(S6.Companion.ChildrenRandomizer::class)
        abstract class C2: S8()
        data class C3<T>(val i: T) : C2()
    }

    @Test
    fun `random on wrongly annotated abstract children`(){
        shouldThrow<Throwable> {
            rdm.random(RDClassData.from<S8>())
        }
    }

}
