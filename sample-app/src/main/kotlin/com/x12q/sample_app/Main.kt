package com.x12q.sample_app

import com.x12q.kotlin.randomizer.lib.RandomConfig
import com.x12q.kotlin.randomizer.lib.RandomContextBuilderFunctions.constant
import com.x12q.kotlin.randomizer.lib.RandomContextBuilderFunctions.double
import com.x12q.kotlin.randomizer.lib.RandomContextBuilderFunctions.int
import com.x12q.kotlin.randomizer.lib.RandomContextBuilderFunctions.long
import com.x12q.kotlin.randomizer.lib.RandomContextBuilderFunctions.string
import java.util.Date
import com.x12q.kotlin.randomizer.lib.random
import kotlinx.serialization.Serializable
import kotlin.random.Random

data class ABC<T, E>(val qxParam: T, val map: Map<Double, String>, val l: List<E>, val q: Date)
data class XYZ<T, E>(val t: T, val e:E)
data class Q(val i:ABC<String,Int>)
data class QxC<Z>(val z:Z)

@Serializable
data class SE(val i:Int)


fun main() {
    val rdConfig = RandomConfig.defaultWith(random = Random(123))
    println(random<SE>(randomConfig = rdConfig))
    println(random<Int>(randomizers = {int(123)}))
    println(random<XYZ<Int,Long>>(randomizers = {
        int(123)
        long(456L)
    }))
    println(random<QxC<XYZ<Int,Long>>>(randomizers = {
        int(123)
        long{
            random<Int>().toLong()
        }
    }))
}
