package com.x12q.randomizer

import com.x12q.randomizer.di.DaggerRDComponent
import com.x12q.randomizer.randomizer.ClassRandomizer
import com.x12q.randomizer.randomizer.ParameterRandomizer
import com.x12q.randomizer.randomizer.RDClassData
import kotlin.random.Random



/**
 * Make a random instance of [T] with the option to specify [randomizers] and [paramRandomizers] that
 * can override default random logic.
 */
inline fun <reified T : Any> random(
    random: Random = Random,
    randomizers: Collection<ClassRandomizer<*>> = emptyList(),
    paramRandomizers: Collection<ParameterRandomizer<*>> = emptyList(),
): T {
    val comp = DaggerRDComponent.builder()
        .setRandom(random)
        .build()

    val randomizer = comp.randomizer()

    val randomizer2 = randomizer.copy(
        lv1RandomizerCollection = randomizer.lv1RandomizerCollection
            .addParamRandomizer(*paramRandomizers.toTypedArray())
            .addRandomizers(*randomizers.toTypedArray())
    )

    val clzzData = RDClassData.from<T>()
    return randomizer2.random(clzzData) as T
}

/**
 * Make a random instance of an inner class [T] within [enclosingObject].
 */
inline fun <reified T : Any> randomInnerClass(
    enclosingObject:Any,
    random: Random = Random,
): T {
    val comp = DaggerRDComponent.builder()
        .setRandom(random)
        .build()

    val randomizer = comp.randomizer()
    val clzzData = RDClassData.from<T>()
    return randomizer.randomInnerClass(clzzData,enclosingObject) as T
}

