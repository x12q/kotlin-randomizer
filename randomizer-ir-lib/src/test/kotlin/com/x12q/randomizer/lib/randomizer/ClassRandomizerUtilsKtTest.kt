package com.x12q.randomizer.lib.randomizer

import io.kotest.matchers.shouldBe
import kotlin.test.*

class ClassRandomizerUtilsKtTest {

    data class ABC(val i: Int)
    data class DDD(val abc: ABC)

    val intRdm = ConstantClassRandomizer(1)
    val floatRdm = ConstantClassRandomizer(2f)
    val strRdm = FactoryClassRandomizer({ "abc" }, String::class)
    val abcRdm = FactoryClassRandomizer({ ABC(222) }, ABC::class)
    val dddRdm = FactoryClassRandomizer({ DDD(ABC(8888)) }, DDD::class)
    val l = listOf(intRdm, floatRdm, strRdm, abcRdm, dddRdm)
    val col = RandomizerCollectionImp(l)

    @Test
    fun getRandomizer() {
        col.getRandomizer<Int>() shouldBe intRdm
        col.getRandomizer<Float>() shouldBe floatRdm
        col.getRandomizer<String>() shouldBe strRdm
        col.getRandomizer<ABC>() shouldBe abcRdm
        col.getRandomizer<DDD>() shouldBe dddRdm
    }

    @Test
    fun random() {
        col.random<Int>() shouldBe intRdm.random()
        col.random<Float>() shouldBe floatRdm.random()
        col.random<String>() shouldBe strRdm.random()
        col.random<ABC>() shouldBe abcRdm.random()
        col.random<DDD>() shouldBe dddRdm.random()
    }
}
