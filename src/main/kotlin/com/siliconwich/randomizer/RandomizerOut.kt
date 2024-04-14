package com.siliconwich.randomizer

import com.siliconwich.randomizer.config.RandomizerConfig
import com.siliconwich.randomizer.config.RandomizerConfigFactory
import kotlin.random.Random

object RandomizerOut {
    inline fun <reified T : Any> makeRandomInstance(
        random: Random = Random,
        config: RandomizerConfig = RandomizerConfigFactory.defaultConfig(),
    ): T {
        val producer = Randomizer(random, config)
        val q =ClassData.from<T>()
        return producer.makeRandomInstance(q) as T
    }
}
