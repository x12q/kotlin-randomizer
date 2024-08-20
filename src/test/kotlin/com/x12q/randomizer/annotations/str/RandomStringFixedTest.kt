package com.x12q.randomizer.annotations.str

import com.x12q.randomizer.annotations.str.RandomStringFixed.Companion.makeClassRandomizer
import com.x12q.randomizer.randomizer.ClassRandomizer
import io.kotest.matchers.types.shouldBeInstanceOf
import org.junit.jupiter.api.Assertions.*
import kotlin.test.Test

class RandomStringFixedTest{
    @Test
    fun makeClassRandomizer(){
        RandomStringFixed("a").makeClassRandomizer().shouldBeInstanceOf<ClassRandomizer<String>>()
    }
}
