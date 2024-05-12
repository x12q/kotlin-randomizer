package com.x12q.randomizer

import com.x12q.randomizer.di.DaggerRandomizerComponent
import com.x12q.randomizer.randomizer.ClassRandomizer
import com.x12q.randomizer.randomizer.ParameterRandomizer
import com.x12q.randomizer.randomizer.config.DefaultRandomConfig
import kotlin.random.Random

inline fun <reified T> random(
    randomConfig:RandomConfig
):T{

    val random = randomConfig.random
    val randomizers = randomConfig.randomizers
    val paramRandomizers = randomConfig.paramRandomizers
    val defaultRandomConfig = randomConfig.defaultRandomConfig

    val comp = DaggerRandomizerComponent.builder()
        .setRandom(random)
        .build()

    val randomizer = comp.randomizer().let {
        it.copy(
            lv1RandomizerCollection = it.lv1RandomizerCollection
                .addParamRandomizer(*paramRandomizers.toTypedArray())
                .addRandomizers(*randomizers.toTypedArray()),
            defaultRandomConfig = defaultRandomConfig,
        )
    }

    val clzzData = RDClassData.from<T>()
    return randomizer.random(clzzData) as T
}

/**
 * Make a random instance of [T] with the option to specify [randomizers] and [paramRandomizers] that
 * can override default random logic.
 */
inline fun <reified T> random(
    random: Random = Random,
    randomizers: Collection<ClassRandomizer<*>> = emptyList(),
    paramRandomizers: Collection<ParameterRandomizer<*>> = emptyList(),
    defaultRandomConfig: DefaultRandomConfig = DefaultRandomConfig.default
): T {

    val config = RandomContextImp(
        random = random,
        randomizers = randomizers,
        paramRandomizers = paramRandomizers,
        defaultRandomConfig = defaultRandomConfig
    )

    return random(config)
}

/**
 * Make a random instance of an inner class [T] within [enclosingObject].
 */
inline fun <reified T : Any> randomInnerClass(
    enclosingObject: Any,
    random: Random = Random,
    randomizers: Collection<ClassRandomizer<*>> = emptyList(),
    paramRandomizers: Collection<ParameterRandomizer<*>> = emptyList(),
    defaultRandomConfig: DefaultRandomConfig = DefaultRandomConfig.default
): T {
    val comp = DaggerRandomizerComponent.builder()
        .setRandom(random)
        .build()

    val randomizer = comp.randomizer().let {
        it.copy(
            lv1RandomizerCollection = it.lv1RandomizerCollection
                .addParamRandomizer(*paramRandomizers.toTypedArray())
                .addRandomizers(*randomizers.toTypedArray()),
            defaultRandomConfig = defaultRandomConfig,
        )
    }
    val clzzData = RDClassData.from<T>()
    return randomizer.randomInnerClass(clzzData, enclosingObject) as T
}

