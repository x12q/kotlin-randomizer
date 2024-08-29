package com.x12q.randomizer.lib.randomizer

import com.x12q.randomizer.lib.ConstantClassRandomizer
import com.x12q.randomizer.lib.RandomContextBuilderFunctions.constant
import com.x12q.randomizer.lib.RandomContextBuilderImp
import com.x12q.randomizer.lib.constantRandomizer
import com.x12q.randomizer.lib.random
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

    @Test
    fun addForTier2(){
        val intRdm = ConstantClassRandomizer(1,Int::class)
        val builder = RandomContextBuilderImp()


        builder.add(intRdm)

        builder.addForTier2 {
            val tier1Context = this
            constantRandomizer(tier1Context.random<Int>()!!.toFloat())
        }


        val context = builder.buildContext()
        context.random<Float>() shouldBe 1f
    }
}
