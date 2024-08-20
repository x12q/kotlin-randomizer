package com.x12q.randomizer.lib.randomizer

import com.x12q.randomizer.lib.ConstantClassRandomizer
import com.x12q.randomizer.lib.RandomContextBuilderImp
import io.kotest.matchers.shouldBe
import kotlin.test.*

class RandomContextBuilderImpTest{


    @Test
    fun build(){
        val intRdm = ConstantClassRandomizer(1,Int::class)
        val floatRdm = ConstantClassRandomizer(2f,Float::class)
        val builder = RandomContextBuilderImp()

        builder.add(intRdm)
        builder.add(floatRdm)

        val context = builder.buildContext()
        context.randomizersMap shouldBe listOf(intRdm,floatRdm).associateBy { it.returnType }

    }
}
