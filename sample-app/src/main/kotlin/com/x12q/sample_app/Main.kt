package com.x12q.sample_app

import com.x12q.randomizer.DefaultRandomConfig
import com.x12q.randomizer.RandomConfig
import com.x12q.randomizer.annotations.Randomizable
import kotlin.random.Random
import kotlin.reflect.typeOf




object Oo:RandomConfig{
    override val charRange: CharRange = 'A' .. 'z'
    override val collectionSizeRange: IntRange = 5 .. 5
    override val random: Random = Random
}
fun main(){
    val n = Q123.random()
    println(n)

}

@Randomizable(randomConfig = Oo::class)
data class Q123(
    val i:UInt
)


