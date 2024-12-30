package com.x12q.kotlin.randomizer.lib

import kotlin.random.Random

data class RandomConfigImp(
    override val random: Random,
    override val collectionSizeRange: IntRange,
    override val charRange: CharRange,
    override val stringSize: IntRange,
    override val stringCandidates: List<Char>,
    val candidateIndex: Int?
) : RandomConfig{
    @ForKotlinRandomizerGeneratedCodeOnly
    override fun randomizableCandidateIndex(candidateCount: Int): Int {
        return candidateIndex ?: super.randomizableCandidateIndex(candidateCount)
    }
}
