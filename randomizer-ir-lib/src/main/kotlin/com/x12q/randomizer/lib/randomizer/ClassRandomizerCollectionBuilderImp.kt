package com.x12q.randomizer.lib.randomizer

class ClassRandomizerCollectionBuilderImp: ClassRandomizerCollectionBuilder {
    private val randomizers:MutableList<ClassRandomizer<*>> = mutableListOf()

    fun add(classRandomizer: ClassRandomizer<*>): ClassRandomizerCollectionBuilder {
        randomizers.add(classRandomizer)
        return this
    }

    override fun build(): ClassRandomizerCollection {
        return ClassRandomizerCollectionImp(randomizers.toList())
    }
}
