package com.x12q.randomizer.test.util

import java.io.ByteArrayOutputStream
import java.io.OutputStream

class ManyOutputStream(
    private val streams:List<OutputStream>
) : OutputStream() {

    constructor(vararg streams:OutputStream):this(streams.toList())

    override fun write(b: Int) {
        streams.forEach {
            it.write(b)
        }
    }
}
