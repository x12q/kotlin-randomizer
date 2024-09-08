package com.x12q.randomizer.ir_plugin

import com.x12q.randomizer.ir_plugin.mock_objects.LegalRandomConfigObject
import com.x12q.randomizer.ir_plugin.mock_objects.RandomConfigForTest
import com.x12q.randomizer.lib.*
import com.x12q.randomizer.test.util.WithData

abstract class AbstractClassQWE

object ObjectQWE1 : AbstractClassQWE()
object ObjectQWE2 : AbstractClassQWE()

inline fun <reified T : Any> makeConstantRandomizer(i: T): ClassRandomizer<T> {
    return object : ClassRandomizer<T> {
        override fun random(): T {
            return i
        }

        override val returnType: TypeKey = TypeKey.of<T>()
    }
}

val defaultRandomizerCollection = RandomContextBuilderImp().apply {
    add(makeConstantRandomizer(444))
    add(makeConstantRandomizer(33.33f))
    add(makeConstantRandomizer(666.66))
    add(makeConstantRandomizer("from default randomizer"))
    add(makeConstantRandomizer<AbstractClassQWE>(ObjectQWE2))
}.buildContext()


data class QxC<T1>(override val data: List<List<T1>>) : WithData {
    companion object {
        inline fun <reified T1 : Any> random(
            noinline randomizers: RandomContextBuilder.() -> Unit = {}
        ): QxC<T1> {
            val varRandomConfig: RandomConfig = LegalRandomConfigObject
            val varRandomContext: RandomContext = run {
                val randomContextBuilder = RandomContextBuilderImp()
                randomContextBuilder.setRandomConfigAndGenerateStandardRandomizers(randomConfig = varRandomConfig)
                randomContextBuilder.randomizers()
                randomContextBuilder.buildContext()
            }

            return QxC(data = run {
                val randomResult: List<List<T1>> = run {
                    val randomFromContext: List<List<T1>>? = varRandomContext.random<List<List<T1>>>()

                    if (randomFromContext == null) {
                        List(varRandomConfig.randomCollectionSize()) {
                            List(varRandomConfig.randomCollectionSize()) {
                                val innerRandomResult: T1? = varRandomContext.random<T1>()

                                if (innerRandomResult == null) {
                                    throw UnableToMakeRandomException(
                                        targetClassName = T1::class.simpleName,
                                        paramName = null,
                                        type = "T1"
                                    )
                                } else {
                                    innerRandomResult
                                }
                            }
                        }
                    } else {
                        randomFromContext
                    }
                }

                if (randomResult == null) {
                    throw UnableToMakeRandomException(
                        targetClassName = T1::class.simpleName,
                        paramName = null,
                        type = "List<List<T1>>"
                    )
                } else {
                    randomResult
                }
            })
        }
    }
}


fun main() {
    println(QxC.random<Int>())
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
            noinline randomTypeT1: (RandomContext.() -> T1)?,
            noinline randomTypeT2: (RandomContext.() -> T2)?,
            noinline randomTypeT3: (RandomContext.() -> T3)?,
            randomizers: RandomContextBuilder.() -> Unit = {},
        ): ABC<T1, T2, T3> {

            val randomConfig = RandomConfigForTest

            val context = run {
                val builder = RandomContextBuilderImp()
                randomizers(builder)
                builder.setRandomConfig(randomConfig)
                builder.buildContext()
            }

            val t1 = randomTypeT1?.invoke(context)
                ?: context.random<T1>()
                ?: defaultRandomizerCollection.random<T1>()

            val t1_2 = randomTypeT1?.invoke(context)
                ?: context.random<T1>()
                ?: defaultRandomizerCollection.random<T1>()

            val t2 = randomTypeT2?.invoke(context)
                ?: context.random<T2>()
                ?: defaultRandomizerCollection.random<T2>()

            val _absVal = context.random<AbstractClassQWE>()
                ?: defaultRandomizerCollection.random<AbstractClassQWE>()

            val _i2 = context.random<Int>() ?: defaultRandomizerCollection.random<Int>()

            return ABC(
                t1Val1 = t1 ?: throw IllegalArgumentException("t1Val1"),
                t1Val2 = t1_2 ?: throw IllegalArgumentException("t1Val2"),
                t2Val = t2 ?: throw IllegalArgumentException("t2"),
                absVal = _absVal ?: throw IllegalArgumentException("absVal"),
                i2 = _i2 ?: throw IllegalArgumentException("i2"),
                innerClass = context.random<InnerClass<T3>>()
                    ?: InnerClass(
                        tVal = randomTypeT3?.invoke(context) ?: defaultRandomizerCollection.random<T3>()!!,
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
        fun random(randomizers: RandomContextBuilder.() -> Unit = {}): RandomContextBuilder {
            println("random1_2")
            val builder = RandomContextBuilderImp()
            randomizers(builder)
            val collection = builder.buildContext()
            return builder
        }

        /**
         * R2
         */
        fun random(randomConfig: RandomConfig, randomizers: RandomContextBuilder.() -> Unit = {}): AB {
            val builder = RandomContextBuilderImp()
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
            randomT1: RandomizerCollection2.(RandomConfig) -> T1,
            randomT2: RandomizerCollection2.(RandomConfig) -> T2,
            randomizers: RandomContextBuilder.() -> Unit = {}
        ): CD<T1, T2> {
            val builder = RandomContextBuilderImp()
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


