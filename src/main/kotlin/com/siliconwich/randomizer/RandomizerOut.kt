package com.siliconwich.randomizer

import com.siliconwich.randomizer.config.RandomizerCollection
import com.siliconwich.randomizer.config.RandomizerConfigFactory
import kotlin.random.Random

object RandomizerOut {
    inline fun <reified T : Any> makeRandomInstance(
        random: Random = Random,
        config: RandomizerCollection = RandomizerConfigFactory.defaultConfig(),
    ): T {
        val producer = Randomizer(random, config)
        val q =RDClassData.from<T>()
        return producer.random(q) as T
    }
}
