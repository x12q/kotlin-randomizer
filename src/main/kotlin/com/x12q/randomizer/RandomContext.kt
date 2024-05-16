package com.x12q.randomizer

import com.x12q.randomizer.randomizer.RandomizerCollection
import com.x12q.randomizer.randomizer.config.RandomizerConfig
import com.x12q.randomizer.randomizer_checker.RandomizerChecker
import javax.inject.Inject
import kotlin.random.Random

/**
 * A random context is simply an encapsulation of everything a [RandomGenerator] need.
 */
data class RandomContext(
    val random: Random,
    val lv1RandomizerCollection: RandomizerCollection,
    val randomizerChecker: RandomizerChecker,
    val defaultRandomConfig: RandomizerConfig,
)
