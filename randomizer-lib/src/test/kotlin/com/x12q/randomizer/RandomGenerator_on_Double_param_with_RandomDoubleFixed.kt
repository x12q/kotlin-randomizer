package com.x12q.randomizer

import com.x12q.randomizer.annotations.number._double.RandomDoubleFixed
import com.x12q.randomizer.annotations.number._float.RandomFloatFixed
import com.x12q.randomizer.annotations.number._int.RandomIntFixed
import com.x12q.randomizer.test_util.TestSamples
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import kotlin.test.BeforeTest
import kotlin.test.Test


class RandomGenerator_on_Double_param_with_RandomDoubleFixed {

    lateinit var rdm: RandomGenerator

    @BeforeTest
    fun bt() {
        rdm = TestSamples.comp.randomizer()
    }

    data class B(
        @RandomDoubleFixed(123.0)
        val i:Double
    )

    @Test
    fun `on Double param`() {
        (rdm.random(RDClassData.from<B>()) as B).i shouldBe 123f
    }

    data class B2(
        @RandomDoubleFixed(33.0)
        val f:Int
    )

    @Test
    fun `on wrong type param`() {
        shouldThrow<Exception> {
            rdm.random(RDClassData.from<B2>())
        }
    }

    data class B3<T>(
        @RandomDoubleFixed(123.0)
        val i:T
    )

    @Test
    fun `on generic type param`() {
        (rdm.random(RDClassData.from<B3<Double>>()) as B3<Double>).i shouldBe 123f
        shouldThrow<Exception> {
            rdm.random(RDClassData.from<B3<String>>())
        }
    }

    data class B4(
        @RandomDoubleFixed(1.0)
        val n:Number
    )

    @Test
    fun `on Number`(){
        (rdm.random(RDClassData.from<B4>()) as B4).n shouldBe 1.0
    }

}
