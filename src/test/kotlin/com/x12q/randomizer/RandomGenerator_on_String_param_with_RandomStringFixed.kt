package com.x12q.randomizer

import com.x12q.randomizer.annotations.number._double.RandomDoubleFixed
import com.x12q.randomizer.annotations.number._float.RandomFloatFixed
import com.x12q.randomizer.annotations.number._int.RandomIntFixed
import com.x12q.randomizer.annotations.str.RandomStringFixed
import com.x12q.randomizer.test_util.TestSamples
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import kotlin.test.BeforeTest
import kotlin.test.Test


class RandomGenerator_on_String_param_with_RandomStringFixed {

    lateinit var rdm: RandomGenerator

    @BeforeTest
    fun bt() {
        rdm = TestSamples.comp.randomizer()
    }

    data class B(
        @RandomStringFixed("abc")
        val i:String
    )

    @Test
    fun `on Double param`() {
        (rdm.random(RDClassData.from<B>()) as B).i shouldBe "abc"
    }

    data class B2(
        @RandomStringFixed("abc")
        val f:Int
    )

    @Test
    fun `on wrong type param`() {
        shouldThrow<Exception> {
            rdm.random(RDClassData.from<B2>())
        }
    }

    data class B3<T>(
        @RandomStringFixed("abc")
        val i:T
    )

    @Test
    fun `on generic type param`() {
        (rdm.random(RDClassData.from<B3<String>>()) as B3<String>).i shouldBe "abc"
        shouldThrow<Exception> {
            rdm.random(RDClassData.from<B3<Double>>())
        }
    }

    data class B4(
        @RandomStringFixed("qwe")
        val n:CharSequence
    )

    @Test
    fun `on CharSequence`(){
        (rdm.random(RDClassData.from<B4>()) as B4).n shouldBe "qwe"
    }

}
