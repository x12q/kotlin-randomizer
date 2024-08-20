package com.x12q.randomizer.annotations.number._float

import com.x12q.randomizer.annotations.number._float.RandomLongitude.Companion.makeClassRandomizer
import com.x12q.randomizer.randomizer.ClassRandomizer
import io.kotest.matchers.types.shouldBeInstanceOf
import org.junit.jupiter.api.Assertions.*
import kotlin.test.Test

class RandomLongitudeTest{
    @Test
    fun makeClassRandomizer(){
        RandomLongitude().makeClassRandomizer().shouldBeInstanceOf<ClassRandomizer<Float>>()
    }
}
