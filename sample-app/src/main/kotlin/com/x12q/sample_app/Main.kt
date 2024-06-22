package com.x12q.sample_app

import com.x12q.randomizer.annotations.Randomizable
import kotlin.reflect.typeOf


fun main(){
    val n = Q123.random()
    println(n)
    println("zbc")
//    someFunction()
}
@Randomizable
data class Q123(
    val i:Int
)

//fun someFunction() {
//
//}
