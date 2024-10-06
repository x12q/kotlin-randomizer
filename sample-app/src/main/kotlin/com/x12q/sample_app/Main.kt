package com.x12q.sample_app

import com.x12q.randomizer.lib.RandomContextBuilderFunctions.constant
import com.x12q.randomizer.lib.RandomContextBuilderFunctions.factory
import com.x12q.randomizer.lib.annotations.Randomizable
import com.x12q.randomizer.lib.randomizer.factoryRandomizer

@Randomizable
data class Qx<T>(val qxParam:T, val map:Map<Double,String>)

fun main(){

    println(Qx.random<Int>(randomizers={
        constant{666}
        factory{ listOf("a","b").random() }
    }))
}




