package com.x12q.randomizer.annotations.str

import com.x12q.randomizer.annotations.str.RandomStringOneOf.Companion.makeClassRandomizer
import com.x12q.randomizer.randomizer.ClassRandomizer
import io.kotest.matchers.types.shouldBeInstanceOf
import org.junit.jupiter.api.Assertions.*
import kotlin.test.Test

class RandomStringOneOfTest{
    @Test
    fun makeClassRandomizer(){
        RandomStringOneOf(arrayOf("a","b")).makeClassRandomizer().shouldBeInstanceOf<ClassRandomizer<String>>()
    }
}
