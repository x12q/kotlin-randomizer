package com.x12q.randomizer.ir_plugin

import com.x12q.randomizer.RandomConfig
import com.x12q.randomizer.ir_plugin.mock_objects.RandomConfigForTest
import com.x12q.randomizer.lib.randomizer.*
import com.x12q.randomizer.test.util.WithData


data class AB(val i: Int, val x: Double, val s: String) {
    val c = Int

    companion object {

        /**
         * R1
         */
        fun random(randomizers: RandomizerCollectionBuilder.() -> Unit = {}): RandomizerCollectionBuilder {
            println("random1_2")
            val builder = RandomizerCollectionBuilderImp()
            randomizers(builder)
            val collection = builder.build()
            return builder
        }

        /**
         * R2
         */
        fun random(randomConfig: RandomConfig, randomizers: RandomizerCollectionBuilder.() -> Unit = {}): AB {
            val builder = RandomizerCollectionBuilderImp()
            randomizers(builder)
            val z = builder.build()
            val i = z.getRandomizer<Int>()?.random() ?: randomConfig.nextInt()
            val x = z.getRandomizer<Double>()?.random() ?: randomConfig.nextDouble()
            val s = z.getRandomizer<String>()?.random() ?: randomConfig.nextStringUUID()
            return AB(i = i, x = x, s)
        }
    }
}


data class CD<T1, T2>(val t1: T1, val t2: T2) {
    companion object {
        // is this a good design?
        /**
         * When there are generic, I want user to explicitly
         */
        inline fun <reified T1:Any, reified T2:Any> random(
            randomT1: RandomizerCollection.(RandomConfig) -> T1,
            randomT2: RandomizerCollection.(RandomConfig) -> T2,
            randomizers: RandomizerCollectionBuilder.() -> Unit = {}
        ): CD<T1, T2> {
            val builder = RandomizerCollectionBuilderImp()
            randomizers(builder)
            val collection = builder.build()
            val config = RandomConfigForTest
            return CD(
                t1 = collection.random<T1>() ?: collection.randomT1(config),
                t2 = collection.random<T2>() ?: collection.randomT2(config)
            )
        }
    }
}


data class B2(
    val i: Int
)

data class M2(
    val b2: B2,
)

fun main() {

    println(
        CD.random<Int,String>(
            randomT1 = {
                random<Int>()!!
            },
            randomT2 = {
                "abczxc"
            }
        ){
            add(ConstantClassRandomizer<Int>(-123))
        }
    )



//    val builder = AB.random{
//        add(FactoryClassRandomizer<Dt>({ Dt(-999) }, Dt::class))
//    }
//    val collection = builder.build()
//
//    M2(
//        b2 = collection.random<B2>() ?: B2(i = collection.random<Int>() ?: 123),
//    )
}


class X(override val data: AB) : WithData
