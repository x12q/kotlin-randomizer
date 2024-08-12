package com.x12q.sample_app

import com.x12q.randomizer.lib.annotations.Randomizable

@Randomizable
data class Qx<T>(val qxParam:T)

fun main(){
    println(Qx.random<Int>(randomT={777}))
}



