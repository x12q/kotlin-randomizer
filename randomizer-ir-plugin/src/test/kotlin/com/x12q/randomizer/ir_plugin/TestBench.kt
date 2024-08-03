package com.x12q.randomizer.ir_plugin

import com.x12q.randomizer.ir_plugin.mock_objects.RandomConfigForTest
import com.x12q.randomizer.lib.*
import kotlin.reflect.KClass

abstract class AbstractClassQWE

object ObjectQWE1 : AbstractClassQWE()
object ObjectQWE2 : AbstractClassQWE()

inline fun <reified T : Any> makeConstantRandomizer(i: T): ClassRandomizer<T> {
    return object : ClassRandomizer<T> {
        override fun random(): T {
            return i
        }

        override val returnType: KClass<T> = T::class
    }
}

val defaultRandomizerCollection = RandomizerCollectionBuilderImp().apply {
    add(makeConstantRandomizer(444))
    add(makeConstantRandomizer(33.33f))
    add(makeConstantRandomizer(666.66))
    add(makeConstantRandomizer("from default randomizer"))
    add(makeConstantRandomizer<AbstractClassQWE>(ObjectQWE2))

}.build()

fun main() {

    println(
        ABC.random<Double, String, Float>(
            randomTypeT1 = { _, _ ->
                33.33
            },
            randomTypeT2 = { _, _ ->
                "T2"
            },
            randomTypeT3 = { _, _ ->
                1.2f
            },
            randomT1Val1 = { _, _ ->
                12.3
            },
            randomT2Val = { _, _ ->
                "T2_t2"
            },
            randomAbsVal = { _, _ ->
                ObjectQWE1
            },
            randomizers = {
                add(makeConstantRandomizer(999))
                add(makeConstantRandomizer<AbstractClassQWE>(ObjectQWE2))
            }
        )
    )
}


data class ABC<T1 : Number, T2, T3>(
    val t1Val1: T1,
    val t1Val2: T1,
    val t2Val: T2,
    val absVal: AbstractClassQWE,
    val i2: Int,
    val innerClass: InnerClass<T3>
) {
    companion object {
        inline fun <reified T1 : Number, reified T2 : Any, reified T3 : Any> random(
            noinline randomTypeT1: ((RandomConfig, RandomizerCollection) -> T1)? = null,
            noinline randomTypeT2: ((RandomConfig, RandomizerCollection) -> T2)? = null,
            noinline randomTypeT3: (RandomConfig, RandomizerCollection) -> T3,

            noinline randomT1Val1: ((RandomConfig, RandomizerCollection) -> T1)? = null,
            noinline randomT1Val2: ((RandomConfig, RandomizerCollection) -> T1)? = null,
            noinline randomT2Val: ((RandomConfig, RandomizerCollection) -> T2)? = null,
            noinline randomAbsVal: ((RandomConfig, RandomizerCollection) -> AbstractClassQWE)? = null,
            noinline randomI2: (() -> Int)? = null,
            noinline randomInnerClass: ((RandomConfig, RandomizerCollection) -> InnerClass<T3>)? = null,

            randomizers: RandomizerCollectionBuilder.() -> Unit = {},
        ): ABC<T1, T2, T3> {
            val builder = RandomizerCollectionBuilderImp()
            randomizers(builder)
            val collection = builder.build()
            val randomConfig = RandomConfigForTest

            val t1 = (randomT1Val1?.invoke(randomConfig, collection) ?: randomTypeT1?.invoke(randomConfig, collection))
                ?: collection.random<T1>() ?: defaultRandomizerCollection.random<T1>()
            val t1_2 =
                (randomT1Val2?.invoke(randomConfig, collection) ?: randomTypeT1?.invoke(randomConfig, collection))
                    ?: collection.random<T1>() ?: defaultRandomizerCollection.random<T1>()

            val t2 = (randomT2Val?.invoke(randomConfig, collection) ?: randomTypeT2?.invoke(randomConfig, collection))
                ?: collection.random<T2>() ?: defaultRandomizerCollection.random<T2>()
            val absVal = randomAbsVal?.invoke(randomConfig, collection) ?: collection.random<AbstractClassQWE>()
            ?: defaultRandomizerCollection.random<AbstractClassQWE>()
            val i2 = (randomI2?.invoke() ?: collection.random<Int>()) ?: defaultRandomizerCollection.random<Int>()
            return ABC(
                t1Val1 = t1 ?: throw IllegalArgumentException("t1Val1"),
                t1Val2 = t1_2 ?: throw IllegalArgumentException("t1Val2"),
                t2Val = t2 ?: throw IllegalArgumentException("t2"),
                absVal = absVal ?: throw IllegalArgumentException("absVal"),
                i2 = i2 ?: throw IllegalArgumentException("i2"),
                innerClass = randomInnerClass?.invoke(randomConfig, collection) ?: collection.random<InnerClass<T3>>()
                ?: InnerClass.random(
                    randomTypeT = randomTypeT3,
                    randomTVal = randomTypeT3,
                    randomI = { _, _ ->
                        collection.random<Int>() ?: defaultRandomizerCollection.random<Int>()
                        ?: throw IllegalArgumentException("randomI")
                    },
                    aboveCollection = collection
                )
            )
        }
    }
}

data class InnerClass<T>(
    val tVal: T,
    val i: Int
) {
    companion object {
        inline fun <reified T : Any> random(
            noinline randomTypeT: ((RandomConfig, RandomizerCollection) -> T)? = null,
            noinline randomTVal: ((RandomConfig, RandomizerCollection) -> T)? = null,
            noinline randomI: ((RandomConfig, RandomizerCollection) -> Int)? = null,
            randomizers: RandomizerCollectionBuilder.() -> Unit = {},
            aboveCollection: RandomizerCollection? = null,
        ): InnerClass<T> {

            val collection = run {
                if(aboveCollection!=null){
                    aboveCollection
                }else{
                    val builder = RandomizerCollectionBuilderImp()
                    randomizers(builder)
                    builder.build()
                }
            }
            val randomConfig = RandomConfigForTest

            val tVal = randomTVal?.invoke(randomConfig, collection) ?: randomTypeT?.invoke(randomConfig, collection)
            ?: collection.random<T>() ?: defaultRandomizerCollection.random<T>()
            val i = randomI?.invoke(randomConfig, collection) ?: collection.random<Int>()
            ?: defaultRandomizerCollection.random<Int>() ?: defaultRandomizerCollection.random<Int>()

            return InnerClass(
                tVal = tVal ?: throw IllegalArgumentException("InnerClass.tVal"),
                i = i ?: throw IllegalArgumentException("InnerClass.i")
            )
        }
    }
}


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
        inline fun <reified T1 : Any, reified T2 : Any> random(
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
