package com.x12q.randomizer.lib.randomizer

import com.x12q.randomizer.lib.ConstantClassRandomizer
import com.x12q.randomizer.lib.RandomizerCollectionBuilderImp
import io.kotest.matchers.shouldBe
import kotlin.test.*

class ClassRandomizerCollectionBuilderImpTest{


    @Test
    fun build(){
        val intRdm = ConstantClassRandomizer(1)
        val floatRdm = ConstantClassRandomizer(2f)
        val builder = RandomizerCollectionBuilderImp()

        builder.add(intRdm)
        builder.add(floatRdm)

        builder.build().randomizersMap shouldBe listOf(intRdm,floatRdm).associateBy { it.returnType }
    }
}
