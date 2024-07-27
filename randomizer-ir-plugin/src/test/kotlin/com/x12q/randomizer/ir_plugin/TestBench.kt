package com.x12q.randomizer.ir_plugin

import com.x12q.randomizer.DefaultRandomConfig
import com.x12q.randomizer.RandomConfig
import com.x12q.randomizer.lib.randomizer.*


data class AB(val i: Int, val x:Double, val s:String) {
    val c = Int

    companion object {
        fun random(randomConfig: RandomConfig, randomizers: ClassRandomizerCollection): AB {
            val i = randomizers.getRandomizer<Int>()?.random() ?: randomConfig.nextInt()
            val x = randomizers.getRandomizer<Double>()?.random() ?: randomConfig.nextDouble()
            val s = randomizers.getRandomizer<String>()?.random() ?: randomConfig.nextStringUUID()
            return AB(i=i,x=x,s)
        }
    }
}


fun main() {
    val int = ConstantClassRandomizer(1)
    val float = ConstantClassRandomizer(2f)
    val  l = listOf(
        int, float, FactoryClassRandomizer({"abc"},String::class)
    )
    println(AB.random(DefaultRandomConfig.default, ClassRandomizerCollectionImp(l)))
}
