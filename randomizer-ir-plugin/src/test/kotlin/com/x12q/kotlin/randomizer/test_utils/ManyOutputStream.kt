package com.x12q.kotlin.randomizer.test_utils

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
