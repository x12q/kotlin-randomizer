package com.x12q.kotlin.randomizer.test_utils.assertions

import java.io.ByteArrayOutputStream
import java.io.OutputStream

abstract class TestOutputStream : OutputStream(){
    abstract fun getStr():String
    abstract fun getByteArrayOutputStream():ByteArrayOutputStream
}
