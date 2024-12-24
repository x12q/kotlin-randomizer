package com.x12q.kotlin.randomizer.lib.randomizer

import com.x12q.kotlin.randomizer.lib.*
import com.x12q.kotlin.randomizer.lib.RandomContextBuilderFunctions.double
import com.x12q.kotlin.randomizer.lib.RandomContextBuilderFunctions.int
import io.kotest.matchers.shouldBe
import kotlin.test.*

class RandomContextBuilderImpTest {

    lateinit var builder: RandomContextBuilder

    @BeforeTest
    fun bt() {
        builder = RandomContextBuilderImp()
    }

    @Test
    fun build() {
        val intRdm = ConstantRandomizer.of(1)
        val floatRdm = ConstantRandomizer.of(2f)
        val builder = RandomContextBuilderImp()

        builder.add(intRdm)
        builder.add(floatRdm)

        val context = builder.build()
        context.randomizersMap shouldBe listOf(intRdm, floatRdm).associateBy { it.returnType }
    }

    @Test
    fun addTier2Randomizer() {
        val intRdm = ConstantRandomizer.of(1)
        val builder = RandomContextBuilderImp()

        builder.add(intRdm)

        builder.addTier2Randomizer(makeRandomizer = {
            val tier1Context = this
            constantRandomizer(tier1Context.random<Int>()!!.toFloat())
        })


        val context = builder.build()
        context.random<Float>() shouldBe 1f
    }

    @Test
    fun `add factory randomizer`() {

        val intList = List(5) { 1 }
        val strList = List(3) { "qqq" }

        builder.add(factoryRandomizer { intList })
            .add(factoryRandomizer { strList })

        val context = builder.build()

        context.random<List<Int>>() shouldBe intList
        context.random<List<String>>() shouldBe strList
    }

    @Test
    fun `add int 1`() {
        builder.int(123)
        val c = builder.build()
        c.random<Int>() shouldBe 123
    }

    @Test
    fun `add int 2`() {
        builder.int { 999 }
        val ctx = builder.build()
        ctx.random<Int>() shouldBe 999
    }

    @Test
    fun `test RandomContextBuilder config functions`(){
        val bd = RandomContextBuilderImp()

        fun RandomContextBuilder.config(){
            int(123)
            val b= this
            double{ b.random<Int>().toDouble() }
        }
        bd.config()

        val ctx = bd.build()

        ctx.random<Int>() shouldBe 123
        ctx.random<Double>() shouldBe 123.0
    }
}
