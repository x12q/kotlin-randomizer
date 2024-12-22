package com.x12q.kotlin.randomizer.lib

import com.x12q.kotlin.randomizer.lib.util.developerErrorMsg

/**
 * Generate a random instance of [T]
 */
fun <T> random(
    makeRandom: (randomContext: RandomContext) -> T = {
        throw IllegalArgumentException(developerErrorMsg("makeRandom is supposed to be replaced by a generated substitute."))
    },
    randomConfig: RandomConfig = RandomConfig.default,
    randomizers: RandomContextBuilder.() -> Unit = {}
): T {
    val randomContextBuilder = RandomContextBuilderImp()
    return random(
        makeRandom = makeRandom,
        randomConfig = randomConfig,
        randomContextBuilder = randomContextBuilder,
        configRandomContextBuilder = randomizers
    )
}


fun <T> RandomContextBuilder.random(
    makeRandom: ((randomContext: RandomContext) -> T) = {
        throw IllegalArgumentException(developerErrorMsg("makeRandom is supposed to be replaced by a generated substitute."))
    },
    randomConfig: RandomConfig = this.randomConfig,
    randomizers: RandomContextBuilder.() -> Unit = {}
): T {
    return random(
        makeRandom = makeRandom,
        randomConfig = randomConfig,
        randomContextBuilder = this,
        configRandomContextBuilder = randomizers
    )
}


fun <T> random(
    makeRandom: (randomContext: RandomContext) -> T,
    randomConfig: RandomConfig = RandomConfig.default,
    randomContextBuilder: RandomContextBuilder,
    configRandomContextBuilder: RandomContextBuilder.() -> Unit = {}
): T {
    randomContextBuilder.setRandomConfigAndGenerateStandardRandomizers(randomConfig)
    randomContextBuilder.configRandomContextBuilder()
    val randomContext = randomContextBuilder.build()
    return makeRandom(randomContext)
}
