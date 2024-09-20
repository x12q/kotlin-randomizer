package com.x12q.randomizer.lib

import kotlin.random.Random

data class RandomConfigImp(
    override val random: Random,
    override val collectionSizeRange: IntRange,
    override val charRange: CharRange,
    override val stringSize: IntRange,
    override val stringCandidates: List<Char>,
) : RandomConfig {

    companion object{
        val default = default()
        fun default(
            random: Random = Random,
            collectionSizeRange: IntRange = 0 .. 10,
            charRange: CharRange = 'A' .. 'z',
            stringSize: IntRange = 1 .. 20,
            stringCandidates: List<Char> = charRange.toList(),
        ):RandomConfigImp{
            return RandomConfigImp(
                random = random,
                collectionSizeRange = collectionSizeRange,
                charRange = charRange,
                stringSize = stringSize,
                stringCandidates = stringCandidates,
            )
        }
    }
}
