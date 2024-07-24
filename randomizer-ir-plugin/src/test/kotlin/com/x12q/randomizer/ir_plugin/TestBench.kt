package com.x12q.randomizer.ir_plugin

import com.x12q.randomizer.RandomConfig
import io.kotest.matchers.types.shouldBeSameInstanceAs
import io.kotest.matchers.types.shouldBeTypeOf
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.random.Random
import kotlin.reflect.KClass


interface ClassRandomizer<T : Any> {
    fun random(): T
    val returnType: KClass<out T>
}

class ConstantRandomizerG<T:Any>(val i: T) : ClassRandomizer<T> {
    override val returnType: KClass<out T> = i::class
    override fun random(): T {
        return i
    }
}

class VLRandomizer<T : Any>(val rd: () -> T, override val returnType: KClass<out T>) : ClassRandomizer<T> {
    override fun random(): T {
        return rd()
    }
}

interface ClassRandomizerCollection {
    val randomizers: List<ClassRandomizer<*>>
    fun <T> getRandomizerFor(): ClassRandomizer<*>?
}

class ClassRandomizerCollectionImp(
    override val randomizers: List<ClassRandomizer<*>>
) : ClassRandomizerCollection {
    override fun <T> getRandomizerFor(): ClassRandomizer<*>? {
        TODO()
    }
}

inline fun <reified T:Any> List<ClassRandomizer<*>>.getFor(): ClassRandomizer<T>? {
    val rt = this.firstOrNull {
        it.returnType == T::class
    }
    return rt?.let { it as? ClassRandomizer<T> }
}

class AB(val i: Int) {
    val c = Int

    companion object {
        fun random(randomConfig: RandomConfig, randomizers: List<ClassRandomizer<*>>) {
            TODO()
        }
    }
}


fun main() {
    val int = ConstantRandomizerG(19)
    val float = ConstantRandomizerG(132.2f)
    val  l = listOf(
        int, float,VLRandomizer({"abc"},String::class)
    )

    println(l.getFor<Int>()?.random())
    println(l.getFor<Float>()?.random())
    println(l.getFor<String>()?.random())
}
