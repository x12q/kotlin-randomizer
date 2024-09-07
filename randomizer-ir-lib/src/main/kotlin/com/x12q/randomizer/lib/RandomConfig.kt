package com.x12q.randomizer.lib

import com.x12q.randomizer.lib.util.randomUUIDStr
import kotlin.random.Random
import kotlin.random.nextUBytes
import kotlin.random.nextULong

interface RandomConfig {

    val random: Random

    val collectionSizeRange: IntRange

    fun randomCollectionSize():Int{
        return collectionSizeRange.random(random)
    }

    fun nextAny():Any{
        return listOf(
            nextInt(),nextBoolean(),nextFloat(),nextLong(),nextDouble(),nextChar(),nextByte(),nextStringUUID(),nextUnit()
        ).random(random)
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



    fun nextULong():ULong{
        return random.nextULong()
    }


    fun nextByte(): Byte {
        return random.nextBytes(1)[0]
    }

    @OptIn(ExperimentalUnsignedTypes::class)
    fun nextUByte():UByte{
        return random.nextUBytes(1).first()
    }

    val charRange: CharRange

    fun nextChar(): Char {
        return charRange.random(this.random)
    }

    fun nextShort(): Short {
        return random.nextInt().toShort()
    }


    fun nextUShort():UShort{
        return nextShort().toUShort()
    }


    fun nextStringUUID(): String {
        return randomUUIDStr()
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

    private fun <T> T.orNull():T?{
        return if(random.nextBoolean()){
            this
        }else{
            null
        }
    }
}
