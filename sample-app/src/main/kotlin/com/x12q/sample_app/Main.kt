package com.x12q.sample_app

import com.x12q.kotlin.randomizer.lib.RandomContextBuilderFunctions.constant
import com.x12q.kotlin.randomizer.lib.RandomContextBuilderFunctions.double
import com.x12q.kotlin.randomizer.lib.RandomContextBuilderFunctions.int
import com.x12q.kotlin.randomizer.lib.RandomContextBuilderFunctions.long
import com.x12q.kotlin.randomizer.lib.RandomContextBuilderFunctions.string
import java.util.Date
import com.x12q.kotlin.randomizer.lib.random

data class ABC<T, E>(val qxParam: T, val map: Map<Double, String>, val l: List<E>, val q: Date)
data class XYZ<T, E>(val t: T, val e:E)
// data class Q(val i:ABC<String,Int>)
data class QxC<Z>(val z:Z)

fun main() {
    println(random<Int>(randomizers = {int(123)}))
    println(random<XYZ<Int,Long>>(randomizers = {
        int(123)
        long(456L)
    }))
    println(random<QxC<XYZ<Int,Long>>>(randomizers = {
        // constant<Int>(123)
        int(123)
        long{
            random<Int>().toLong()
        }
        // double{
        //     listOf(1.0,2.0,3.0).random()
        // }
        // string{
        //     random<Int>().toString()
        // }
    }))
    // println(random<Q>())
}
