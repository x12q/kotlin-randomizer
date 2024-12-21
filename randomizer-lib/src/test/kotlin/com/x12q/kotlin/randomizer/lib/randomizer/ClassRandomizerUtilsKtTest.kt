package com.x12q.kotlin.randomizer.lib.randomizer

import com.x12q.kotlin.randomizer.lib.*
import com.x12q.kotlin.randomizer.lib.random
import com.x12q.kotlin.randomizer.lib.test_utils.mock_obj.random_config.AlwaysTrueRandomConfig
import io.kotest.matchers.shouldBe
import kotlin.test.*

class ClassRandomizerUtilsKtTest {

    data class ABC(val i: Int)
    data class DDD(val abc: ABC)

    val floatRdm = ConstantRandomizer(2f, TypeKey.of<Float>())
    val strRdm = FactoryClassRandomizer({ "abc" }, TypeKey.of<String>())
    val abcRdm = FactoryClassRandomizer({ ABC(222) }, TypeKey.of<ABC>())
    val dddRdm = FactoryClassRandomizer({ DDD(ABC(8888)) }, TypeKey.of<DDD>())
    val listList = FactoryClassRandomizer.of<List<List<List<Int>>>> {
        List(1) { List(2) { List(3) { 123 } } }
    }
    val l = listOf(floatRdm, strRdm, abcRdm, dddRdm,listList)
    val col = MutableRandomizerCollection(l.associateBy { it.returnType })
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
        col.getRandomizer<List<List<List<Int>>>>() shouldBe listList
    }

    @Test
    fun `random from randomizer in collection`() {
        context.random<Float>() shouldBe floatRdm.random()
        context.random<String>() shouldBe strRdm.random()
        context.random<ABC>() shouldBe abcRdm.random()
        context.random<DDD>() shouldBe dddRdm.random()
        context.random<List<List<List<Int>>>>() shouldBe listList.random()
        z<List<List<List<Int>>>>()shouldBe listList.random()

    }

    inline fun <reified T:Any>z():T?{
        return context.random<T>()
    }

    @Test
    fun `random of primitive type that does not have a randomizer`() {
        context.random<Int>() shouldBe null
        context.random<List<Int>>() shouldBe null
        context.random<List<List<Int>>>() shouldBe null
    }
}
