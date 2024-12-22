package com.x12q.sample_app

import java.util.Date
import com.x12q.kotlin.randomizer.lib.random

data class ABC<T, E>(val qxParam: T, val map: Map<Double, String>, val l: List<E>, val q: Date)
data class Q(val i:ABC<String,Int>)
fun main() {
    // println(random<ABC<Int,Long>>())
    println(random<Q>())
    // println(Qx.random<Int,Long>(randomizers={
    //     constant{666}
    //     factory{ listOf("a","b").random() }
    // }))

    // val abc1 = random<ABC<Int, Long>>(
    //     randomizers = {
    //         val randomContextBuilder = this
    //         ////
    //     }
    // )
    //
    // val abc2 = ABC.random<Int,Long>(
    //     randomConfig = RandomConfigImp.default,
    //     randomizers= {
    //         val randomContextBuilder = this
    //         ////
    //     }
    // )


}
