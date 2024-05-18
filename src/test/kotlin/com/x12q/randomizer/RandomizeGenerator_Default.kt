package com.x12q.randomizer

import com.x12q.randomizer.test_util.TestSamples
import com.x12q.randomizer.test_util.TestSamples.Class1
import com.x12q.randomizer.test_util.TestSamples.Class2
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.spyk
import kotlin.test.BeforeTest
import kotlin.test.Test


class RandomizeGenerator_Default {

    lateinit var rdm0: RandomGenerator
    lateinit var rdm: RandomGenerator

    val spyParamRdm = spyk(Class1.tm12FixedRandomizer)
    val classRdm = Class2.classFixedRandomizer

    @BeforeTest
    fun bt() {
        rdm0 = TestSamples.comp.randomizer()
        rdm = rdm0.copy(
            lv1RandomizerCollection = rdm0.lv1RandomizerCollection
                .addParamRandomizer(spyParamRdm)
                .addRandomizers(classRdm)
        )
    }

    /**
     * Verify that custom class randomizer was used instead of the default one.
     */
    @Test
    fun random() {
        val p0 = rdm0.random(Class2.dt)
        val p1 = rdm.random(Class2.dt)

        p1 shouldNotBe p0
        p1 shouldBe classRdm.random()

    }
}
