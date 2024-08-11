package com.x12q.randomizer.ir_plugin.mock_objects

object NullRandomConfig : DefaultTestRandomConfig(){
    override fun nextBoolean(): Boolean {
        return false
    }

    override fun nextAnyOrNull(): Any? {
        return null
    }

    override fun nextUIntOrNull(): UInt? {
        return null
    }

    override fun nextIntOrNull(): Int? {
        return null
    }

    override fun nextBoolOrNull(): Boolean? {
        return null
    }

    override fun nextFloatOrNull(): Float? {
        return null
    }

    override fun nextULongOrNull(): ULong? {
        return null
    }

    override fun nextLongOrNull(): Long? {
        return null
    }

    override fun nextDoubleOrNull(): Double? {
        return null
    }

    override fun nextByteOrNull(): Byte? {
        return null
    }

    override fun nextUByteOrNull(): UByte? {
        return null
    }

    override fun nextCharOrNull(): Char? {
        return null
    }

    override fun nextShortOrNull(): Short? {
        return null
    }

    override fun nextUShortOrNull(): UShort? {
        return null
    }

    override fun nextStringUUIDOrNull(): String? {
        return null
    }

    override fun nextUnitOrNull(): Unit? {
        return null
    }

    override fun nextNumberOrNull(): Number? {
        return null
    }
}
