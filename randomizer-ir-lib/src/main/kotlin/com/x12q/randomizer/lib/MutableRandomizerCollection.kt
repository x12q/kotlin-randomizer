package com.x12q.randomizer.lib

import com.x12q.randomizer.lib.randomizer.ClassRandomizer

class MutableRandomizerCollection(
    initMap: Map<TypeKey, ClassRandomizer<*>>
) : RandomizerCollection {

    private val _randomizersMap: MutableMap<TypeKey, ClassRandomizer<*>> = initMap.toMutableMap()
    override val randomizersMap: Map<TypeKey, ClassRandomizer<*>> get() = _randomizersMap

    override fun add(key: TypeKey, randomizer: ClassRandomizer<*>): MutableRandomizerCollection{
        _randomizersMap.put(key,randomizer)
        return this
    }

    override fun add(randomizer: ClassRandomizer<*>): MutableRandomizerCollection {
        return this.add(randomizer.returnType,randomizer)
    }

    override fun remove(key: TypeKey):MutableRandomizerCollection{
        _randomizersMap.remove(key)
        return this
    }

    override fun removeAll(): RandomizerCollection {
        _randomizersMap.clear()
        return this
    }
}
