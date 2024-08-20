package com.x12q.randomizer.annotations.number._float

import com.x12q.randomizer.annotations.number._float.RandomFloatOneOf.Companion.makeClassRandomizer
import com.x12q.randomizer.randomizer.ClassRandomizer
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlin.random.Random
import kotlin.random.nextInt
import kotlin.test.Test

class RandomFloatOneOfTest{
    @Test
    fun makeClassRandomizer(){
        val l = listOf(1f)
        RandomFloatOneOf(l.toFloatArray()).makeClassRandomizer().also {
            it.shouldBeInstanceOf<ClassRandomizer<Float>>()
            it.random() shouldBe 1f
        }
    }
}
