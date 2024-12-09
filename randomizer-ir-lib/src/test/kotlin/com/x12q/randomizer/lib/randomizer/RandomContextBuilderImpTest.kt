package com.x12q.randomizer.lib.randomizer

import com.x12q.randomizer.lib.*
import io.kotest.matchers.shouldBe
import kotlin.test.*

class RandomContextBuilderImpTest{


    @Test
    fun build(){
        val intRdm = ConstantRandomizer.of(1)
        val floatRdm = ConstantRandomizer.of(2f)
        val builder = RandomContextBuilderImp()

        builder.add(intRdm)
        builder.add(floatRdm)

        val context = builder.build()
        context.randomizersMap shouldBe listOf(intRdm,floatRdm).associateBy { it.returnType }
    }

    @Test
    fun addForTier2(){
        val intRdm = ConstantRandomizer.of(1)
        val builder = RandomContextBuilderImp()


        builder.add(intRdm)

        builder.addForTier2 {
            val tier1Context = this
            constantRandomizer(tier1Context.random<Int>()!!.toFloat())
        }


        val context = builder.build()
        context.random<Float>() shouldBe 1f
    }

    @Test
    fun qweqwe(){

        val builder = RandomContextBuilderImp()

        val intList = List(5){1}
        val strList = List(3){"qqq"}

        builder.add(factoryRandomizer { intList })
            .add(factoryRandomizer { strList })

        val context = builder.build()
        context.random<List<Int>>() shouldBe intList
    }
}
