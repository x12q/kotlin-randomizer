package com.x12q.randomizer.randomizer.chain

import com.x12q.randomizer.randomizer.primitive.floatRandomizer
import com.x12q.randomizer.randomizer.primitive.stringRandomizer
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Assertions.*
import kotlin.random.Random
import kotlin.test.Test

class ChainRandomizerKtTest{
    @Test
    fun chainTest(){
        val randomizer = stringRandomizer {
            (1..10).joinToString("") { "a" }
        }.then { str->
            str.length
        }.then{ int->
            (1..int).joinToString("") { "b" }
        }
        randomizer.random() shouldBe (1..10).joinToString("") { "b" }
    }
}
