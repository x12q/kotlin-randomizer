package com.x12q.randomizer.lib.randomizer

import io.kotest.matchers.shouldNotBe
import org.junit.jupiter.api.Test
import kotlin.reflect.KClass

class ClassRandomizerFactoryFunctionsTest {

    @Test
    fun factoryRandomizer_(){
        // val intListRandomizer = factoryRandomizer { List(2){1} }
        // val strListRandomizer = factoryRandomizer { List(2){"azc"} }

        // println(intListRandomizer.returnType)
        printKclass<List<Int>>() shouldNotBe printKclass<List<String>>()


    }

    inline fun <reified T:Any> printKclass():KClass<*> {
        return T::class
    }
}
