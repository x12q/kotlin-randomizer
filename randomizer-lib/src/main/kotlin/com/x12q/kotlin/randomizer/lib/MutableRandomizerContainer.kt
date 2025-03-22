package com.x12q.kotlin.randomizer.lib

import com.x12q.kotlin.randomizer.lib.randomizer.ClassRandomizer
import com.x12q.kotlin.randomizer.lib.randomizer.CustomRandomizer

class MutableRandomizerContainer(
    initMap: Map<TypeKey, ClassRandomizer<*>>,
    initPropertyMap:Map<PropertyKey, CustomRandomizer<*>>,
) : RandomizerContainer {

    private val _randomizersMap: MutableMap<TypeKey, ClassRandomizer<*>> = initMap.toMutableMap()
    override val randomizersMap: Map<TypeKey, ClassRandomizer<*>> get() = _randomizersMap

    private val _randomizersMapByPropertyKey: MutableMap<PropertyKey, CustomRandomizer<*>> = initPropertyMap.toMutableMap()
    override val propertyRandomizersMap: Map<PropertyKey, CustomRandomizer<*>> = _randomizersMapByPropertyKey

    override fun add(key: TypeKey, randomizer: ClassRandomizer<*>): MutableRandomizerContainer{
        _randomizersMap.put(key,randomizer)
        return this
    }

    override fun add(randomizer: ClassRandomizer<*>): MutableRandomizerContainer {
        return this.add(randomizer.returnType,randomizer)
    }

    override fun remove(key: TypeKey):MutableRandomizerContainer{
        _randomizersMap.remove(key)
        return this
    }

    override fun removeAll(): RandomizerContainer {
        _randomizersMap.clear()
        return this
    }
}
