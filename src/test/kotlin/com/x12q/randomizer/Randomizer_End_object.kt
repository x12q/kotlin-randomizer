package com.x12q.randomizer

import com.github.michaelbull.result.Ok
import com.x12q.randomizer.randomizer.RDClassData
import com.x12q.randomizer.randomizer.ClassRandomizer
import com.x12q.randomizer.randomizer.ParamInfo
import com.x12q.randomizer.randomizer.ParameterRandomizer
import com.x12q.randomizer.test_util.TestSamples
import com.x12q.randomizer.test_util.TestSamples.Class1
import com.x12q.randomizer.test_util.TestSamples.Class2
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.spyk
import kotlin.reflect.KParameter
import kotlin.test.BeforeTest
import kotlin.test.Test


class Randomizer_End_object {

    lateinit var rdm: RandomizerEnd

    @BeforeTest
    fun bt() {
        rdm = TestSamples.comp.randomizer()
    }

    object Q{}

    @Test
    fun `random on object`(){
        shouldNotThrow<Throwable> {
            rdm.random(RDClassData.from<Q>()) shouldBe Q
        }
    }
}
