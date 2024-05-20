package com.x12q.randomizer.annotations.number._float

import com.x12q.randomizer.annotations.number._float.RandomFloatFixed.Companion.makeClassRandomizer
import com.x12q.randomizer.randomizer.ClassRandomizer
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import org.junit.jupiter.api.Assertions.*
import kotlin.test.Test

class RandomFloatFixedTest{
    @Test
    fun makeClassRandomizer(){
        RandomFloatFixed(1f).makeClassRandomizer().also {
            it.shouldBeInstanceOf<ClassRandomizer<Float>>()
            it.random() shouldBe 1f
        }
    }
}
