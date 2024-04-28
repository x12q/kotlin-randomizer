package com.x12q.randomizer.randomizer.class_randomizer

import com.x12q.randomizer.randomizer.RDClassData
import com.x12q.randomizer.randomizer.classRandomizer
import com.x12q.randomizer.test_util.TestSamples
import io.kotest.matchers.shouldBe
import kotlin.test.*

class ClassRandomizerEnd1UtilsTest {
    @Test
    fun randomizer() {

        fun condition(dt: RDClassData): Boolean {
            return dt.kClass == Int::class
        }

        fun makeRandomIfApplicable(): Int {
            return 123
        }

        val rdm = classRandomizer<Int>(
            condition = ::condition,
            random = ::makeRandomIfApplicable
        )


        rdm.isApplicable(RDClassData.from<TestSamples.Class1>()) shouldBe condition(RDClassData.from<TestSamples.Class1>())
        rdm.isApplicable(RDClassData.from<Int>()) shouldBe condition(RDClassData.from<Int>())
        rdm.random() shouldBe makeRandomIfApplicable()

    }
}
