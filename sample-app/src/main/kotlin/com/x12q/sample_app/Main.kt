package com.x12q.randomizer.sample_app

import com.x12q.randomizer.annotations.Randomizable
import kotlinx.serialization.Serializable


fun main(){

    val q = Q123()
    Q123.Companion.randomFrom_IR()
//    println()
    println(ABC(1,"abc"))

//    println(q)
//    Q1.random()
//    println(Q1.serializer()

//    Q1.random()
//    Q123123::
//    Q123123.randomX123()
//    println(q.random<ABC>())
//    println(q.random<XCV>())
//    println(makeRandomInstance<ABC>())
    someFunction()
}
@Randomizable
//@Serializable
class Q123{

}


data class ABC(
    val numberx: Int,
    val text123: String,
)

data class XCV(
    val f:Int
)

fun someFunction() {

}

fun <T> makeRandomInstance(f:(()->T)? = null):T?{
    return f?.invoke()
}
