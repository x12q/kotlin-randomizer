package com.x12q.randomizer

import com.x12q.randomizer.annotations.number._float.RandomLongitude
import com.x12q.randomizer.test_util.TestSamples
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.floats.shouldBeBetween
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlin.test.BeforeTest
import kotlin.test.Test


class RandomGenerator_on_Float_param_with_RandomLongitude {

    lateinit var rdm: RandomGenerator

    @BeforeTest
    fun bt() {
        rdm = TestSamples.comp.randomizer()
    }

    data class B(
        @RandomLongitude
        val i:Float
    )

    @Test
    fun `on float param`() {
        (rdm.random(RDClassData.from<B>()) as B).i.shouldBeBetween(-180f,180f,0.0001f)
    }

    data class B2(
        @RandomLongitude
        val f:Int
    )

    @Test
    fun `on wrong type param`() {
        shouldThrow<Exception> {
            rdm.random(RDClassData.from<B2>())
        }
    }

    data class B3<T>(
        @RandomLongitude
        val i:T
    )

    @Test
    fun `on float generic type param`() {
        (rdm.random(RDClassData.from<B3<Float>>()) as B3<Float>).i.shouldBeInstanceOf<Float>()
        shouldThrow<Exception> {
            rdm.random(RDClassData.from<B3<String>>())
        }
    }

}
