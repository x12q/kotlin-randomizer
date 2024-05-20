package com.x12q.randomizer

import com.x12q.randomizer.RandomGenerator_on_Double_param_with_RandomDoubleOneOf.B3
import com.x12q.randomizer.annotations.number._double.RandomDoubleFixed
import com.x12q.randomizer.annotations.number._float.RandomFloatFixed
import com.x12q.randomizer.annotations.number._int.RandomIntFixed
import com.x12q.randomizer.test_util.TestSamples
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import kotlin.test.BeforeTest
import kotlin.test.Test


class RandomGenerator_on_Float_param_with_RandomFloatFixed {

    lateinit var rdm: RandomGenerator

    @BeforeTest
    fun bt() {
        rdm = TestSamples.comp.randomizer()
    }

    data class B(
        @RandomFloatFixed(123f)
        val i:Float
    )

    @Test
    fun `on float param`() {
        (rdm.random(RDClassData.from<B>()) as B).i shouldBe 123f
    }

    data class B2(
        @RandomFloatFixed(33f)
        val f:Int
    )

    @Test
    fun `on wrong type param`() {
        shouldThrow<Exception> {
            rdm.random(RDClassData.from<B2>())
        }
    }

    data class B3<T>(
        @RandomFloatFixed(123f)
        val i:T
    )

    @Test
    fun `on float generic type param`() {
        (rdm.random(RDClassData.from<B3<Float>>()) as B3<Float>).i shouldBe 123f
        shouldThrow<Exception> {
            rdm.random(RDClassData.from<B3<String>>())
        }
    }


    data class B4(
        @RandomFloatFixed(1.0f)
        val n:Number
    )

    @Test
    fun `on Number`(){
        (rdm.random(RDClassData.from<B4>()) as B4).n shouldBe 1.0f
    }
}
