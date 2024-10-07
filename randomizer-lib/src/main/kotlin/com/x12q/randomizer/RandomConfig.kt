package com.x12q.randomizer

import com.x12q.randomizer.util.randomUUIDStr
import kotlin.random.Random
import kotlin.random.nextUBytes
import kotlin.random.nextULong

interface RandomConfig {

    val random: Random

    val collectionSizeRange: IntRange

    fun nextAny():Any{
        return listOf(
            nextInt(),nextBoolean(),nextFloat(),nextLong(),nextDouble(),nextChar(),nextByte(),nextStringUUID(),nextUnit()
        ).random(random)
    }

    fun nextAnyOrNull():Any?{
        return listOf(null,nextAny()).random(random)
    }

    fun nextFloat():Float{
        return random.nextFloat()
    }

    fun nextDouble():Double{
        return random.nextDouble()
    }

    fun nextBoolean():Boolean{
        return random.nextBoolean()
    }

    fun nextInt(): Int {
        return random.nextInt()
    }

    fun nextLong(): Long {
        return random.nextLong()
    }

    fun nextUInt():UInt{
        return random.nextInt().toUInt()
    }

    fun nextUIntOrNull():UInt?{
        return nextUInt().orNull()
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

    fun nextULong():ULong{
        return random.nextULong()
    }

    fun nextULongOrNull():ULong?{
        return nextULong().orNull()
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

    @OptIn(ExperimentalUnsignedTypes::class)
    fun nextUByte():UByte{
        return random.nextUBytes(1).first()
    }

    fun nextUByteOrNull():UByte?{
        return nextUByte().orNull()
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

    fun nextUShort():UShort{
        return nextShort().toUShort()
    }

    fun nextUShortOrNull():UShort?{
        return nextUShort().orNull()
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


