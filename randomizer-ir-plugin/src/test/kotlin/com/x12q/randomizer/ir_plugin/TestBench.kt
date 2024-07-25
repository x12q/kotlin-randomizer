package com.x12q.randomizer.ir_plugin

import com.x12q.randomizer.DefaultRandomConfig
import com.x12q.randomizer.RandomConfig
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
}

inline fun <reified T:Any> List<ClassRandomizer<*>>.getRandomizer(): ClassRandomizer<T>? {
    val rt = this.firstOrNull {
        it.returnType == T::class
    }
    return rt?.let { it as? ClassRandomizer<T> }
}

inline fun <reified T:Any> ClassRandomizerCollection.getRandomizer():ClassRandomizer<T>?{
    return this.randomizers.getRandomizer<T>()
}

class ClassRandomizerCollectionImp(
    override val randomizers: List<ClassRandomizer<*>>
) : ClassRandomizerCollection

data class AB(val i: Int, val x:Double, val s:String) {
    val c = Int

    companion object {
        fun random(randomConfig: RandomConfig, randomizers:ClassRandomizerCollection): AB {
            val iRdm = randomizers.getRandomizer<Int>()
            val i = if(iRdm!=null){
                iRdm.random()
            }else{
                randomConfig.nextInt()
            }

            val x = randomizers.getRandomizer<Double>()?.random() ?: randomConfig.nextDouble()
            val s = randomizers.getRandomizer<String>()?.random() ?: randomConfig.nextStringUUID()

            return AB(i=i,x=x,s)
        }
    }
}


fun main() {
    val int = ConstantRandomizerG(1)
    val float = ConstantRandomizerG(2f)
    val  l = listOf(
        int, float,VLRandomizer({"abc"},String::class)
    )
    println(AB.random(DefaultRandomConfig.default,ClassRandomizerCollectionImp(l)))
}
