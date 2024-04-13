package com.siliconwich.randomizer

import kotlin.random.Random

object Randomizer {
    inline fun <reified T : Any> makeRandomInstance(
        random: Random = Random,
    ): T {
        val producer = Randomizer0(random)
        return producer.makeRandomInstance(ClassData.from<T>()) as T
    }
}
