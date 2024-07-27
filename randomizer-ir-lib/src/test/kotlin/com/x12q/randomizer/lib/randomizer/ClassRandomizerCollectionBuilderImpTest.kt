package com.x12q.randomizer.lib.randomizer

import com.x12q.randomizer.lib.randomizer.ClassRandomizerUtilsKtTest.ABC
import com.x12q.randomizer.lib.randomizer.ClassRandomizerUtilsKtTest.DDD
import io.kotest.matchers.shouldBe
import kotlin.test.*

class ClassRandomizerCollectionBuilderImpTest{


    @Test
    fun build(){
        val intRdm = ConstantClassRandomizer(1)
        val floatRdm = ConstantClassRandomizer(2f)
        val builder = ClassRandomizerCollectionBuilderImp()

        builder.add(intRdm)
        builder.add(floatRdm)

        builder.build().randomizers shouldBe listOf(intRdm,floatRdm)
    }
}
