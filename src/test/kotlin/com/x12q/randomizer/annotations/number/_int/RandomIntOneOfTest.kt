package com.x12q.randomizer.annotations.number._int

import com.x12q.randomizer.annotations.number._int.RandomIntOneOf.Companion.makeClassRandomizer
import com.x12q.randomizer.randomizer.ClassRandomizer
import io.kotest.matchers.collections.shouldBeIn
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import org.junit.jupiter.api.Assertions.*
import kotlin.test.Test

class RandomIntOneOfTest{
    @Test
    fun makeClassRandomizer(){

        val array = listOf(1,2,3,4)
        RandomIntOneOf(array.toIntArray()).makeClassRandomizer().also {rdm->
            rdm.shouldBeInstanceOf<ClassRandomizer<Int>>()
            repeat(100){
                rdm.random().shouldBeIn(array)
            }
        }
    }
}
