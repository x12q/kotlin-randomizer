package com.x12q.randomizer.test.util.assertions

import java.io.ByteArrayOutputStream
import java.io.OutputStream

abstract class TestOutputStream : OutputStream(){
    abstract fun getStr():String
    abstract fun getByteArrayOutputStream():ByteArrayOutputStream
}
