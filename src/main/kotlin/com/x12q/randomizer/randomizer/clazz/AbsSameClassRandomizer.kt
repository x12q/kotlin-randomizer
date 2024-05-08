package com.x12q.randomizer.randomizer.clazz

import com.x12q.randomizer.randomizer.ClassRandomizer
import com.x12q.randomizer.lookup_node.RDClassData

/**
 * An abstract class randomizer that provides same-class checking.
 * User should extend this class if all they need in their custom randomizers is checking for the correct class.
 */
abstract class AbsSameClassRandomizer<T>:ClassRandomizer<T>{
    override fun isApplicableTo(classData: RDClassData): Boolean {
        return classData == this.returnedInstanceData
    }
}
