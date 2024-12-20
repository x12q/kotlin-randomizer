package com.x12q.kotlin.randomizer.test.util.assertions

import java.io.ByteArrayOutputStream

class StringTestOutputStream(
    private val byteArrayOutputStream: ByteArrayOutputStream = ByteArrayOutputStream()
): TestOutputStream(){


    override fun write(b: Int) {
        byteArrayOutputStream.write(b)
    }

    override fun getStr():String{
        return String(byteArrayOutputStream.toByteArray())
    }

    override fun getByteArrayOutputStream(): ByteArrayOutputStream {
        return byteArrayOutputStream
    }

}
