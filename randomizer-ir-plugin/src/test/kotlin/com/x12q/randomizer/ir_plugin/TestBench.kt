package com.x12q.randomizer.ir_plugin

import kotlin.random.Random

//fun main(){
//    println(Q123.random())
//}
//@Randomizable
//class Q123

enum class E1
enum class E2{
    v1,v2
}
data class B(
    val q:List<E1>,
)

fun main(){
    E2.values().forEach {
        println(it)
    }
    E2.entries.random(Random)
    E2.entries.forEach {
        println(it)
    }
}
