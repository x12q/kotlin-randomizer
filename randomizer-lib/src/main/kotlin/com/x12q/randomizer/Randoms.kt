package com.x12q.randomizer

import com.x12q.randomizer.di.DaggerRandomizerComponent
import com.x12q.randomizer.randomizer.builder.ParamRandomizerListBuilder
import com.x12q.randomizer.randomizer.builder.RandomizerListBuilder
import com.x12q.randomizer.randomizer.config.RandomizerConfig
import kotlin.random.Random

/**
 * Make a random instance of [T] with the option to specify [randomizers] and [paramRandomizers] that
 * can override default random logic.
 */
inline fun <reified T> random(
    random: Random = Random,
    randomizers: RandomizerListBuilder = RandomizerListBuilder(),
    paramRandomizers: ParamRandomizerListBuilder = ParamRandomizerListBuilder(),
    defaultRandomConfig: RandomizerConfig = RandomizerConfig.default,
): T {

    val comp = DaggerRandomizerComponent.builder()
        .setRandom(random)
        .build()

    val baseRandomizer = comp.randomizer()

    val randomizerCollection = baseRandomizer.lv1RandomizerCollection
        .addRandomizers(randomizers.buildNormalRandomizer())
        .addParamRandomizer(paramRandomizers.build())

    val nonContextRandomizer = baseRandomizer.copy(
        lv1RandomizerCollection = randomizerCollection,
        defaultRandomConfig = defaultRandomConfig,
    )

    val context=  nonContextRandomizer.makeContext()
    randomizers.externalContext = context
    paramRandomizers.externalContext = context

    val contextualRandomCollection = baseRandomizer.lv1RandomizerCollection
        .addRandomizers(randomizers.buildContextualRandomizer())
        .addParamRandomizer(paramRandomizers.buildContextualRandomizer())


    val contextRandomizer = baseRandomizer.copy(
        lv1RandomizerCollection = contextualRandomCollection
    )

    val mergedRandomizer = nonContextRandomizer.mergeWith(contextRandomizer)

    val clzzData = RDClassData.from<T>()
    return mergedRandomizer.random(clzzData) as T
}

/**
 * Make a random instance of an inner class [T] within [enclosingObject].
 */
inline fun <reified T : Any> randomInnerClass(
    enclosingObject: Any,
    random: Random = Random,
    randomizers: RandomizerListBuilder,
    paramRandomizers: ParamRandomizerListBuilder,
    defaultRandomConfig: RandomizerConfig = RandomizerConfig.default
): T {
    val comp = DaggerRandomizerComponent.builder()
        .setRandom(random)
        .build()

    val randomizer = comp.randomizer().let {
        it.copy(
            lv1RandomizerCollection = it.lv1RandomizerCollection
                .addRandomizers(randomizers.buildNormalRandomizer())
                .addParamRandomizer(paramRandomizers.build()),
            defaultRandomConfig = defaultRandomConfig,
        )
    }
    val clzzData = RDClassData.from<T>()
    return randomizer.randomInnerClass(clzzData, enclosingObject) as T
}
