package com.x12q.randomizer.lib



fun <T:Any> random(
    makeRandom: (randomContext: RandomContext)->T = {
        throw IllegalArgumentException("makeRandom is supposed to be replaced by a generated substitute.")
    },
    randomConfig: RandomConfig = RandomConfigImp.default,
    randomizers: RandomContextBuilder.()-> Unit = {}
):T{
    val randomContextBuilder = RandomContextBuilderImp()
    randomContextBuilder.setRandomConfigAndGenerateStandardRandomizers(randomConfig)
    randomContextBuilder.randomizers()
    val randomContext = randomContextBuilder.build()
    return makeRandom(randomContext)
    // val t = randomContext.random<T>()
    // if(t!=null){
    //     return t
    // }else{
    //
    // }
}
