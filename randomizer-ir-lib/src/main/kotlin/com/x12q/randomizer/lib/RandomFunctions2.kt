package com.x12q.randomizer.lib


fun<T> zxc():T{
    return null!!
}
fun <T> random(
    makeRandom: (randomContext: RandomContext)->T = {
        zxc<T>()
    },
    randomConfig: RandomConfig,
    addRandomizers: RandomContextBuilder.()-> Unit = {}
):T{
    val randomContextBuilder = RandomContextBuilderImp()
    randomContextBuilder.setRandomConfigAndGenerateStandardRandomizers(randomConfig)
    randomContextBuilder.addRandomizers()
    val randomContext = randomContextBuilder.build()
    return makeRandom(randomContext)
}

fun <T> random(
    makeRandom: (randomContext: RandomContext)->T = {
        zxc<T>()
    },
    addRandomizers: RandomContextBuilder.()-> Unit = {}
):T{
    return random(makeRandom, RandomConfigImp.default, addRandomizers)
}
