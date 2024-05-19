package com.x12q.randomizer

import com.x12q.randomizer.annotations.number._int.RandomIntOneOf
import com.x12q.randomizer.test_util.TestSamples
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import kotlin.test.BeforeTest
import kotlin.test.Test


class RandomGenerator_on_Int_param_with_RandomIntOneOf {

    lateinit var rdm: RandomGenerator

    @BeforeTest
    fun bt() {
        rdm = TestSamples.comp.randomizer()
    }

    data class B(
        @RandomIntOneOf([100])
        val i:Int
    )

    @Test
    fun `on int param`() {
        (rdm.random(RDClassData.from<B>()) as B).i shouldBe 100
    }

    data class B2(
        @RandomIntOneOf([33])
        val f:Float
    )

    @Test
    fun `on wrong type param`() {
        shouldThrow<Exception> {
            rdm.random(RDClassData.from<B2>())
        }
    }

    data class B3<T>(
        @RandomIntOneOf([123])
        val i:T
    )

    @Test
    fun `on int generic type param`() {
        (rdm.random(RDClassData.from<B3<Int>>()) as B3<Int>).i shouldBe 123
    }

}
