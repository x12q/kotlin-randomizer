package com.x12q.randomizer

import com.x12q.randomizer.util.randomUUIDStr
import kotlin.random.Random

interface RandomConfig {

    val random: Random

    val collectionSizeRange: IntRange

    fun nextInt(): Int {
        return random.nextInt()
    }

    fun nextIntOrNull(): Int? {
        return random.nextInt().orNull()
    }

    fun nextBoolOrNull(): Boolean? {
        return random.nextBoolean().orNull()
    }

    fun nextFloatOrNull(): Float? {
        return random.nextFloat().orNull()
    }

    fun nextLongOrNull(): Long? {
        return random.nextLong().orNull()
    }

    fun nextDoubleOrNull(): Double? {
        return random.nextDouble().orNull()
    }

    fun nextByte(): Byte {
        return random.nextBytes(1)[0]
    }

    fun nextByteOrNull():Byte?{
        return nextByte().orNull()
    }

    val charRange: CharRange

    fun nextChar(): Char {
        return charRange.random(this.random)
    }

    fun nextCharOrNull():Char?{
        return nextChar().orNull()
    }

    fun nextShort(): Short {
        return random.nextInt().toShort()
    }
    fun nextShortOrNull(): Short? {
        return nextShort().orNull()
    }

    fun nextStringUUID(): String {
        return randomUUIDStr()
    }

    fun nextStringUUIDOrNull(): String? {
        return nextStringUUID().orNull()
    }

    fun nextUnit(): Unit {
        return Unit
    }
    fun nextUnitOrNull(): Unit? {
        return Unit.orNull()
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

    fun nextNumberOrNull():Number?{
        return nextNumber().orNull()
    }

    private fun <T> T.orNull():T?{
        return if(random.nextBoolean()){
            this
        }else{
            null
        }
    }
}


