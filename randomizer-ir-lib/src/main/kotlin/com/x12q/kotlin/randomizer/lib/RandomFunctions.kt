package com.x12q.kotlin.randomizer.lib

import com.x12q.kotlin.randomizer.lib.util.developerErrorMsg


fun <T> random(
    makeRandom: (randomContext: RandomContext) -> T = {
        throw IllegalArgumentException(developerErrorMsg("makeRandom is supposed to be replaced by a generated substitute."))
    },
    randomConfig: RandomConfig = RandomConfig.default,
    randomizers: RandomContextBuilder.() -> Unit = {}
): T {
    val randomContextBuilder = RandomContextBuilderImp()
    randomContextBuilder.setRandomConfigAndGenerateStandardRandomizers(randomConfig)
    randomContextBuilder.randomizers()
    val randomContext = randomContextBuilder.build()
    return makeRandom(randomContext)
}
