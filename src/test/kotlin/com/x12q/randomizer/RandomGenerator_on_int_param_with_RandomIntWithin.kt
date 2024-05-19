package com.x12q.randomizer

import com.x12q.randomizer.annotations.number._int.RandomIntFixed
import com.x12q.randomizer.annotations.number._int.RandomIntWithin
import com.x12q.randomizer.test_util.TestSamples
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import kotlin.test.BeforeTest
import kotlin.test.Test


class RandomGenerator_on_int_param_with_RandomIntWithin {

    lateinit var rdm: RandomGenerator

    @BeforeTest
    fun bt() {
        rdm = TestSamples.comp.randomizer()
    }

    data class B(
        @RandomIntWithin(123,123)
        val i:Int
    )

    @Test
    fun `RandomIntFixed on int param`() {
        (rdm.random(RDClassData.from<B>()) as B).i shouldBe 123
    }

    data class B2(
        @RandomIntWithin(123,123)
        val f:Float
    )

    @Test
    fun `RandomIntFixed on wrong type param`() {
        shouldThrow<Exception> {
            rdm.random(RDClassData.from<B2>())
        }
    }

    data class B3<T>(
        @RandomIntWithin(123,123)
        val i:T
    )

    @Test
    fun `RandomIntFixed on int generic type param`() {
        (rdm.random(RDClassData.from<B3<Int>>()) as B3<Int>).i shouldBe 123

    }

}
