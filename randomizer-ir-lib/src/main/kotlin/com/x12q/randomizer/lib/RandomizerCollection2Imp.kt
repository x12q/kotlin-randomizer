package com.x12q.randomizer.lib

data class RandomizerCollection2Imp(
    override val randomizersMap: Map<TypeKey, ClassRandomizer<*>>
) : RandomizerCollection2 {
    override fun add(key: TypeKey, randomizer: ClassRandomizer<*>): RandomizerCollection2 {
        return this.copy(
            randomizersMap = randomizersMap + (key to randomizer)
        )
    }
}
