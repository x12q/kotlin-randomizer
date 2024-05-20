package com.x12q.randomizer.annotations.number._double

import com.x12q.randomizer.annotations.number._double.RandomDoubleFixed.Companion.makeClassRandomizer
import com.x12q.randomizer.randomizer.ClassRandomizer
import io.kotest.matchers.types.shouldBeInstanceOf
import org.junit.jupiter.api.Assertions.*
import kotlin.test.Test

class RandomDoubleFixedTest{
    @Test
    fun qwe(){
        RandomDoubleFixed(123.0).makeClassRandomizer().shouldBeInstanceOf<ClassRandomizer<Double>>()
    }
}
