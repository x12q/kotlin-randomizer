package com.x12q.randomizer.lib

import com.x12q.randomizer.lib.util.developerErrorMsg


fun <T> random(
    makeRandom: ((randomContext: RandomContext)->T)? = null,
    randomConfig: RandomConfig,
    addRandomizers: RandomContextBuilder.()-> Unit = {}
):T{
    if(makeRandom!=null){
        val randomContextBuilder = RandomContextBuilderImp()
        randomContextBuilder.setRandomConfigAndGenerateStandardRandomizers(randomConfig)
        randomContextBuilder.addRandomizers()
        val randomContext = randomContextBuilder.build()
        return makeRandom(randomContext)
    }else{
        throw IllegalArgumentException(developerErrorMsg("makeRandom lambda cannot be null"))
    }
}

fun <T> random(
    makeRandom: ((randomContext: RandomContext)->T)? = null,
    addRandomizers: RandomContextBuilder.()-> Unit = {}
):T{
    return random(makeRandom, RandomConfigImp.default, addRandomizers)
}
