package com.x12q.randomizer.annotations.str

import com.x12q.randomizer.annotations.str.RandomStringUUID.Companion.makeClassRandomizer
import com.x12q.randomizer.randomizer.ClassRandomizer
import io.kotest.matchers.types.shouldBeInstanceOf
import org.junit.jupiter.api.Assertions.*
import kotlin.test.Test

class RandomStringUUIDTest{
    @Test
    fun makeClassRandomizer(){
        RandomStringUUID().makeClassRandomizer().shouldBeInstanceOf<ClassRandomizer<String>>()
    }
}
