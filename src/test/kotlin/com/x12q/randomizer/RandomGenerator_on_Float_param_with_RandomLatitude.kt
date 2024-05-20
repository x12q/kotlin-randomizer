package com.x12q.randomizer

import com.x12q.randomizer.RandomGenerator_on_Float_param_with_RandomFloatWithin.B4
import com.x12q.randomizer.annotations.number._double.RandomDoubleFixed
import com.x12q.randomizer.annotations.number._float.RandomLatitude
import com.x12q.randomizer.annotations.number._int.RandomIntFixed
import com.x12q.randomizer.test_util.TestSamples
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.floats.shouldBeBetween
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlin.test.BeforeTest
import kotlin.test.Test


class RandomGenerator_on_Float_param_with_RandomLatitude {

    lateinit var rdm: RandomGenerator

    @BeforeTest
    fun bt() {
        rdm = TestSamples.comp.randomizer()
    }

    data class B(
        @RandomLatitude
        val i:Float
    )

    @Test
    fun `on float param`() {
        (rdm.random(RDClassData.from<B>()) as B).i.shouldBeBetween(-90f,90f,0.0001f)
    }

    data class B2(
        @RandomLatitude
        val f:Int
    )

    @Test
    fun `on wrong type param`() {
        shouldThrow<Exception> {
            rdm.random(RDClassData.from<B2>())
        }
    }

    data class B3<T>(
        @RandomLatitude
        val i:T
    )

    @Test
    fun `on float generic type param`() {
        (rdm.random(RDClassData.from<B3<Float>>()) as B3<Float>).i.shouldBeInstanceOf<Float>()
        shouldThrow<Exception> {
            rdm.random(RDClassData.from<B3<String>>())
        }
    }
    data class B4(
        @RandomLatitude
        val n:Number
    )

    @Test
    fun `on Number`(){
        (rdm.random(RDClassData.from<B4>()) as B4).n.also {
            it.shouldBeInstanceOf<Float>()
            it.shouldBeBetween(-90f,90f,0.000001f)
        }
    }
}
