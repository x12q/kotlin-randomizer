package com.x12q.randomizer.lib

import com.x12q.randomizer.lib.randomizer.ClassRandomizer

class MutableRandomizerCollection(
    initMap: Map<TypeKey, ClassRandomizer<*>>
) : RandomizerCollection {

    private val _randomizersMap: MutableMap<TypeKey, ClassRandomizer<*>> = initMap.toMutableMap()
    override val randomizersMap: Map<TypeKey, ClassRandomizer<*>> get() = _randomizersMap

    override fun add(key: TypeKey, randomizer: ClassRandomizer<*>) {
        _randomizersMap.put(key,randomizer)
    }
}
