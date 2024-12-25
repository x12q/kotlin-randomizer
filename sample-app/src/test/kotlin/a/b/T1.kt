package a.b

import com.x12q.kotlin.randomizer.lib.random
import kotlin.test.Test
import kotlin.test.assertEquals

class T1 {

    data class ABC<T>(val i: Int,  val t:T)
    @Test
    fun qweQEW(){
        println(random<ABC<Double>>())
    }
}
