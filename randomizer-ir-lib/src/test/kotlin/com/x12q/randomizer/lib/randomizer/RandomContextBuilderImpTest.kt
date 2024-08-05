package com.x12q.randomizer.lib.randomizer

import com.x12q.randomizer.lib.ConstantClassRandomizer
import com.x12q.randomizer.lib.RandomizerContextBuilderImp
import io.kotest.matchers.shouldBe
import kotlin.test.*

class RandomContextBuilderImpTest{


    @Test
    fun build(){
        val intRdm = ConstantClassRandomizer(1,Int::class)
        val floatRdm = ConstantClassRandomizer(2f,Float::class)
        val builder = RandomizerContextBuilderImp()

        builder.add(intRdm)
        builder.add(floatRdm)

        builder.buildContext().randomizersMap shouldBe listOf(intRdm,floatRdm).associateBy { it.returnType }
    }
}
