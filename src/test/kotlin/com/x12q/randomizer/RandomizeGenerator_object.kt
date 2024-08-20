package com.x12q.randomizer

import com.x12q.randomizer.test_util.TestSamples
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.matchers.shouldBe
import kotlin.test.BeforeTest
import kotlin.test.Test


class RandomizeGenerator_object {

    lateinit var rdm: RandomGenerator

    @BeforeTest
    fun bt() {
        rdm = TestSamples.comp.randomizer()
    }

    object Q

    @Test
    fun `random on object`(){
        shouldNotThrow<Throwable> {
            rdm.random(RDClassData.from<Q>()) shouldBe Q
        }
    }
}
