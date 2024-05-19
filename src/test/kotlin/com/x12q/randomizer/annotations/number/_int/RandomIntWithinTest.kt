package com.x12q.randomizer.annotations.number._int

import com.x12q.randomizer.annotations.number._int.RandomIntWithin.Companion.makeClassRandomizer
import com.x12q.randomizer.randomizer.ClassRandomizer
import io.kotest.matchers.collections.shouldBeIn
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import org.junit.jupiter.api.Assertions.*
import kotlin.test.Test

class RandomIntWithinTest{
    @Test
    fun qwe(){
        RandomIntWithin(20,20).makeClassRandomizer().also {rdm->
            rdm.shouldBeInstanceOf<ClassRandomizer<Int>>()
            rdm.random() shouldBe 20
        }

        RandomIntWithin(20,100).makeClassRandomizer().also {rdm->
            rdm.shouldBeInstanceOf<ClassRandomizer<Int>>()
            repeat(100){
                rdm.random().shouldBeIn((20 .. 100).toList())
            }
        }
    }
}
