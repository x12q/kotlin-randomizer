package com.x12q.sample_app

import com.x12q.randomizer.lib.RandomContextBuilderFunctions.constant
import com.x12q.randomizer.lib.RandomContextBuilderFunctions.factory
import com.x12q.randomizer.lib.annotations.Randomizable
import java.util.Date

@Randomizable
data class Qx<T,E>(val qxParam:T, val map:Map<Double,String>, val l:List<E>, val q: Date)

fun main(){
    println(Qx.random<Int,Long>(randomizers={
        constant{666}
        factory{ listOf("a","b").random() }
    }))
}
