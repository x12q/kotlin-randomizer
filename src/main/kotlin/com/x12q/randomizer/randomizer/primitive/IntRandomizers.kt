package com.x12q.randomizer.randomizer.primitive

import com.x12q.randomizer.randomizer.ClassRandomizer
import com.x12q.randomizer.randomizer.RDClassData
import com.x12q.randomizer.randomizer.clazz.SameClassRandomizer
import kotlin.math.absoluteValue
import kotlin.random.Random

abstract class AbsIntRandomizer(
    makeRandom: () -> Int
) : ClassRandomizer<Int> {

    private val rdm: SameClassRandomizer<Int> = SameClassRandomizer(
        returnedInstanceData = RDClassData.from<Int>(),
        makeRandom = {
            makeRandom()
        }
    )

    override val returnedInstanceData: RDClassData = rdm.returnedInstanceData

    override fun isApplicableTo(classData: RDClassData): Boolean = rdm.isApplicableTo(classData)

    override fun random(): Int = rdm.random()
}

class IntRandomizer2(
    random: Random,
) : AbsIntRandomizer({
    random.nextInt()
})

class RangeIntRandomizer private constructor(
    random: Random,
    intRange: IntRange,
) : AbsIntRandomizer({
    intRange.random(random)
})


object IntRandomizers {

}
