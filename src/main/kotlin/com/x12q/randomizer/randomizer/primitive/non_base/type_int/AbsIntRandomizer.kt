package com.x12q.randomizer.randomizer.primitive.non_base.type_int

import com.x12q.randomizer.randomizer.ClassRandomizer
import com.x12q.randomizer.randomizer.RDClassData
import com.x12q.randomizer.randomizer.clazz.SameClassRandomizer

/**
 * Base abstract int randomizer. All convenient int randomizers extend this one
 */
abstract class AbsIntRandomizer private constructor(
    private val rdm: SameClassRandomizer<Int>
) : ClassRandomizer<Int> by rdm {

    constructor(
        makeRandom: () -> Int
    ):this(
        SameClassRandomizer(
            returnedInstanceData = RDClassData.from<Int>(),
            makeRandom = {
                makeRandom()
            }
        )
    )
}
