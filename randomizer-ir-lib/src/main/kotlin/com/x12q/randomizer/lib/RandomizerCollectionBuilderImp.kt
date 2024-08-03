package com.x12q.randomizer.lib

class RandomizerCollectionBuilderImp: RandomizerCollectionBuilder {
    private val randomizers:MutableList<ClassRandomizer<*>> = mutableListOf()

    override fun add(randomizer: ClassRandomizer<*>): RandomizerCollectionBuilder {
        randomizers.add(randomizer)
        return this
    }

    override fun build(): RandomizerCollection {
        return RandomizerCollectionImp(randomizers.associateBy { it.returnType })
    }
}
