package com.x12q.randomizer.lib

class RandomContextImp(
    val randomConfig: RandomConfig,
    val collection: RandomizerCollection,
): RandomContext, RandomConfig by randomConfig, RandomizerCollection by collection {
    override fun nextInt(): Int {
        return this.random<Int>()?: randomConfig.nextInt()
    }

    override fun nextAny(): Any {
        return this.random<Any>()?: randomConfig.nextAny()
    }

    override fun nextAnyOrNull(): Any? {
        return this.randomOrNull<Any>(random) ?: randomConfig.nextAnyOrNull()
    }

    override fun nextFloat(): Float {
        return this.random<Float>()?: randomConfig.nextFloat()
    }

    override fun nextDouble(): Double {
        return this.random<Double>()?: randomConfig.nextDouble()
    }

    override fun nextBoolean(): Boolean {
        return super.nextBoolean()
    }

    override fun nextLong(): Long {
        return super.nextLong()
    }

    override fun nextUInt(): UInt {
        return super.nextUInt()
    }

    override fun nextUIntOrNull(): UInt? {
        return super.nextUIntOrNull()
    }

    override fun nextIntOrNull(): Int? {
        return super.nextIntOrNull()
    }

    override fun nextBoolOrNull(): Boolean? {
        return super.nextBoolOrNull()
    }

    override fun nextFloatOrNull(): Float? {
        return super.nextFloatOrNull()
    }

    override fun nextULong(): ULong {
        return super.nextULong()
    }

    override fun nextULongOrNull(): ULong? {
        return super.nextULongOrNull()
    }

    override fun nextLongOrNull(): Long? {
        return super.nextLongOrNull()
    }

    override fun nextDoubleOrNull(): Double? {
        return super.nextDoubleOrNull()
    }

    override fun nextByte(): Byte {
        return super.nextByte()
    }

    override fun nextByteOrNull(): Byte? {
        return super.nextByteOrNull()
    }

    override fun nextUByte(): UByte {
        return super.nextUByte()
    }

    override fun nextUByteOrNull(): UByte? {
        return super.nextUByteOrNull()
    }

    override val charRange: CharRange
        get() = TODO("Not yet implemented")

    override fun nextChar(): Char {
        return super.nextChar()
    }

    override fun nextCharOrNull(): Char? {
        return super.nextCharOrNull()
    }

    override fun nextShort(): Short {
        return super.nextShort()
    }

    override fun nextShortOrNull(): Short? {
        return super.nextShortOrNull()
    }

    override fun nextUShort(): UShort {
        return super.nextUShort()
    }

    override fun nextUShortOrNull(): UShort? {
        return super.nextUShortOrNull()
    }

    override fun nextStringUUID(): String {
        return super.nextStringUUID()
    }

    override fun nextStringUUIDOrNull(): String? {
        return super.nextStringUUIDOrNull()
    }

    override fun nextUnit() {
        super.nextUnit()
    }

    override fun nextUnitOrNull(): Unit? {
        return super.nextUnitOrNull()
    }

    override fun nextNumber(): Number {
        return super.nextNumber()
    }

    override fun nextNumberOrNull(): Number? {
        return super.nextNumberOrNull()
    }
}
