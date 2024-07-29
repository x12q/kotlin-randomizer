package com.x12q.randomizer.ir_plugin

import com.x12q.randomizer.RandomConfig
import com.x12q.randomizer.ir_plugin.TestPassingRandomizerBuilder.Dt
import com.x12q.randomizer.lib.randomizer.*
import com.x12q.randomizer.test.util.WithData


data class AB(val i: Int, val x: Double, val s: String) {
    val c = Int

    companion object {

        /**
         * R1
         */
        fun random(randomizers: ClassRandomizerCollectionBuilder.() -> Unit = {}): ClassRandomizerCollectionBuilder {
            println("random1_2")
            val builder = ClassRandomizerCollectionBuilderImp()
            randomizers(builder)
            val collection = builder.build()
            return builder
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


data class B2(
    val i:Int
)

data class M2(
    val b2:B2,
)

fun main() {
    val builder = AB.random{
        add(FactoryClassRandomizer<Dt>({ Dt(-999) }, Dt::class))
    }
    val collection = builder.build()

    M2(
        b2 = collection.random<B2>() ?: B2(i = collection.random<Int>() ?: 123),
    )


}


class X(override val data: AB) :WithData
