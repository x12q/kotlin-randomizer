package com.x12q.randomizer.randomizer.primitive.non_base.type_int

import kotlin.random.Random


/**
 * This can generate random int within a range
 */
class RangeIntRandomizer (
    intRange: IntRange,
    random:Random = Random,
) : AbsIntRandomizer({
    intRange.random(random)
})
