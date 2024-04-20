package com.x12q.randomizer

import com.x12q.randomizer.config.RandomizerCollection
import com.x12q.randomizer.config.RandomizerConfigFactory
import com.x12q.randomizer.di.DaggerRDComponent
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
