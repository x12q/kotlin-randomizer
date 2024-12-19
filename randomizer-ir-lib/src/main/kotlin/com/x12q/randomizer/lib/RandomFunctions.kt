package com.x12q.randomizer.lib



fun <T> random(
    makeRandom: (randomContext: RandomContext)->T = {
        throw IllegalArgumentException("makeRandom is supposed to be replaced by a generated substitute.")
    },
    randomConfig: RandomConfig = RandomConfig.default,
    randomizers: RandomContextBuilder.()-> Unit = {}
):T{
    val randomContextBuilder = RandomContextBuilderImp()
    randomContextBuilder.setRandomConfigAndGenerateStandardRandomizers(randomConfig)
    randomContextBuilder.randomizers()
    val randomContext = randomContextBuilder.build()
    return makeRandom(randomContext)
}
