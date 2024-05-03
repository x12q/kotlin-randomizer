package com.x12q.randomizer

import com.x12q.randomizer.randomizer.ClassRandomizer
import com.x12q.randomizer.randomizer.RDClassData
import com.x12q.randomizer.randomizer.clazz.SameClassRandomizer
import com.x12q.randomizer.test_util.TestSamples
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlin.test.BeforeTest
import kotlin.test.Test

class Randomizer_End_Sealed_Class {
    lateinit var rdm: RandomizerEnd

    @BeforeTest
    fun bt() {
        rdm = TestSamples.comp.randomizer()
    }

    sealed class S1 {
        object C1 : S1()
        object C2 : S1()
        class C3(val i: Int) : S1()
        class C4(val i: String) : S1()
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
        object C1 : S3()
        object C2 : S3()
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
        object C1 : S4()
        object C2 : S4()
        @Randomizable(S4.Companion.ChildrenRandomizer::class)
        class C3(val i: Int) : S4()

        companion object {
            class ParentRandomizer(
                val r: SameClassRandomizer<S4.C1> = SameClassRandomizer<S4.C1>(
                    returnedInstanceData = RDClassData.from<S4.C1>(),
                    makeRandom = {
                        S4.C1
                    }
                )
            ) : ClassRandomizer<S4.C1> by r

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
            rdm.random(RDClassData.from<S4>()) shouldBe S4.Companion.ChildrenRandomizer().random()
        }
    }
}
