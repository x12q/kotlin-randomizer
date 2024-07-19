package com.x12q.randomizer.ir_plugin

import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
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

@Serializable
data class Q<T>(
    val x:Int,
//    val t:T
)

@Serializable
data class MMM(val i:Int)

fun main(){
//    val str = Json.encodeToString(Q(1,2))
//    val q = Json.decodeFromString<Q<Int>>(str)
//    Q.serializer()
    println()
    val serializer = Q.serializer(Float.serializer())
}
