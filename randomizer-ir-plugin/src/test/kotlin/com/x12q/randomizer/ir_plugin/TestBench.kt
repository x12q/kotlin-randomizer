package com.x12q.randomizer.ir_plugin

import com.x12q.randomizer.DefaultRandomConfig
import com.x12q.randomizer.RandomConfig
import com.x12q.randomizer.lib.randomizer.*


data class AB(val i: Int, val x: Double, val s: String) {
    val c = Int

    companion object {

        /**
         * R1
         */
        fun random(randomizers: ClassRandomizerCollectionBuilder.() -> Unit = {}): AB {
            println("random1_2")
            val builder = ClassRandomizerCollectionBuilderImp()
            randomizers(builder)
            val collection = builder.build()
            return AB(
                i = collection.random<Int>() ?: 1,
                x = collection.random<Double>() ?: 2.0,
                s = collection.random<String>() ?: "zzz",
            )
        }

        /**
         * R2
         */
        fun random(randomConfig: RandomConfig, randomizers: ClassRandomizerCollectionBuilder.() -> Unit = {}): AB {
            val builder = ClassRandomizerCollectionBuilderImp()
            randomizers(builder)
            val z = builder.build()
            val i = z.getRandomizer<Int>()?.random() ?: randomConfig.nextInt()
            val x = z.getRandomizer<Double>()?.random() ?: randomConfig.nextDouble()
            val s = z.getRandomizer<String>()?.random() ?: randomConfig.nextStringUUID()
            return AB(i = i, x = x, s)
        }
    }
}


fun main() {
    val int = ConstantClassRandomizer(100)
    val float = ConstantClassRandomizer(2f)
    val l = listOf(
        int, float, FactoryClassRandomizer({ "abc" }, String::class)
    )
    println(AB.random{
        add(int)
        add(float)
    })
}
