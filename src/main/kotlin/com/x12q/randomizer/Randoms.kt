package com.x12q.randomizer

import com.x12q.randomizer.di.DaggerRandomizerComponent
import com.x12q.randomizer.randomizer.ClassRandomizer
import com.x12q.randomizer.randomizer.ParameterRandomizer
import com.x12q.randomizer.randomizer.builder.ParamRandomizerListBuilder
import com.x12q.randomizer.randomizer.builder.RandomizerListBuilder
import com.x12q.randomizer.randomizer.config.RandomizerConfig
import kotlin.random.Random


inline fun <reified T> randomFromBuilder(
    random: Random = Random,
    randomizersBuilder: RandomizerListBuilder,
    paramRandomizersBuilder: ParamRandomizerListBuilder,
    defaultRandomConfig: RandomizerConfig = RandomizerConfig.default
): T {
    return random<T>(
        random = random,
        randomizers = randomizersBuilder.build(),
        paramRandomizers = paramRandomizersBuilder.build(),
        defaultRandomConfig = defaultRandomConfig,
    )
}

inline fun <reified T> randomFromContext(
    context: RandomContext,
): T {
    return randomFromBuilder(
        random = context.random,
        randomizersBuilder = context.randomizersBuilder,
        paramRandomizersBuilder = context.paramRandomizersBuilder,
        defaultRandomConfig = context.randomizerConfig
    )
}
/**
 * Make a random instance of [T] with the option to specify [randomizers] and [paramRandomizers] that
 * can override default random logic.
 */
inline fun <reified T> random(
    random: Random = Random,
    randomizers: Collection<ClassRandomizer<*>> = emptyList(),
    paramRandomizers: Collection<ParameterRandomizer<*>> = emptyList(),
    defaultRandomConfig: RandomizerConfig = RandomizerConfig.default
): T {

    val comp = DaggerRandomizerComponent.builder()
        .setRandom(random)
        .build()

    val randomizer = comp.randomizer().let {rdm->
        rdm.copy(
            lv1RandomizerCollection = rdm
                .lv1RandomizerCollection
                .addParamRandomizer(*paramRandomizers.toTypedArray())
                .addRandomizers(*randomizers.toTypedArray()),
            defaultRandomConfig = defaultRandomConfig,
        )
    }

    val clzzData = RDClassData.from<T>()
    return randomizer.random(clzzData) as T
}

/**
 * Make a random instance of an inner class [T] within [enclosingObject].
 */
inline fun <reified T : Any> randomInnerClass(
    enclosingObject: Any,
    random: Random = Random,
    randomizers: Collection<ClassRandomizer<*>> = emptyList(),
    paramRandomizers: Collection<ParameterRandomizer<*>> = emptyList(),
    defaultRandomConfig: RandomizerConfig = RandomizerConfig.default
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

