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

val defaultRandomizerCollection = RandomizerContextBuilderImp().apply {
    add(makeConstantRandomizer(444))
    add(makeConstantRandomizer(33.33f))
    add(makeConstantRandomizer(666.66))
    add(makeConstantRandomizer("from default randomizer"))
    add(makeConstantRandomizer<AbstractClassQWE>(ObjectQWE2))
}.buildContext()

fun main() {

    println(
        ABC.random<Double, String, Float>(
            randomTypeT1 = {
                33.33
            },
            randomTypeT2 = {
                "T2"
            },
            randomTypeT3 = {
                1.2f
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
            /**
             * Type randomizer. Nullable or not.
             * Nullable:
             *  - encourage user to NOT provide them here
             *  - users not provide -> can still compile.
             * Non-null:
             *  - enforce user to provide them, otherwise -> not compile.
             *  - user have to provide it, no other way out. So, if they want to reused to randomizer from random config, they will have explicitly say so everytime. This is tedious.
             * How about return a nullable
             * - user can easily return a null if they don't want to specify the random logic, then that will be delegated to random config.
             *
             *
             */
            noinline randomTypeT1: RandomConfig.() -> T1?,
            noinline randomTypeT2: RandomConfig.() -> T2?,
            noinline randomTypeT3: RandomConfig.() -> T3?,
            randomizers: RandomizerContextBuilder.() -> Unit = {},
        ): ABC<T1, T2, T3> {

            val randomConfig = RandomConfigForTest

            val confiz = run {
                val builder = RandomizerContextBuilderImp()
                randomizers(builder)
                builder.setRandomConfig(randomConfig)
                builder.buildContext()
            }

            val t1 = randomTypeT1.invoke(randomConfig)
                ?: confiz.random<T1>() ?: defaultRandomizerCollection.random<T1>()
            val t1_2 = randomTypeT1.invoke(randomConfig) ?: confiz.random<T1>() ?: defaultRandomizerCollection.random<T1>()

            val t2 = randomTypeT2.invoke(randomConfig)
                ?: confiz.random<T2>() ?: defaultRandomizerCollection.random<T2>()

            val _absVal = confiz.random<AbstractClassQWE>()
                ?: defaultRandomizerCollection.random<AbstractClassQWE>()

            val _i2 = confiz.random<Int>() ?: defaultRandomizerCollection.random<Int>()

            return ABC(
                t1Val1 = t1 ?: throw IllegalArgumentException("t1Val1"),
                t1Val2 = t1_2 ?: throw IllegalArgumentException("t1Val2"),
                t2Val = t2 ?: throw IllegalArgumentException("t2"),
                absVal = _absVal ?: throw IllegalArgumentException("absVal"),
                i2 = _i2 ?: throw IllegalArgumentException("i2"),
                innerClass = confiz.random<InnerClass<T3>>()
                    ?: InnerClass(
                        tVal = randomTypeT3?.invoke(randomConfig) ?: defaultRandomizerCollection.random<T3>() !!,
                        i = 123
                    )
            )
        }
    }
}

data class InnerClass<T>(
    val tVal: T,
    val i: Int
)


data class AB(val i: Int, val x: Double, val s: String) {
    val c = Int

    companion object {

        /**
         * R1
         */
        fun random(randomizers: RandomizerContextBuilder.() -> Unit = {}): RandomizerContextBuilder {
            println("random1_2")
            val builder = RandomizerContextBuilderImp()
            randomizers(builder)
            val collection = builder.buildContext()
            return builder
        }

        /**
         * R2
         */
        fun random(randomConfig: RandomConfig, randomizers: RandomizerContextBuilder.() -> Unit = {}): AB {
            val builder = RandomizerContextBuilderImp()
            randomizers(builder)
            val z = builder.buildContext()
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
            randomizers: RandomizerContextBuilder.() -> Unit = {}
        ): CD<T1, T2> {
            val builder = RandomizerContextBuilderImp()
            randomizers(builder)
            val collection = builder.buildContext()
            val config = RandomConfigForTest
            return CD(
                t1 = collection.random<T1>() ?: collection.randomT1(config),
                t2 = collection.random<T2>() ?: collection.randomT2(config)
            )
        }
    }
}
