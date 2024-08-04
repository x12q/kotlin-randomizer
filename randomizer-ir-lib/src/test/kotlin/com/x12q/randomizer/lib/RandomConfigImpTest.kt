package com.x12q.randomizer.lib

import io.kotest.matchers.shouldBe
import kotlin.random.Random
import kotlin.reflect.KClass
import kotlin.test.*

class RandomConfigImpTest{

    @Test
    fun `access random collection from random config`(){

        val collection = object:RandomizerCollection{
            override val randomizersMap: Map<KClass<*>, ClassRandomizer<*>> = mapOf(
                Int::class to ConstantClassRandomizer(123,Int::class),
                Float::class to ConstantClassRandomizer(33f,Float::class),
            )
        }

        val rdConfig = RandomConfigImp(
            random = Random,
            collectionSizeRange = 1 .. 3,
            charRange = RandomConfigImp.default.charRange,
            randomizerCollection = collection
        )

        rdConfig.random<Int>() shouldBe 123
        rdConfig.random<Float>() shouldBe 33f
    }
}
