package com.x12q.randomizer.lib.randomizer

import com.x12q.randomizer.lib.*
import com.x12q.randomizer.lib.randomizer.mock_obj.AlwaysTrueRandomConfig
import io.kotest.matchers.shouldBe
import kotlin.test.*

class ClassRandomizerUtilsKtTest {

    data class ABC(val i: Int)
    data class DDD(val abc: ABC)

    val floatRdm = ConstantClassRandomizer(2f,Float::class)
    val strRdm = FactoryClassRandomizer({ "abc" }, String::class)
    val abcRdm = FactoryClassRandomizer({ ABC(222) }, ABC::class)
    val dddRdm = FactoryClassRandomizer({ DDD(ABC(8888)) }, DDD::class)
    val l = listOf(floatRdm, strRdm, abcRdm, dddRdm)
    val col = RandomizerCollectionImp(l.associateBy { it.returnType })
    val config = AlwaysTrueRandomConfig
    val context = RandomContextImp(
        randomConfig = config,
        collection = col
    )

    @Test
    fun getRandomizer() {
        col.getRandomizer<Float>() shouldBe floatRdm
        col.getRandomizer<String>() shouldBe strRdm
        col.getRandomizer<ABC>() shouldBe abcRdm
        col.getRandomizer<DDD>() shouldBe dddRdm
    }

    @Test
    fun `random from randomizer in collection`() {
        context.random<Float>() shouldBe floatRdm.random()
        context.random<String>() shouldBe strRdm.random()
        context.random<ABC>() shouldBe abcRdm.random()
        context.random<DDD>() shouldBe dddRdm.random()
    }
    @Test
    fun `random of primitive type that does not have a randomizer`(){
        context.random<Int>() shouldBe config.nextInt()
    }
}
