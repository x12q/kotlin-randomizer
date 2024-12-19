package com.x12q.randomizer.lib

import com.x12q.randomizer.lib.randomizer.ClassRandomizer
import com.x12q.randomizer.lib.randomizer.constantRandomizer
import com.x12q.randomizer.lib.randomizer.factoryRandomizer
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

@Deprecated("kept for reference only, don't use for now")
object CustomRandomizerRegistry {

    private val mutableRandomizerCollection = MutableRandomizerCollection(emptyMap())
    val randomizers: List<ClassRandomizer<*>> get() = mutableRandomizerCollection.randomizersMap.values.toList()

    private val mutex = Mutex()

    fun add(randomizer: ClassRandomizer<*>) {
        runBlocking {
            mutex.withLock {
                mutableRandomizerCollection.add(randomizer.returnType, randomizer)
            }
        }
    }

    fun remove(randomizer: ClassRandomizer<*>) {
        runBlocking {
            mutex.withLock {
                mutableRandomizerCollection.remove(randomizer.returnType)
            }
        }
    }

    fun remove(typeKey: TypeKey) {
        runBlocking {
            mutex.withLock {
                mutableRandomizerCollection.remove(typeKey)
            }
        }
    }

    fun removeAll() {
        runBlocking {
            mutex.withLock {
                mutableRandomizerCollection.removeAll()
            }
        }
    }
}


inline fun <reified T : Any> CustomRandomizerRegistry.addConstant(value: T) {
    add(constantRandomizer(value))
}

inline fun <reified T : Any> CustomRandomizerRegistry.addConstant(makeValue: () -> T) {
    val value = makeValue()
    addConstant(value)
}

inline fun <reified T : Any> CustomRandomizerRegistry.addFactory(noinline makeRandom: () -> T) {
    add(factoryRandomizer(makeRandom))
}
