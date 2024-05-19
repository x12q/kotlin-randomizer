package com.x12q.randomizer.annotations.number._int

import com.x12q.randomizer.RDClassData
import com.x12q.randomizer.annotations.number._int.RandomIntFixed.Companion.makeClassRandomizer
import com.x12q.randomizer.annotations.number._int.RandomIntFixed.Companion.makeParamRandomizer
import com.x12q.randomizer.randomizer.ClassRandomizer
import com.x12q.randomizer.randomizer.ParameterRandomizer
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.every
import io.mockk.mockk
import kotlin.reflect.KParameter
import kotlin.test.Test

class RandomIntFixedTest {

    val kParam = mockk<KParameter>().also {
        every { it.name } returns ""
    }
    @Test
    fun factoryFunctions() {
        val v = 123
        RandomIntFixed(v).makeClassRandomizer().also {
            it.shouldBeInstanceOf<ClassRandomizer<Int>>()
            it.random() shouldBe v
        }
        RandomIntFixed(v).makeParamRandomizer().also {
            it.shouldBeInstanceOf<ParameterRandomizer<Int>>()
            it.random(RDClassData.from<Int>(),kParam,RDClassData.from<Any>()) shouldBe v
        }
    }

}
