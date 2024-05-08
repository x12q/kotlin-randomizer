package com.x12q.randomizer.randomizer.clazz

import com.x12q.randomizer.randomizer.ClassRandomizer
import com.x12q.randomizer.lookup_node.RDClassData

/**
 * A [ClassRandomizer] whose condition checking is to check if the target class is the same as [returnedInstanceData]
 */
class SameClassRandomizer<T> private constructor(
    private val conditionalRandomizer: ConditionalClassRandomizer<T>
) : ClassRandomizer<T> by conditionalRandomizer {
    constructor(
        returnedInstanceData: RDClassData,
        makeRandom: () -> T
    ) : this(
        ConditionalClassRandomizer(
            returnedInstanceData, condition = { targetClass, returnedInstanceClass ->
                targetClass == returnedInstanceClass
            }, makeRandomIfApplicable = makeRandom
        )
    )
}
