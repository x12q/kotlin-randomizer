package com.x12q.randomizer.sample_app

import kotlinx.serialization.Serializable

fun main(){
//    makeRandomInstance<Int>({1}).also {
//        println(it)
//    }

    ABC(1,"abc")
    makeRandomInstance<ABC>().also {
        println(it)
    }
    someFunction()
}

@Serializable
data class ABC(
    val numberx: Int,
    val text123: String,
)

fun someFunction() {

}

fun <T> makeRandomInstance(f:(()->T)? = null):T?{
    return f?.invoke()
}
