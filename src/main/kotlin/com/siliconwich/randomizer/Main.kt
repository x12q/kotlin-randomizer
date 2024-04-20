package com.siliconwich.randomizer


data class ABC(val lst:List<Float>)
data class ABC2(val a:ABC, val t:String)
data class Q<T>(val t:T)

fun main(){
    val abc = RandomizerOut.makeRandomInstance<ABC>()
    println(abc)
    val q= RandomizerOut.makeRandomInstance<Q<Int>>()
    println(q)

    println(RandomizerOut.makeRandomInstance<ABC2>())

}
