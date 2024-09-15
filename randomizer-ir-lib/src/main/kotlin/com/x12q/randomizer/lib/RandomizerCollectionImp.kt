package com.x12q.randomizer.lib

import com.x12q.randomizer.lib.randomizer.ClassRandomizer

data class RandomizerCollectionImp(
    override val randomizersMap: Map<TypeKey, ClassRandomizer<*>>
) : RandomizerCollection {
    override fun add(key: TypeKey, randomizer: ClassRandomizer<*>): RandomizerCollection {
        return this.copy(
            randomizersMap = randomizersMap + (key to randomizer)
        )
    }

    companion object{
        fun empty():RandomizerCollectionImp{
            return RandomizerCollectionImp(emptyMap())
        }
    }
}
