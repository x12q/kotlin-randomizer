package com.x12q.randomizer.sample_app


fun main(){

    ABC(1,"abc")
    val q = Q()
    println(q.random<ABC>())
    println(q.random<XCV>())
//    println(makeRandomInstance<ABC>())
    someFunction()
}

class Q{
    fun <T> random():T?{
        return makeRandomInstance<T>()
    }
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
