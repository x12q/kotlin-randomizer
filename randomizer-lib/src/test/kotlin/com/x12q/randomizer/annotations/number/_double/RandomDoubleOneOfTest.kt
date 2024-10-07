package com.x12q.randomizer.annotations.number._double

import com.x12q.randomizer.annotations.number._double.RandomDoubleOneOf.Companion.makeClassRandomizer
import com.x12q.randomizer.randomizer.ClassRandomizer
import io.kotest.matchers.types.shouldBeInstanceOf
import org.junit.jupiter.api.Assertions.*
import kotlin.test.Test

class RandomDoubleOneOfTest{
    @Test
    fun qwe(){
        RandomDoubleOneOf(listOf(1.0,2.0).toDoubleArray()).makeClassRandomizer().also {
            it.shouldBeInstanceOf<ClassRandomizer<Double>>()
        }
    }
}
