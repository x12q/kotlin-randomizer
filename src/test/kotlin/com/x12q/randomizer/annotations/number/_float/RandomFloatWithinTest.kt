package com.x12q.randomizer.annotations.number._float

import com.x12q.randomizer.annotations.number._float.RandomFloatWithin.Companion.makeClassRandomizer
import com.x12q.randomizer.randomizer.ClassRandomizer
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlin.test.Test

class RandomFloatWithinTest{
    @Test
    fun qwe(){
        RandomFloatWithin(1f,2f).makeClassRandomizer().also {
            it.shouldBeInstanceOf<ClassRandomizer<Float>>()
        }
    }
}
