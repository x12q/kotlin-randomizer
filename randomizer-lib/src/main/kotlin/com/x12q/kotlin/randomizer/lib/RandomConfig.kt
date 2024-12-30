package com.x12q.kotlin.randomizer.lib

import kotlin.random.Random
import kotlin.random.nextUBytes
import kotlin.random.nextULong

/**
 * RandomConfig contains the basic random configuration such as:
 * - The base [Random] object (which contains the random seed)
 * - random functions for all primitive types
 * - random size for collections (map, list, set)
 * - random size for string
 * - character range
 */
interface RandomConfig {

    val random: Random
    val collectionSizeRange: IntRange
    val charRange: CharRange
    val stringSize: IntRange
    val stringCandidates: List<Char>

    fun randomCollectionSize(): Int {
        return collectionSizeRange.random(random)
    }

    fun randomStringSize(): Int {
        return stringSize.random(random)
    }

    /**
     * A random int that dictate the choosing of candidate class in [com.x12q.kotlin.randomizer.lib.annotations.Randomizable] annotation
     */
    @ForKotlinRandomizerGeneratedCodeOnly
    fun randomizableCandidateIndex(candidateCount: Int): Int {
        return nextInt() % candidateCount
    }

    fun nextAny(): Any {
        return listOf(
            nextInt(),
            nextBoolean(),
            nextFloat(),
            nextLong(),
            nextDouble(),
            nextChar(),
            nextByte(),
            nextString(),
            nextUnit()
        ).random(random)
    }

    fun nextFloat(): Float {
        return random.nextFloat()
    }

    fun nextDouble(): Double {
        return random.nextDouble()
    }

    fun nextBoolean(): Boolean {
        return random.nextBoolean()
    }

    fun nextInt(): Int {
        return random.nextInt()
    }

    fun nextLong(): Long {
        return random.nextLong()
    }

    fun nextUInt(): UInt {
        return random.nextInt().toUInt()
    }

    fun nextULong(): ULong {
        return random.nextULong()
    }

    fun nextByte(): Byte {
        return random.nextBytes(1)[0]
    }

    @OptIn(ExperimentalUnsignedTypes::class)
    fun nextUByte(): UByte {
        return random.nextUBytes(1).first()
    }

    fun nextChar(): Char {
        return charRange.random(this.random)
    }

    fun nextShort(): Short {
        return random.nextInt().toShort()
    }

    fun nextUShort(): UShort {
        return nextShort().toUShort()
    }

    fun nextString(): String {
        val strBuilder = StringBuilder()
        val strSize = randomStringSize()
        repeat(strSize) {
            strBuilder.append(stringCandidates.random(random))
        }
        return strBuilder.toString()
    }

    fun nextUnit(): Unit {
        return Unit
    }

    fun nextNumber(): Number {
        return listOf(
            random.nextInt(),
            random.nextLong(),
            random.nextFloat(),
            random.nextDouble(),
            nextShort(),
            nextByte(),
        ).random(random)
    }

    private fun <T> T.orNull(): T? {
        return if (random.nextBoolean()) {
            this
        } else {
            null
        }
    }

    companion object {
        val default = defaultWith()
        fun defaultWith(
            random: Random = Random,
            collectionSizeRange: IntRange = 0..10,
            charRange: CharRange = 'A'..'z',
            stringSize: IntRange = 1..20,
            stringCandidates: List<Char> = charRange.toList(),
            candidateIndex: Int? = null
        ): RandomConfig {
            return RandomConfigImp(
                random = random,
                collectionSizeRange = collectionSizeRange,
                charRange = charRange,
                stringSize = stringSize,
                stringCandidates = stringCandidates,
                candidateIndex = null,
            )
        }
    }
}
