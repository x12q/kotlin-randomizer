package com.x12q.randomizer.lib

class RandomContextImp(
    val randomConfig: RandomConfig,
    val collection: RandomizerCollection,
) : RandomContext, RandomConfig by randomConfig, RandomizerCollection by collection {
    override fun nextInt(): Int {
        return this.random<Int>() ?: randomConfig.nextInt()
    }

    override fun nextAny(): Any {
        return this.random<Any>() ?: randomConfig.nextAny()
    }

    override fun nextAnyOrNull(): Any? {
        return this.randomOrNull<Any>(random) ?: randomConfig.nextAnyOrNull()
    }

    override fun nextFloat(): Float {
        return this.random<Float>() ?: randomConfig.nextFloat()
    }

    override fun nextDouble(): Double {
        return this.random<Double>() ?: randomConfig.nextDouble()
    }

    override fun nextBoolean(): Boolean {
        return this.random<Boolean>() ?: randomConfig.nextBoolean()
    }

    override fun nextLong(): Long {
        return this.random<Long>() ?: randomConfig.nextLong()
    }

    override fun nextIntOrNull(): Int? {
        return this.randomOrNull<Int>(random) ?: randomConfig.nextIntOrNull()
    }

    override fun nextBoolOrNull(): Boolean? {
        return this.randomOrNull<Boolean>(random) ?: randomConfig.nextBoolOrNull()
    }

    override fun nextFloatOrNull(): Float? {
        return this.randomOrNull<Float>(random) ?: randomConfig.nextFloatOrNull()
    }

    override fun nextULong(): ULong {
        return this.random<ULong>() ?: randomConfig.nextULong()
    }

    override fun nextULongOrNull(): ULong? {
        return this.randomOrNull<ULong>(random) ?: randomConfig.nextULongOrNull()
    }

    override fun nextLongOrNull(): Long? {
        return this.randomOrNull<Long>(random) ?: randomConfig.nextLongOrNull()
    }

    override fun nextDoubleOrNull(): Double? {
        return this.randomOrNull<Double>(random) ?: randomConfig.nextDoubleOrNull()
    }

    override fun nextByte(): Byte {
        return this.random<Byte>() ?: randomConfig.nextByte()
    }

    override fun nextByteOrNull(): Byte? {
        return this.randomOrNull<Byte>(random)?: randomConfig.nextByteOrNull()
    }

    override fun nextUByte(): UByte {
        return this.random<UByte>()?: randomConfig.nextUByte()
    }

    override fun nextUByteOrNull(): UByte? {
        return this.randomOrNull<UByte>(random)?: randomConfig.nextUByteOrNull()
    }


    override fun nextChar(): Char {
        return this.random<Char>()?: randomConfig.nextChar()
    }

    override fun nextCharOrNull(): Char? {
        return this.randomOrNull<Char>(random)?: randomConfig.nextCharOrNull()
    }

    override fun nextShort(): Short {
        return this.random<Short>()?: randomConfig.nextShort()
    }

    override fun nextShortOrNull(): Short? {
        return this.randomOrNull<Short>(random)?: randomConfig.nextShortOrNull()
    }

    override fun nextUShort(): UShort {
        return this.random<UShort>()?: randomConfig.nextUShort()
    }

    override fun nextUShortOrNull(): UShort? {
        return this.randomOrNull<UShort>(random)?: randomConfig.nextUShortOrNull()
    }

    override fun nextStringUUID(): String {
        return this.random<String>()?: randomConfig.nextStringUUID()
    }

    override fun nextStringUUIDOrNull(): String? {
        return this.randomOrNull<String>(random)?: randomConfig.nextStringUUIDOrNull()
    }

    override fun nextNumber(): Number {
        return this.random<Number>()?: randomConfig.nextNumber()
    }

    override fun nextNumberOrNull(): Number? {
        return this.randomOrNull<Number>(random)?: randomConfig.nextNumberOrNull()
    }
}
